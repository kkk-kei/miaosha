package miaosha.controller;

import com.google.common.util.concurrent.RateLimiter;
import miaosha.access.AccessLimit;
import miaosha.access.NeedLogin;
import miaosha.domain.MiaoshaOrder;
import miaosha.domain.MiaoshaUser;
import miaosha.exception.GlobalException;
import miaosha.rabbitmq.MQSender;
import miaosha.rabbitmq.MiaoshaMessage;
import miaosha.result.CodeMsg;
import miaosha.result.Result;
import miaosha.service.GoodsService;
import miaosha.service.MiaoshaService;
import miaosha.service.OrderService;
import miaosha.vo.GoodsVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MQSender sender;

    private RateLimiter rateLimiter;

    private ExecutorService executorService;

    private HashMap<Long,Boolean> isOverMap = new HashMap<>();
    private HashMap<Long,Integer> originStockMap = new HashMap<>();

    @AccessLimit(seconds = 1,maxCount = 5)
    @NeedLogin(value = true)
    @RequestMapping("/path/{goodsID}")
    @ResponseBody
    public Result<String> getMiaoshaPath(MiaoshaUser user,
                                         @PathVariable("goodsID")Long goodsID){
        String path = miaoshaService.createMiaoshaPath(user.getId(),goodsID);
        if(StringUtils.isEmpty(path)){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        return Result.success(path);
    }

    @AccessLimit(seconds = 30,maxCount = 1)//单用户限流，限制重复请求
    @NeedLogin(value = true)
    @PostMapping("/{path}/do_miaosha")
    @ResponseBody
    public Result<CodeMsg> doMiaosha(MiaoshaUser user,
                                     @PathVariable("path")String path,
                                     @RequestParam("goodsID")Long goodsID){
        //令牌桶限流，限制有效请求
        boolean pass = rateLimiter.tryAcquire(1);
        if(!pass){
            return Result.error(CodeMsg.MIAOSHA_REFUSE);
        }
        Boolean check = miaoshaService.checkPath(path,user.getId(),goodsID);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记
        Boolean isOver = isOverMap.get(goodsID);
        if(isOver){
            return Result.error(CodeMsg.STOCK_EMPTY);
        }
        //判断是否重复秒杀
        //先判断重复秒杀，防止同一用户多次预减库存
        MiaoshaOrder order = orderService.getMiaoOrderByUidGid(user.getId(),goodsID);
        if(order!=null){
            return Result.error(CodeMsg.MIAOSHA_REPEAT);
        }
        //redis预减库存
        Long stock = goodsService.reduceRedisStock(goodsID);
        if(stock<0){
            isOverMap.put(goodsID,true);
            goodsService.increaseRedisStock(goodsID);
            return Result.error(CodeMsg.STOCK_EMPTY);
        }
        //队列泄洪、入队异步下单
        Future<Object> future = executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                MiaoshaMessage message = new MiaoshaMessage();
                message.setMiaoshaUser(user);
                message.setGoodsID(goodsID);
                sender.sendMiaoshaMessage(message);
                return null;
            }
        });
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        return Result.success(CodeMsg.MIAOSHA_WAITING);
    }

    @NeedLogin(value = true)
    @RequestMapping("/result/{goodsID}")
    @ResponseBody
    public Result<CodeMsg> getResult(MiaoshaUser user, @PathVariable("goodsID") Long goodsID){
        if(user==null){
            throw new GlobalException(CodeMsg.SESSION_ERROR);
        }
        return miaoshaService.getMiaoshaResult(user.getId(),goodsID);
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化商品库存
        List<GoodsVO> goodsVOList = goodsService.getGoodsVOList();
        if(goodsVOList==null){
            return;
        }
        for (GoodsVO goodsVO : goodsVOList) {
            goodsService.addRedisStock(goodsVO.getId(),goodsVO.getStockCount());
            if (goodsVO.getGoodsStock()<=0) {
                isOverMap.put(goodsVO.getId(),true);
            }else{
                originStockMap.put(goodsVO.getId(),goodsVO.getStockCount()*3);
                isOverMap.put(goodsVO.getId(),false);
            }
        }
        //初始化令牌桶
        rateLimiter = RateLimiter.create(100);
        //初始化线程池
        executorService = Executors.newFixedThreadPool(20);
    }
}
