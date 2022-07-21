package org.apache.dolphinscheduler.alert.plugin;

import org.apache.dolphinscheduler.alert.manager.MsgManager;
import org.apache.dolphinscheduler.alert.manager.SmsManager;
import org.apache.dolphinscheduler.alert.utils.Constants;
import org.apache.dolphinscheduler.alert.utils.EnterpriseWeChatUtils;
import org.apache.dolphinscheduler.common.utils.CollectionUtils;
import org.apache.dolphinscheduler.plugin.api.AlertPlugin;
import org.apache.dolphinscheduler.plugin.model.AlertData;
import org.apache.dolphinscheduler.plugin.model.AlertInfo;
import org.apache.dolphinscheduler.plugin.model.PluginName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 二次开发新增类  告警发送短信
 */
public class SmsAlertPlugin implements AlertPlugin {
    private static final Logger logger = LoggerFactory.getLogger(SmsAlertPlugin.class);

    private PluginName pluginName;

    private static final SmsManager smsManager = new SmsManager();

    public SmsAlertPlugin() {
        this.pluginName = new PluginName();
        this.pluginName.setEnglish(Constants.PLUGIN_DEFAULT_SMS_EN);
        this.pluginName.setChinese(Constants.PLUGIN_DEFAULT_SMS_CH);
    }


    @Override
    public String getId() {
        return Constants.PLUGIN_DEFAULT_SMS;
    }

    @Override
    public PluginName getName() {
        return pluginName;
    }

    @Override
    public Map<String, Object> process(AlertInfo info) {
        Map<String, Object> retMaps = new HashMap<>();

        AlertData alert = info.getAlertData();

        List<String> receviersList = (List<String>) info.getProp(Constants.PLUGIN_DEFAULT_EMAIL_RECEIVERS);

        if (CollectionUtils.isEmpty(receviersList)) {
            logger.warn("alert send error : At least one receiver address required");
            retMaps.put(Constants.STATUS, "false");
            retMaps.put(Constants.MESSAGE, "execution failure,At least one receiver address required.");
            return retMaps;
        }

        retMaps = smsManager.send(receviersList, alert.getTitle(), alert.getContent(),alert.getShowType());

        if (retMaps == null) {
            retMaps = new HashMap<>();
            retMaps.put(Constants.MESSAGE, "alert send error.");
            retMaps.put(Constants.STATUS, "false");
            logger.info("alert send error : {}", retMaps.get(Constants.MESSAGE));
            return retMaps;
        }

        boolean status = Boolean.parseBoolean(String.valueOf(retMaps.get(Constants.STATUS)));
        if (status) {
            logger.info("alert send success");
            retMaps.put(Constants.MESSAGE, "sms send success.");
        } else {
            retMaps.put(Constants.MESSAGE, "alert send error.");
            logger.info("alert send error : {}", retMaps.get(Constants.MESSAGE));
        }
        return retMaps;
    }
}
