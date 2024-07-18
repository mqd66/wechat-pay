package vip.xiaonuo.modular.weChatPay.service.impl;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.http.HttpUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import vip.xiaonuo.modular.sqlcurrency.mapper.SqlMapper;
import vip.xiaonuo.modular.weChatPay.config.WeChatConfig;
import vip.xiaonuo.modular.weChatPay.entity.weChatOrg;
import vip.xiaonuo.modular.weChatPay.service.WeChatPayService;
import vip.xiaonuo.modular.weChatPay.util.PayForUtil;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class WeChatPayServiceImpl implements WeChatPayService {
    @Resource
    private SqlMapper sqlMapper;
    @Override
    public void codePay(String money,HttpServletRequest request, HttpServletResponse response) {
        // 支付的商品名称
        String body="嘴巴香大辣条";
        //支付金额（单位：分）
        if("".equals(money)){
            throw new RuntimeException("出错了");
        }
//       这里转换为分
        int amountInCents = (int) (Double.parseDouble(money) * 100);
        String amountInCentsStr = String.valueOf(amountInCents);
        //获取二维码内容urlCode
        String resXml = null;
        try {
            resXml = getNative( body, amountInCentsStr);
            Map<String, Object> data= XmlUtil.xmlToMap(resXml);
            String urlCode = data.get("code_url").toString();
            //生成二维码到输出流
            response.setContentType("image/jpeg");
            ServletOutputStream out = response.getOutputStream();
            QrCodeUtil.generate(urlCode, 300, 300,"jpg" ,out);
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @Description 支付回调
     */
    @Override
    public void wxnotify(HttpServletRequest request, HttpServletResponse response) {
        // 读取回调数据
        InputStream inputStream;
        StringBuffer sb = new StringBuffer();
        try {
            inputStream = request.getInputStream();
            String s;
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((s = in.readLine()) != null) {
                sb.append(s);
            }
            in.close();
            inputStream.close();
            // 解析xml成map
            Map<String, Object> m=XmlUtil.xmlToMap(sb.toString());
            // 过滤空 设置 TreeMap
            SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
            Iterator<String> it = m.keySet().iterator();
            while (it.hasNext()) {
                String parameter = it.next();
                Object parameterValue = m.get(parameter);
                String v = "";
                if (null != parameterValue) {
                    v = parameterValue.toString().trim();
                }
                packageParams.put(parameter, v);
            }
            // 微信支付的API密钥
            String key = WeChatConfig.APIKEY;
            // 判断签名是否正确
            if (PayForUtil.isTenpaySign(packageParams, key)) {
                String resXml = "";
                if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
                    System.err.println("--------------------------------------------");
                    System.err.println("支付回调成功。。。可在此处执行业务逻辑。。。");
                    System.err.println("--------------------------------------------");
                    // 支付成功,执行自己的业务逻辑开始
                    String app_id = (String) packageParams.get("appid");
                    String mch_id = (String) packageParams.get("mch_id");
                    String openid = (String) packageParams.get("openid");
                    // 是否关注公众号
                    String is_subscribe = (String) packageParams.get("is_subscribe");
                    // 附加参数【商标申请_0bda32824db44d6f9611f1047829fa3b_15460】--【业务类型_会员ID_订单号】
                    String attach = (String) packageParams.get("attach");
                    String out_trade_no = (String) packageParams.get("out_trade_no");
                    String total_fee = (String) packageParams.get("total_fee");
                    // 微信支付订单号
                    String transaction_id = (String) packageParams.get("transaction_id");
                    // 支付完成时间
                    String time_end = (String) packageParams.get("time_end");
                    System.out.println(packageParams);
                    // 通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                } else {
                    System.err.println("支付失败,错误信息：" + packageParams.get("err_code"));

                    resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                            + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                }
                response.getWriter().write(resXml);
            } else {
                System.err.println("通知签名验证失败");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * @Description NATIVE支付模式（二维码网页扫码支付）
     */
    public static String getNative(String body,String totalFee) throws Exception {
        //账号信息
        String appid = WeChatConfig.APPID;
        String mch_id = WeChatConfig.MCHID;
        String key = WeChatConfig.APIKEY;

        //微信支付成功之后的回调地址【注意：当前回调地址必须是公网能够访问的地址】
        String notify_url = WeChatConfig.WECHAT_NOTIFY_URL_PC;
        String ufdoder_url = WeChatConfig.UFDODER_URL;
        String trade_type = "NATIVE";
        String out_trade_no = PayForUtil.getOrderNo();

        //请求参数封装
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        packageParams.put("appid", appid);
        packageParams.put("mch_id", mch_id);
        // 随机字符串
        packageParams.put("nonce_str", "nonce"+out_trade_no);
        // 支付的商品名称
        packageParams.put("body", body);
        // 商户订单号【备注：每次发起请求都需要随机的字符串，否则失败。】
        packageParams.put("out_trade_no",out_trade_no);
        // 支付金额
        packageParams.put("total_fee", totalFee);
        // 客户端主机
        packageParams.put("spbill_create_ip", PayForUtil.localIp());
        packageParams.put("notify_url", notify_url);
        packageParams.put("trade_type", trade_type);
        // 获取签名
        String sign = PayForUtil.createSign(packageParams, key);
        packageParams.put("sign", sign);
        // 将请求参数转换成String类型
        String requestXML = XmlUtil.mapToXmlStr(packageParams, "xml");
        // 解析请求之后的xml参数并且转换成String类型
        String resXml= HttpUtil.post(ufdoder_url, requestXML);
        return resXml;
    }
}
