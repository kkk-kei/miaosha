package miaosha.service;

import miaosha.domain.MiaoshaOrder;
import miaosha.domain.MiaoshaUser;
import miaosha.redis.key.MiaoshaKey;
import miaosha.redis.key.OrderKey;
import miaosha.redis.RedisService;
import miaosha.result.CodeMsg;
import miaosha.result.Result;
import miaosha.util.MD5Util;
import miaosha.util.UUIDUtil;
import miaosha.vo.GoodsVO;
import org.apache.commons.lang3.StringUtils;
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
        boolean success = goodsService.reduceDBStock(goodsVO.getId());
        if(success){
            orderService.createOrder(user,goodsVO);
        }else{
            redisService.set(MiaoshaKey.isStockEmptyByGid,""+goodsVO.getId(),true);
        }
    }

    public Result<CodeMsg> getMiaoshaResult(Long userID, Long goodsID) {
        MiaoshaOrder order = redisService.get(OrderKey.getMiaoshaOrderByUidGid, "" + userID + "_" + goodsID, MiaoshaOrder.class);
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

    public String createMiaoshaPath(Long userID, Long goodsID) {
        String path = MD5Util.md5(UUIDUtil.uuid() + MD5Util.salt);
        redisService.set(MiaoshaKey.getMiaoshaPathByUidGid,""+userID+"_"+goodsID,path);
        return path;
    }


    public Boolean checkPath(String path, Long userID, Long goodsID) {
        String oldPath = redisService.get(MiaoshaKey.getMiaoshaPathByUidGid, "" + userID + "_" + goodsID, String.class);
        if(StringUtils.isEmpty(oldPath)){
            return false;
        }
        if(oldPath.equals(path)) {
            redisService.delete(MiaoshaKey.getMiaoshaPathByUidGid,""+userID+""+goodsID);
            return true;
        }
        return false;
    }
}
