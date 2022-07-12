package miaosha.rabbitmq;

import miaosha.util.ConvertUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendMiaoshaMessage(MiaoshaMessage message){
        String msg = ConvertUtil.beanToString(message);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
    }
}
