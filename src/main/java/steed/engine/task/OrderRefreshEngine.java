package steed.engine.task;

import java.util.Date;
import java.util.List;

import steed.domain.system.Property;
import steed.engine.wechat.SimpleWechatPayCallBackEngine;
import steed.engine.wechat.WechatPayCallBackEngine;
import steed.hibernatemaster.util.DaoUtil;
import steed.util.base.BaseUtil;
import steed.util.base.DateUtil;
import steed.util.base.PropertyUtil;
import steed.util.reflect.ReflectUtil;
import steed.util.system.SimpleTaskEngine;
import steed.util.wechat.MessageUtil;
import steed.util.wechat.MutiAccountSupportUtil;
import steed.util.wechat.WechatInterfaceInvokeUtil;
import steed.util.wechat.domain.result.OrderQueryResult;
import steed.util.wechat.domain.send.OrderQuerySend;
import steed.util.wechat.domain.sys.PayCallBack;
import steed.util.wechat.domain.sys.ScanPayCallBackResult;

public class OrderRefreshEngine extends SimpleTaskEngine{

	@Override
	public void doTask() {
		Property property = new Property();
		property.setPropertyType("wechatOrder");
		property.setCreateDate_min_1(DateUtil.getLastday(30, new Date()));
		List<Property> listAllObj = DaoUtil.listAllObj(property);
		for(Property p:listAllObj){
			MutiAccountSupportUtil.setWechatAccount(MutiAccountSupportUtil.getWechatAccount(p.getValue()));
			OrderQuerySend send = new OrderQuerySend();
			send.setOut_trade_no(p.getKee().split(",")[0]);
			OrderQueryResult queryOrder = WechatInterfaceInvokeUtil.queryOrder(send);
			if (queryOrder.isSuccess()) {
				if ("SUCCESS".equals(queryOrder.getTrade_state())) {
					BaseUtil.getLogger().debug("查询到订单{}已经支付成功,开始做支付操作..",send.getOut_trade_no());
					PayCallBack payCallBack = new PayCallBack();
					ReflectUtil.copySameField(payCallBack, queryOrder);
					String className = PropertyUtil.getProperties("wechatFrameworkConfig.properties").getProperty("wechatPayCallBackEngine");
			        Object newInstance = ReflectUtil.newInstance(className);
			        String message = ((WechatPayCallBackEngine)newInstance).getMessage(payCallBack);
					ScanPayCallBackResult result = MessageUtil.fromXml(message, ScanPayCallBackResult.class);
					if ("SUCCESS".equals(result.getReturn_code())) {
						p.delete();
					}
				}
			}
		}
	}
}
