package miaosha.vo;

import lombok.Getter;
import lombok.Setter;
import miaosha.domain.Goods;

import java.util.Date;

@Getter
@Setter
public class GoodsVO extends Goods {
	private Double miaoshaPrice;
	private Integer stockCount;
	private Date startDate;
	private Date endDate;
}
