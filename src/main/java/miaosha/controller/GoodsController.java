package miaosha.controller;

import miaosha.access.AccessLimit;
import miaosha.domain.MiaoshaUser;
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

    @RequestMapping("/list")
    @ResponseBody
    public Result<List<GoodsVO>> getList(){
        List<GoodsVO> goodsVOList = goodsService.getGoodsVOList();
        return Result.success(goodsVOList);
    }

    @AccessLimit(seconds = 1,maxCount = 5)
    @RequestMapping("/detail/{goodsID}")
    @ResponseBody
    public Result<GoodsDetailVO> getDetail(MiaoshaUser miaoshaUser,
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
