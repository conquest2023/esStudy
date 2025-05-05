package es.board.config.rabbitmq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQQueue {


    public static final String EXCHANGE_NAME = "comment.fanout";
    public static final String QUEUE_POINT = "comment.point";
    public static final String QUEUE_ALERT = "comment.alert";

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }

    // 각 Queue 선언
    @Bean
    public Queue alertQueue() {
        return new Queue(QUEUE_ALERT, true);
    }

    @Bean
    public Queue pointQueue() {
        return new Queue(QUEUE_POINT, true);
    }

    // 바인딩: 모든 큐를 fanout exchange에 연결
    @Bean
    public Binding bindingAlert(Queue alertQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(alertQueue).to(fanoutExchange);
    }

    @Bean
    public Binding bindingPoint(Queue pointQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(pointQueue).to(fanoutExchange);
    }
}
