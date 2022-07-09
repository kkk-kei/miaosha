package miaosha.service;

import miaosha.dao.OrderDao;
import miaosha.domain.MiaoshaOrder;
import miaosha.domain.MiaoshaUser;
import miaosha.domain.OrderInfo;
import miaosha.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    public MiaoshaOrder getMiaoshaOrderByUserIDGoodsID(Long userID, Long goodsID) {
        return orderDao.getMiaoshaOrderByUserIDGoodsID(userID,goodsID);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVO goodsVO) {
        //生成订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserID(user.getId());
        orderInfo.setGoodsID(goodsVO.getId());
        orderInfo.setDeliveryAddrID(0L);
        orderInfo.setGoodsName(goodsVO.getGoodsName());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsPrice(goodsVO.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setCreateDate(new Date());
        long orderID = orderDao.insertOrderInfo(orderInfo);
        //生成秒杀订单
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setUserID(user.getId());
        miaoshaOrder.setOrderID(orderID);
        miaoshaOrder.setGoodsID(goodsVO.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        return orderInfo;
    }
}
