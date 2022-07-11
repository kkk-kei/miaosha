package miaosha.controller;

import miaosha.domain.MiaoshaUser;
import miaosha.redis.GoodsKey;
import miaosha.redis.RedisService;
import miaosha.result.Result;
import miaosha.service.GoodsService;
import miaosha.vo.GoodsDetailVO;
import miaosha.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/list")
    @ResponseBody
    public Result<List<GoodsVO>> toList(){
//        List<GoodsVO> goodsList = new ArrayList<>();
        List<GoodsVO> goodsList = redisService.get(GoodsKey.getGoodsList, "",List.class);
        if(goodsList==null){
            goodsList = goodsService.getGoodsVOList();
            redisService.set(GoodsKey.getGoodsList,"",goodsList);
        }
        return Result.success(goodsList);
    }

    @RequestMapping("/detail/{goodsID}")
    @ResponseBody
    public Result<GoodsDetailVO> toDetail(MiaoshaUser miaoshaUser,
                                          @PathVariable("goodsID")Long goodsID){
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsID(goodsID);
        long startAt = goodsVO.getStartDate().getTime();
        long endAt = goodsVO.getEndDate().getTime();
        long current = System.currentTimeMillis();
        int remainSeconds = 0;
        int miaoshaStatus = 0;
        if(current<startAt){//秒杀未开始
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt-current)/1000);
        }else if(current>endAt){//秒杀已结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else{//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVO goodsDetailVO = new GoodsDetailVO();
        goodsDetailVO.setGoodsVO(goodsVO);
        goodsDetailVO.setMiaoshaUser(miaoshaUser);
        goodsDetailVO.setRemainSeconds(remainSeconds);
        goodsDetailVO.setMiaoshaStatus(miaoshaStatus);
        return Result.success(goodsDetailVO);
    }

}
