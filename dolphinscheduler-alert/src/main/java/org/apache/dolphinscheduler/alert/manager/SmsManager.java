package org.apache.dolphinscheduler.alert.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.mail.EmailException;
import org.apache.dolphinscheduler.alert.utils.Constants;
import org.apache.dolphinscheduler.alert.utils.DesUtils;
import org.apache.dolphinscheduler.alert.utils.JSONUtils;
import org.apache.dolphinscheduler.common.utils.CollectionUtils;
import org.apache.dolphinscheduler.common.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 二次开发新增类  告警发送短信
 * SMS send manager
 */

public class SmsManager {
    private static final Logger logger = LoggerFactory.getLogger(SmsManager.class);
    private static final String BASE_URL = "http://css.eda.net/inner/apis/test/SmsSend/sendSms?token=";

    /**
     * SMS send
     */
    public Map<String, Object> send(List<String> receviersList, String title, String content, String showType) {

        Map<String, Object> retMap = new HashMap<>();
        retMap.put(Constants.STATUS, false);

        // if there is no receivers && no receiversCc, no need to process
        if (CollectionUtils.isEmpty(receviersList)) {
            return retMap;
        }
        receviersList.removeIf(StringUtils::isEmpty);

        logger.info("receviersList:size:"+receviersList.size());
        if (CollectionUtils.isNotEmpty(receviersList) && StringUtils.isNotBlank(content)){
            for (String receiver : receviersList) {
                try {
                    logger.info("receiver:"+receiver);
                    // sender sms
                    sendOne(receiver,title, content, showType, retMap);
                }catch (Exception e) {
                    handleException(receviersList, retMap, e);
                }
            }
        }
        return retMap;
    }


    /**
     * the string object map
     *
     * @param title    the title
     * @param content  the content
     * @param showType the showType
     * @param retMap   the result map
     * @return the result map
     * @throws EmailException
     */
    private static Map<String, Object> sendOne(String phone, String title, String content, String showType, Map<String, Object> retMap) throws Exception {
        logger.info(title+"   "+phone+"   进入发短信方法");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            String txt = "2^" + phone + "^" + getSmsByTitle(content,title);
            DesUtils des = new DesUtils();//自定义密钥
            logger.info("加密前的字符：" + txt);
            String token = des.encrypt(txt);
            logger.info("加密后的字符：" + token);
            // 创建httpget.
            HttpGet httpget = new HttpGet(BASE_URL + token);
            logger.info("executing request " + httpget.getURI());
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                logger.info("--------------------------------------");
                // 打印响应状态
                logger.info(response.getStatusLine() + "");
                if (entity != null) {
                    // 打印响应内容长度
                    logger.info("Response content length: " + entity.getContentLength());
                    // 打印响应内容
                    logger.info("Response content: " + EntityUtils.toString(entity));
                }
                logger.info("------------------------------------");
                //retMap.put(Constants.STATUS, true);
            } catch (Exception e) {
                throw e;
            } finally {
                response.close();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (Exception e) {
                throw e;
            }
        }
        return retMap;
    }

    /**
     * handle exception
     *
     * @param receivers the receiver list
     * @param retMap    the result map
     * @param e         the exception
     */
    private static void handleException(Collection<String> receivers, Map<String, Object> retMap, Exception e) {
        logger.error("Send sms to {} failed", receivers, e);
        retMap.put(Constants.STATUS, false);
        retMap.put(Constants.MESSAGE, "Send sms to {" + String.join(",", receivers) + "} failed，" + e.toString());
    }

    /**
     * get alert message which type is TEXT
     * @param content message content
     * @return alert message
     */
    private static String getSmsByTitle(String content,String title){
        // scheduler success
        // scheduler failed

//        String sms = null;
//        if (StringUtils.isNotEmpty(content) && StringUtils.isNotEmpty(title)){
//            //手动或自动调度 成功
//            if (title.endsWith(" success")){
//                JSONArray jsonArray = JSON.parseArray(content);
//                if (jsonArray.size()>0){
//                    JSONObject jsonObject = jsonArray.getJSONObject(0);
//                    if (jsonObject.containsKey("name")){
//                        sms = jsonObject.getString("name")+" SUCCESS";
//                    }
//                }
//            }
//        }
//        if (StringUtils.isNotEmpty(content)) {
//            List<String> list;
//            try {
//                list = JSONUtils.toList(content, String.class);
//            } catch (Exception e) {
//                logger.error("json format exception", e);
//                return null;
//            }
//            StringBuilder contents = new StringBuilder(100);
//            for (String str : list){
//                contents.append(str).append(";");
//            }
//            return contents.toString();
//        }
        if (StringUtils.isNotEmpty(content)) {
            String sms = content.replaceAll("\\[","").replaceAll("]","").replaceAll("\\{","").replaceAll("}","").replaceAll("\"","").replaceAll("'","");
            return sms;
        }
        return null;
    }
}
