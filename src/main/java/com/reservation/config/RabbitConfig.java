package com.reservation.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String COMMISSION_QUEUE =
            "commission.queue";

    public static final String EMAIL_QUEUE =
            "email.queue";

    public static final String RESERVATION_CREATED_EXCHANGE =
            "reservation.created.exchange";

    public static final String RESERVATION_CONFIRMED_EXCHANGE =
            "reservation.confirmed.exchange";

    public static final String ROUTING_KEY = "reservation.email";

    @Bean
    public Queue commissionQueue() {
        return new Queue(COMMISSION_QUEUE);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE);
    }

    @Bean
    public FanoutExchange reservationCreatedExchange() {
        return new FanoutExchange(
                RESERVATION_CREATED_EXCHANGE
        );
    }

    @Bean
    public FanoutExchange reservationConfirmedExchange() {
        return new FanoutExchange(
                RESERVATION_CONFIRMED_EXCHANGE
        );
    }

    @Bean
    public Binding commissionBinding() {
        return BindingBuilder
                .bind(commissionQueue())
                .to(reservationCreatedExchange());
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder
                .bind(emailQueue())
                .to(reservationConfirmedExchange());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new JacksonJsonMessageConverter());
        return template;
    }
}