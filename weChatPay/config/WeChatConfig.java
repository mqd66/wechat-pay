package vip.xiaonuo.modular.weChatPay.config;

public class WeChatConfig {

	/**
	* 微信服务号APPID
	*/
	public static String APPID="111111111111111111111";
	/**
	* 微信支付的商户号
	*/
	public static String MCHID="11111111111111111111";
	/**
	* 微信支付的API密钥
	*/
	public static String APIKEY="1111111111111111111111111";
	/**
	* 微信支付成功之后的回调地址【注意：当前回调地址必须是公网能够访问的地址】
      内网穿透去实现
	*/
	public static String WECHAT_NOTIFY_URL_PC="111111111111111111111";
//	public static String WECHAT_NOTIFY_URL_PC="http://hspay.hbhy.com.cn";
	/**
	* 微信统一下单API地址
	*/
	public static String UFDODER_URL="11111111111111111111111";
    /**
	* 应用对应的凭证
	*/
	public static String APP_SECRET="111111111111111111111";
}