package miaosha.controller;

import miaosha.domain.MiaoshaUser;
import miaosha.domain.OrderInfo;
import miaosha.exception.GlobalException;
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

    @RequestMapping("/detail/{orderID}")
    @ResponseBody
    public Result<OrderDetailVO> orderDetail(MiaoshaUser user,
                                             @PathVariable("orderID")Long orderID){
        //用户只能查看自己的订单
        if(user==null){
            throw new GlobalException(CodeMsg.SESSION_ERROR);
        }
        OrderInfo orderInfo = orderService.getOrderByOrderID(orderID);
        if(orderInfo==null){
            throw new GlobalException(CodeMsg.ORDER_NOT_EXIST);
        }
        if(!String.valueOf(user.getId()).equals(String.valueOf(orderInfo.getUserID()))){
            throw new GlobalException(CodeMsg.ORDER_REFUSED_CHECK);
        }
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsID(orderInfo.getGoodsID());
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrderInfo(orderInfo);
        orderDetailVO.setGoodsVO(goodsVO);
        return Result.success(orderDetailVO);
    }
}
