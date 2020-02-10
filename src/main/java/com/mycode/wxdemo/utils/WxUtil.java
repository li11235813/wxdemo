package com.mycode.wxdemo.utils;

import com.thoughtworks.xstream.XStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 接入微信的工具类
 * @author li
 */
public class WxUtil {

    /**接口配置 TOKEN*/
    private static final String TOKEN = "tokennn";

    /**
     *
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param signature 签名
     * @return
     */
    public static boolean check(String timestamp,String nonce,String signature){
        //1) 将token、timestamp、nonce三个参数进行字典序排序
        String[] params = new String[]{timestamp,nonce,TOKEN};
        Arrays.sort(params);
        //2）将三个参数字符串拼接成一个字符串进行sha1加密
        String str = params[0]+params[1]+params[2];
        String mysig = sha1(str);
        System.out.println(mysig);
        System.out.println(signature);
        //3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        if(mysig.equals(signature)){
            return true;
        }
        return false;
    }

    /**
     * 进行sha1加密
     * @param src 待加密字符串
     * @return
     */
    private static String sha1(String src){
        try {
            //获取加密对象
            MessageDigest md = MessageDigest.getInstance("sha1");
            //加密
            byte[] digest = md.digest(src.getBytes());
            char[] oxChars ={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
            StringBuilder sb = new StringBuilder();
            //处理加密结果
            for(byte b:digest){
                sb.append(oxChars[(b>>4)&15]);
                sb.append(oxChars[b&15]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  将xml文件解析并保存到Map中
     * @param is 来自微信服务器的输入流
     * @return
     */
    public static Map<String,String> parseFromStream(InputStream is){
        SAXReader reader = new SAXReader();
        //保存所有的节点对象
        Map<String,String> eleMap = new HashMap<>(16);
        try {
            //读取输入流 获取文档对象
            Document document = reader.read(is);
            //获取根节点
            Element root = document.getRootElement();
            //获取根节点的所有子节点
            List<Element> elements = root.elements();

            for(Element e : elements){
                eleMap.put(e.getName(),e.getStringValue());
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return eleMap;
    }

}
