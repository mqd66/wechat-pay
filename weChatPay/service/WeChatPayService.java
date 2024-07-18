package vip.xiaonuo.modular.weChatPay.service;

import vip.xiaonuo.modular.weChatPay.entity.weChatOrg;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WeChatPayService {
    void codePay(String money,HttpServletRequest request, HttpServletResponse response);

    void wxnotify(HttpServletRequest request, HttpServletResponse response);

}
