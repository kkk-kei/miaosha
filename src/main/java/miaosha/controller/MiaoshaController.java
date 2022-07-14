package miaosha.controller;

import miaosha.access.AccessLimit;
import miaosha.access.Filter;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

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

    private HashMap<Long,Boolean> isOverMap = new HashMap<>();


    @AccessLimit(seconds = 1,maxCount = 5)
    @NeedLogin(value = true)
    @RequestMapping("/path/{goodsID}")
    @ResponseBody
    public Result<String> getMiaoshaPath(MiaoshaUser user,
                                         @PathVariable("goodsID")Long goodsID){
        String path = miaoshaService.createMiaoshaPath(user.getId(),goodsID);
        return Result.success(path);
    }

    //限流30s只能访问一次，即一个动态url只能秒杀一次且有效期为30s
    @AccessLimit(seconds = 30,maxCount = 1)
    @NeedLogin(value = true)
    @Filter
    @PostMapping("/{path}/do_miaosha")
    @ResponseBody
    public Result<CodeMsg> doMiaosha(MiaoshaUser user,
                                     @PathVariable("path")String path,
                                     @RequestParam("goodsID") Long goodsID){
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
        //MiaoshaOrder order = redisService.get(OrderKey.getByUidGid, "" + user.getId() + "_" + goodsID, MiaoshaOrder.class);
        MiaoshaOrder order = orderService.getMiaoOrderByUidGid(user.getId(),goodsID);
        if(order!=null){
            return Result.error(CodeMsg.MIAOSHA_REPEAT);
        }
        //redis预减库存
        //Long stock = redisService.decr(GoodsKey.getGoodsStock, "" + goodsID);
        Long stock = goodsService.reduceRedisStock(goodsID);
        if(stock<0){
            isOverMap.put(goodsID,true);
            goodsService.increaseRedisStock(goodsID);
            throw new GlobalException(CodeMsg.STOCK_EMPTY);
        }
        //入队,异步下单
        MiaoshaMessage message = new MiaoshaMessage();
        message.setMiaoshaUser(user);
        message.setGoodsID(goodsID);
        sender.sendMiaoshaMessage(message);
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
            //redisService.set(GoodsKey.getGoodsStock,""+goodsVO.getId(),goodsVO.getStockCount());
            goodsService.addRedisStock(goodsVO.getId(),goodsVO.getStockCount());
            if (goodsVO.getGoodsStock()<=0) {
                isOverMap.put(goodsVO.getId(),true);
            }
            isOverMap.put(goodsVO.getId(),false);
        }
    }
}
