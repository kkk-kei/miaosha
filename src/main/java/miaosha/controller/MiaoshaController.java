package miaosha.controller;

import miaosha.domain.MiaoshaOrder;
import miaosha.domain.MiaoshaUser;
import miaosha.domain.OrderInfo;
import miaosha.exception.GlobalException;
import miaosha.redis.OrderKey;
import miaosha.redis.RedisService;
import miaosha.result.CodeMsg;
import miaosha.result.Result;
import miaosha.service.GoodsService;
import miaosha.service.MiaoshaService;
import miaosha.service.OrderService;
import miaosha.vo.GoodsVO;
import miaosha.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @PostMapping("/do_miaosha")
    @ResponseBody
    public Result<OrderDetailVO> doMiaosha(MiaoshaUser user, @RequestParam("goodsID") Long goodsID){
        if(user==null){
            throw new GlobalException(CodeMsg.SESSION_ERROR);
        }
        //判断库存
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsID(goodsID);
        Integer stockCount = goodsVO.getStockCount();
        if(stockCount<=0){
            throw new GlobalException(CodeMsg.STOCK_EMPTY);
        }
        //重复秒杀
        MiaoshaOrder order = redisService.get(OrderKey.getByUidGid, "" + user.getId() + "_" + goodsVO.getId(), MiaoshaOrder.class);
        if(order!=null){
            throw new GlobalException(CodeMsg.REPEAT);
        }
        //秒杀
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVO);
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrderInfo(orderInfo);
        orderDetailVO.setGoodsVO(goodsVO);
        return Result.success(orderDetailVO);
    }

}
