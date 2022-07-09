package miaosha.controller;

import miaosha.domain.MiaoshaOrder;
import miaosha.domain.MiaoshaUser;
import miaosha.domain.OrderInfo;
import miaosha.result.CodeMsg;
import miaosha.service.GoodsService;
import miaosha.service.MiaoshaService;
import miaosha.service.OrderService;
import miaosha.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @RequestMapping("/do_miaosha")
    public String doMiaosha(Model model, MiaoshaUser user,
                            @RequestParam("goodsID") Long goodsID){
        model.addAttribute("user",user);
        if(user==null){
            return "login";
        }
        //判断库存
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsID(goodsID);
        Integer stockCount = goodsVO.getStockCount();
        if(stockCount<=0){
            model.addAttribute("errMsg", CodeMsg.STOCK_EMPTY.getMsg());
            return "miaosha_fail";
        }
        //重复秒杀
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIDGoodsID(user.getId(),goodsVO.getId());
        if(order!=null){
            model.addAttribute("errMsg", CodeMsg.REPEAT.getMsg());
            return "miaosha_fail";
        }
        //秒杀
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVO);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goodsVO);
        return"order_detail";
    }

}
