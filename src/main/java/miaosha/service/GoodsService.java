package miaosha.service;

import miaosha.dao.GoodsDao;
import miaosha.redis.RedisService;
import miaosha.redis.key.GoodsKey;
import miaosha.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    @Autowired
    RedisService redisService;

    public List<GoodsVO> getGoodsVOList() {
        List<GoodsVO> goodsVOList = redisService.get(GoodsKey.getGoodsList, "", List.class);
        if(goodsVOList==null){
            goodsVOList = goodsDao.getGoodsVOList();
            if(goodsVOList==null){
                return goodsVOList;
            }
            redisService.set(GoodsKey.getGoodsList,"",goodsVOList);
        }
        return goodsVOList;
    }

    public GoodsVO getGoodsVOByGoodsID(Long goodsID) {
        if(goodsID == null || goodsID <= 0){
            return null;
        }
        GoodsVO goodsVO = redisService.get(GoodsKey.getGoodsDetailByGid, "" + goodsID, GoodsVO.class);
        if(goodsVO==null){
            goodsVO = goodsDao.getGoodsVOByGoodsID(goodsID);
            if(goodsVO==null){
                return goodsVO;
            }
            redisService.set(GoodsKey.getGoodsDetailByGid,""+goodsID,goodsVO);
        }
        return goodsVO;
    }

    public boolean reduceDBStock(Long goodsID) {
        int res = goodsDao.reduceStock(goodsID);
        return res>0;
    }

    public Long reduceRedisStock(Long goodsID) {
        return redisService.decr(GoodsKey.getGoodsStockByGid, "" + goodsID);
    }

    public void addRedisStock(Long goodsID, Integer stockCount) {
        redisService.set(GoodsKey.getGoodsStockByGid,""+goodsID,stockCount);
    }
}
