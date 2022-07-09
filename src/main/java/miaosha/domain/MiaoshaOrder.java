package miaosha.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MiaoshaOrder {
	private Long id;
	private Long userID;
	private Long orderID;
	private Long goodsID;
}
