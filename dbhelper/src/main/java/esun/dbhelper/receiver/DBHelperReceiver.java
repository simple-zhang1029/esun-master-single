//package esun.dbhelper.receiver;
//
//import com.rabbitmq.client.Channel;
//import esun.dbhelper.service.CommonService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * 获取rabbitmq队列信息
// * @author john.xiao
// * @date 2020-09-20 15:02
// */
//@Component
//public class DBHelperReceiver {
//	Logger logger= LoggerFactory.getLogger(DBHelperReceiver.class);
//
//	@Autowired
//	CommonService commonService;
//
//	String ENCODE="UTF-8";
//	@RabbitListener(queues ="esun.dhHelper.select")
//	public void selectProcess(@Payload Message message, Channel channel,@Header("product") String product) throws IOException {
//		String msg=new String(message.getBody(),ENCODE);
//		System.out.println(msg);
//	}
//	//告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了
//	// 否则消息服务器以为这条消息没处理掉 后续还会在发
//	private void basicNack(Message message, Channel channel) throws IOException {
//		channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
//	}
//
//}
//
