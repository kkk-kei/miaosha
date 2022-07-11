package miaosha.vo;


import lombok.Getter;
import lombok.Setter;
import miaosha.domain.MiaoshaUser;

@Getter
@Setter
public class GoodsDetailVO {

    private GoodsVO goodsVO;
    private MiaoshaUser miaoshaUser;
    private int remainSeconds;
    private int miaoshaStatus;

}
