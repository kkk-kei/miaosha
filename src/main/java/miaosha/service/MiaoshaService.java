package miaosha.service;

import miaosha.domain.MiaoshaOrder;
import miaosha.domain.MiaoshaUser;
import miaosha.redis.MiaoshaKey;
import miaosha.redis.OrderKey;
import miaosha.redis.RedisService;
import miaosha.result.CodeMsg;
import miaosha.result.Result;
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

    @Autowired
    RedisService redisService;

    @Transactional
    public void miaosha(MiaoshaUser user, GoodsVO goodsVO) {
        //减少库存 生成订单
        boolean success = goodsService.reduceStock(goodsVO.getId());
        if(success){
            orderService.createOrder(user,goodsVO);
        }else{
            redisService.set(MiaoshaKey.isStockEmptyByGid,""+goodsVO.getId(),true);
        }
    }

    public Result<CodeMsg> getMiaoshaResult(Long userID, Long goodsID) {
        MiaoshaOrder order = redisService.get(OrderKey.getByUidGid, "" + userID + "_" + goodsID, MiaoshaOrder.class);
        if(order!=null){
            return Result.success(CodeMsg.MIAOSHA_SUCCESS.fillArgs(String.valueOf(order.getOrderID())));
        }else{
            boolean isOver = redisService.exists(MiaoshaKey.isStockEmptyByGid,""+goodsID);
            if(isOver){
                return Result.error(CodeMsg.STOCK_EMPTY);
            }else{
                return Result.success(CodeMsg.MIAOSHA_WAITING);
            }
        }
    }
}
