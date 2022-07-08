package miaosha.controller;

import miaosha.domain.MiaoshaUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @RequestMapping("/to_list")
    public String toList(MiaoshaUser user, Model model){
        //根据cookie获取用户并返回
        model.addAttribute("user",user);
        return "goods_list";
    }
}
