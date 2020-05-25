package cn.travelround.core.message;

import cn.travelround.core.service.SearchService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Created by travelround on 2019/4/18.
 */
public class CustomMessageListener implements MessageListener {

    @Autowired
    private SearchService searchService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage am = (ActiveMQTextMessage) message;
        try {
//            System.out.println("接收到的消息内容是:"+am.getText());
            searchService.insertProductToSolr(Long.parseLong(am.getText()));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
