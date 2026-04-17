package soyosa.inventaire.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "library.exchange";
    public static final String QUEUE_NAME = "inventaire.stock.queue";

    @Bean
    public TopicExchange libraryExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue stockQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingBorrowed(Queue stockQueue, TopicExchange libraryExchange) {
        return BindingBuilder.bind(stockQueue).to(libraryExchange).with("document.borrowed");
    }

    @Bean
    public Binding bindingReturned(Queue stockQueue, TopicExchange libraryExchange) {
        return BindingBuilder.bind(stockQueue).to(libraryExchange).with("document.returned");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
