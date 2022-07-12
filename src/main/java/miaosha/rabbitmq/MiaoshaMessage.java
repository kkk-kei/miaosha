package miaosha.rabbitmq;

import lombok.Getter;
import lombok.Setter;
import miaosha.domain.MiaoshaUser;

@Getter
@Setter
public class MiaoshaMessage {
    private MiaoshaUser miaoshaUser;
    private long goodsID;
}
