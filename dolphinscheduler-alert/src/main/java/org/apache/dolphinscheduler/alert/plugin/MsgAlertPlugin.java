package org.apache.dolphinscheduler.alert.plugin;

import org.apache.dolphinscheduler.alert.manager.MsgManager;
import org.apache.dolphinscheduler.alert.utils.Constants;
import org.apache.dolphinscheduler.plugin.api.AlertPlugin;
import org.apache.dolphinscheduler.plugin.model.AlertData;
import org.apache.dolphinscheduler.plugin.model.AlertInfo;
import org.apache.dolphinscheduler.plugin.model.PluginName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 二次开发新增类  告警发送短信
 */
public class MsgAlertPlugin implements AlertPlugin {
    private static final Logger logger = LoggerFactory.getLogger(MsgAlertPlugin.class);

    private PluginName pluginName;

    private static final MsgManager msgManager = new MsgManager();

    public MsgAlertPlugin() {
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

        return null;
    }
}
