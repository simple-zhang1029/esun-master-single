//package esun.dbhelper.config;
//
//
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
///**
// * @author test
// */
//@Configuration
//public class RabbitMqConfig {
//	@Autowired
//	private Environment environment;
//	@Bean
//	public TopicExchange dbHelperExchange(){
//		return new TopicExchange(environment.getProperty("esun.dbHelper.mq.exchange.topic"));
//	}
//
//	@Bean(name = "dbHelperQueue")
//	public Queue dbHelperQueue(){
//		return new Queue(environment.getProperty("esun.dbHelper.mq.queue"));
//	}
//
//	@Bean
//	public Binding dbHelperBinding(){
//		return BindingBuilder.bind(dbHelperQueue()).to(dbHelperExchange()).with(environment.getProperty("esun.dbHelper.mq.router.key"));
//	}
//
//}
