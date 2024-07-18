package vip.xiaonuo.modular.weChatPay.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.core.annotion.Permission;
import vip.xiaonuo.modular.weChatPay.entity.weChatOrg;
import vip.xiaonuo.modular.weChatPay.service.WeChatPayService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Controller
@RequestMapping
@Slf4j
public class weChatPayController {
    @Resource
    private WeChatPayService weChatPayService;
	//预支付接口（生成二维码并输出到浏览器）
    @GetMapping("weChatPay/codePay" )
    public void codePay(@RequestParam("money")String money, HttpServletRequest request, HttpServletResponse response) throws Exception {
      weChatPayService.codePay(money,request,response);
    }
 
	//回调接口
    @PostMapping("weChatPay/wxnotify" )
    public void wxnotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        weChatPayService.wxnotify(request, response);
    }


}