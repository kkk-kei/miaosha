package miaosha.dao;

import miaosha.domain.MiaoshaOrder;
import miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id=#{userID} and goods_id=#{goodsID}")
    public MiaoshaOrder getMiaoshaOrderByUserIDGoodsID(@Param("userID") Long userID, @Param("goodsID") Long goodsID);

    @Insert("insert into order_info (user_id,goods_id,goods_name,goods_count,goods_price,order_channel,status,create_date)" +
            "values(#{userID},#{goodsID},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    @SelectKey(statement = "select last_insert_id()", keyColumn = "id",keyProperty = "id", before = false, resultType = long.class)
    long insertOrderInfo(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id,order_id,goods_id) values(#{userID},#{orderID},#{goodsID})")
    void insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id = #{orderID}")
    OrderInfo getOrderByOrderID(@Param("orderID") Long orderID);
}
