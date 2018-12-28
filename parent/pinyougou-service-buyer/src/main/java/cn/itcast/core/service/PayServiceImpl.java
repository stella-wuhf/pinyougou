 package cn.itcast.core.service;

import cn.itcast.common.utils.HttpClient;
import cn.itcast.core.pojo.log.PayLog;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付管理
 */
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${appid}")
    private String appid; //微信公众账号或开放平台APP的唯一标识
    @Value("${partner}")
    private String partner; //商户号  (配置文件中的partner)
    @Value("${partnerkey}")
    private String partnerkey; //商户密钥


    //生成二维码的value值
    //连接微信服务器,发出请求
    @Override
    public Map<String, String> createNative(String name) {
        Map<String, String> param = new HashMap<>();
        //从缓存中获取到payLog
        PayLog payLog = (PayLog) redisTemplate.boundHashOps("payLog").get(name);
        //按照微信接口设置参数
        param.put("out_trade_no", payLog.getOutTradeNo());//支付定单号,商户订单号	out_trade_no
        //map.put("total_fee", String.valueOf(payLog.getTotalFee())); //支付总金额,标价金额
        param.put("total_fee", "1");
        param.put("appid", appid);//公众账号ID	appid
        param.put("mch_id", partner);//商户号	mch_id
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串	nonce_str    随机数生成算法
        //签名	sign  转换成xml时会自动签名
        param.put("body", "品优购商城"); //商品描述	body
        param.put("spbill_create_ip", "127.0.0.1");//IP
        param.put("notify_url", "http://www.itcast.cn");//回调地址(随便写)
        param.put("trade_type", "NATIVE");//交易类型

        try {
            //生成要发送的xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //统一下单的网址
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            HttpClient httpClient = new HttpClient(url);
            //设置https
            httpClient.setHttps(true);
            //设置入参
            httpClient.setXmlParam(xmlParam);
            //post提交
            httpClient.post();
            //响应,数据位xml
            String result = httpClient.getContent();
            //将响应的xml转换为map
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            map.put("out_trade_no", payLog.getOutTradeNo());//支付定单号
            map.put("total_fee", String.valueOf(payLog.getTotalFee()));//支付总金额
            return map;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //查询支付状态
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        Map<String, String> param = new HashMap<>();
        param.put("out_trade_no", out_trade_no);//支付定单号,商户订单号	out_trade_no
        param.put("appid", appid);//公众账号ID	appid
        param.put("mch_id", partner);//商户号	mch_id
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串	nonce_str    随机数生成算法
        try {
            //生成要发送的xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //查询订单的网址
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            HttpClient httpClient = new HttpClient(url);
            //设置https
            httpClient.setHttps(true);
            //设置入参
            httpClient.setXmlParam(xmlParam);
            //post提交
            httpClient.post();
            //响应,数据位xml
            String result = httpClient.getContent();
            //将响应的xml转换为map
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            return map;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
