package miaosha.controller;

import miaosha.domain.MiaoshaUser;
import miaosha.service.GoodsService;
import miaosha.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/to_list")
    public String toList(Model model,MiaoshaUser user){
        model.addAttribute("user",user);
        List<GoodsVO> goodsList = goodsService.getGoodsVOList();
        model.addAttribute("goodsList",goodsList);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsID}")
    public String toDetail(Model model, MiaoshaUser user,
                           @PathVariable("goodsID")Long goodsID){
        model.addAttribute("user",user);

        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsID(goodsID);
        model.addAttribute("goods",goodsVO);

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
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        return "goods_detail";
    }

}
