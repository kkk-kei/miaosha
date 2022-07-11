package miaosha.vo;

import lombok.Getter;
import lombok.Setter;
import miaosha.domain.OrderInfo;

@Getter
@Setter
public class OrderDetailVO {
    private GoodsVO goodsVO;
    private OrderInfo orderInfo;
}
