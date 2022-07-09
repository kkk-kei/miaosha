package miaosha.service;

import miaosha.dao.GoodsDao;
import miaosha.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVO> getGoodsVOList() {
        return goodsDao.getGoodsVOList();
    }

    public GoodsVO getGoodsVOByGoodsID(Long goodsID) {
        return goodsDao.getGoodsVOByGoodsID(goodsID);
    }

    public void reduceStock(Long goodsID) {
        goodsDao.reduceStock(goodsID);
    }
}
