package miaosha.service;

import miaosha.dao.OrderDao;
import miaosha.domain.MiaoshaOrder;
import miaosha.domain.MiaoshaUser;
import miaosha.domain.OrderInfo;
import miaosha.redis.OrderKey;
import miaosha.redis.RedisService;
import miaosha.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

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
        orderDao.insertOrderInfo(orderInfo);
        //生成秒杀订单
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setUserID(user.getId());
        miaoshaOrder.setOrderID(orderInfo.getId());
        miaoshaOrder.setGoodsID(goodsVO.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        redisService.set(OrderKey.getByUidGid, "" + user.getId() + "_" + goodsVO.getId(), miaoshaOrder);
        return orderInfo;
    }

    public OrderInfo getOrderByOrderID(Long orderID) {
        return orderDao.getOrderByOrderID(orderID);
    }
}
