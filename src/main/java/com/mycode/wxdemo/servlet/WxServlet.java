package com.mycode.wxdemo.servlet;

import com.mycode.wxdemo.utils.WxUtil ;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author li
 */
@WebServlet(urlPatterns="/wx")
@Slf4j
public class WxServlet extends HttpServlet {

    /**
     * 接入微信时会通过此方法进行接入校验
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //获取微信服务器GET请求中的参数
        //加密签名
        String signature = req.getParameter("signature");
        //时间戳
        String timestamp = req.getParameter("timestamp");
        //随机数
        String nonce = req.getParameter("nonce");
        //随机字符串
        String echostr = req.getParameter("echostr");
        //若确认此次GET请求来自微信服务器，请原样返回echostr参数内容，则接入生效，成为开发者成功，否则接入失败
        if(WxUtil.check(timestamp,nonce,signature)){
            log.info("接入成功");
            PrintWriter out = resp.getWriter();
            //原样返回echostr
            out.print(echostr);
            out.flush();
            out.close();
        }else{
            log.info("接入失败");
        }
    }

    /**
     *  后台与微信服务器数据交互的接口
     *  当普通微信用户向公众账号发消息时，微信服务器将POST消息的XML数据包到开发者填写的URL上
     *  公众号/服务号也可通过此接口对用户做被动消息回复
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {

        try {
            //将微信服务器发送的xml数据包解析存入到Map中
            Map<String,String> eleMap = WxUtil.parseFromStream(req.getInputStream());
            log.info("用户输入的消息是:{}",eleMap);
            //获取用户输入的语音 转换成文字 公众号/服务号必须开启语音识别权限
            //如果用户输入的是其他格式的信息 则返回为空
            log.info("用户输入的语音内容是:{}",eleMap.get("Recognition"));
            req.setCharacterEncoding("utf8");
            resp.setCharacterEncoding("utf8");

            //如果不需要对用户的输入进行反馈 则直接返回空或success
            //建议使用success
            //如果业务需要对用户输入进行针对性回复 则调用封装后的方法获取回复信息
            String respXml = "success";
            PrintWriter out = resp.getWriter();
            out.print(respXml);
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
