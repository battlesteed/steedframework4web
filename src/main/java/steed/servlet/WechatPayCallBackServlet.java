package steed.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import steed.engine.wechat.MessageEngine;
import steed.engine.wechat.SimpleWechatPayCallBackEngine;
import steed.engine.wechat.WechatPayCallBackEngine;
import steed.hibernatemaster.util.base.ReflectUtil;
import steed.util.base.BaseUtil;
import steed.util.base.PropertyUtil;
import steed.util.base.StringUtil;
import steed.util.wechat.MessageUtil;
import steed.util.wechat.MutiAccountSupportUtil;
import steed.util.wechat.SignUtil;
import steed.util.wechat.domain.sys.PayCallBack;


/** 
 * 微信servlet
 * @author 战马
 */  
public class WechatPayCallBackServlet extends HttpServlet{
    private static final long serialVersionUID = 4440739483644821986L; 
    private static final Logger logger = LoggerFactory.getLogger(WechatPayCallBackServlet.class);
  
    /** 
     * 确认请求来自微信服务器 
     */  
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  
    	supportMutiAccount(request);
    }  
  
    /** 
     * 处理微信服务器发来的消息
     */  
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  
    	supportMutiAccount(request);
        PayCallBack scanPayCallBack = MessageUtil.getPayCallBack(request);
        logger.debug("接收实体类--->"+BaseUtil.getJson(scanPayCallBack));
        String sign = scanPayCallBack.getSign();
        scanPayCallBack.setSign(null);
        if(!sign.equals(SignUtil.signScanPayCallBack(scanPayCallBack))){
			logger.warn("非法扫描支付回调,微信支付回调实体类"+BaseUtil.getJson(scanPayCallBack));
			return;
		}
        PrintWriter out = response.getWriter();  
        
		String className = PropertyUtil.getProperties("wechatFrameworkConfig.properties").getProperty("wechatPayCallBackEngine");
        Object newInstance = ReflectUtil.newInstance(className);
        String message = ((WechatPayCallBackEngine)newInstance).getMessage(scanPayCallBack);
		out.print(message);
        out.flush();
        out.close();  
        logger.debug("回复xml--->"+message);
    }
    

	private void supportMutiAccount(HttpServletRequest request) {
		if (!MutiAccountSupportUtil.isSingleMode()) {
			MutiAccountSupportUtil.setWechatAccount(MutiAccountSupportUtil.getWechatAccount(request.getParameter("key")));
		}
	}  
	  
}
