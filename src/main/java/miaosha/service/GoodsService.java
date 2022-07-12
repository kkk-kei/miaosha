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

    public boolean reduceStock(Long goodsID) {
        int res = goodsDao.reduceStock(goodsID);
        return res>0;
    }
}
