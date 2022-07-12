package miaosha.rabbitmq;

import miaosha.domain.MiaoshaOrder;
import miaosha.domain.MiaoshaUser;
import miaosha.redis.OrderKey;
import miaosha.redis.RedisService;
import miaosha.service.GoodsService;
import miaosha.service.MiaoshaService;
import miaosha.util.ConvertUtil;
import miaosha.vo.GoodsVO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receiveMiaoshaMessage(String message){
        MiaoshaMessage msg = ConvertUtil.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = msg.getMiaoshaUser();
        long goodsID = msg.getGoodsID();
        //判断重复秒杀
        MiaoshaOrder order = redisService.get(OrderKey.getByUidGid, "" + user.getId() + "_" + goodsID, MiaoshaOrder.class);
        if(order!=null){
            return;
        }
        //判断库存
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsID(goodsID);
        int stockCount = goodsVO.getStockCount();
        if(stockCount<=0){
            return;
        }
        //秒杀
        miaoshaService.miaosha(user,goodsVO);
    }
}
