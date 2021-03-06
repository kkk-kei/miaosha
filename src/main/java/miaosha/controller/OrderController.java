package miaosha.controller;

import miaosha.access.NeedLogin;
import miaosha.domain.MiaoshaUser;
import miaosha.domain.OrderInfo;
import miaosha.result.CodeMsg;
import miaosha.result.Result;
import miaosha.service.GoodsService;
import miaosha.service.OrderService;
import miaosha.vo.GoodsVO;
import miaosha.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @NeedLogin(value = true)
    @RequestMapping("/detail/{orderID}")
    @ResponseBody
    public Result<OrderDetailVO> getOrderDetail(MiaoshaUser user,
                                             @PathVariable("orderID")Long orderID){
        //用户只能查看自己的订单
        OrderInfo orderInfo = orderService.getOrderInfoByOrderID(orderID);
        if(orderInfo==null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        if(!String.valueOf(user.getId()).equals(String.valueOf(orderInfo.getUserID()))){
            return Result.error(CodeMsg.ORDER_REFUSED_CHECK);
        }
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsID(orderInfo.getGoodsID());
        if(goodsVO==null){
            return Result.error(CodeMsg.GOODS_NOT_EXIST);
        }
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrderInfo(orderInfo);
        orderDetailVO.setGoodsVO(goodsVO);
        return Result.success(orderDetailVO);
    }
}
