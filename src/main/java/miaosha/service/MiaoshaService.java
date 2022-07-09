package miaosha.service;

import miaosha.domain.MiaoshaUser;
import miaosha.domain.OrderInfo;
import miaosha.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    OrderService orderService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVO goodsVO) {
        //减少库存 生成订单
        goodsService.reduceStock(goodsVO.getId());
        return orderService.createOrder(user,goodsVO);
    }
}
