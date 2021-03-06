package miaosha.dao;

import miaosha.vo.GoodsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*,mg.miaosha_price,mg.stock_count, mg.start_date, mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVO> getGoodsVOList();

    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id where mg.goods_id = #{goodsID}")
    public GoodsVO getGoodsVOByGoodsID(@Param("goodsID")Long goodsID);

    @Update("update miaosha_goods set stock_count = stock_count-1 where goods_id = #{goodsID} and stock_count > 0")
    public int reduceStock(@Param("goodsID") Long goodsID);
}
