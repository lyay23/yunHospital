package com.neuedu.hisweb.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitMqConfig {

    @Value("${his.mq.audit-exchange}")
    private String auditExchange;
    @Value("${his.mq.audit-queue}")
    private String auditQueue;
    @Value("${his.mq.audit-dlq}")
    private String auditDlq;
    @Value("${his.mq.audit-routing-key}")
    private String auditRoutingKey;

    @Value("${his.mq.prescription-exchange}")
    private String prescriptionExchange;
    @Value("${his.mq.prescription-queue}")
    private String prescriptionQueue;
    @Value("${his.mq.prescription-dlq}")
    private String prescriptionDlq;
    @Value("${his.mq.prescription-routing-key}")
    private String prescriptionRoutingKey;

    @Value("${his.mq.register-exchange}")
    private String registerExchange;
    @Value("${his.mq.register-queue}")
    private String registerQueue;
    @Value("${his.mq.register-dlq}")
    private String registerDlq;
    @Value("${his.mq.register-routing-key}")
    private String registerRoutingKey;

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public DirectExchange auditExchange() {
        return new DirectExchange(auditExchange, true, false);
    }

    @Bean
    public DirectExchange prescriptionExchange() {
        return new DirectExchange(prescriptionExchange, true, false);
    }

    @Bean
    public DirectExchange registerExchange() {
        return new DirectExchange(registerExchange, true, false);
    }

    @Bean
    public DirectExchange auditDeadLetterExchange() {
        return new DirectExchange(auditExchange + ".dlx", true, false);
    }

    @Bean
    public DirectExchange prescriptionDeadLetterExchange() {
        return new DirectExchange(prescriptionExchange + ".dlx", true, false);
    }

    @Bean
    public DirectExchange registerDeadLetterExchange() {
        return new DirectExchange(registerExchange + ".dlx", true, false);
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(auditQueue)
                .withArgument("x-dead-letter-exchange", auditExchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", auditDlq)
                .build();
    }

    @Bean
    public Queue auditDlq() {
        return QueueBuilder.durable(auditDlq).build();
    }

    @Bean
    public Queue prescriptionQueue() {
        return QueueBuilder.durable(prescriptionQueue)
                .withArgument("x-dead-letter-exchange", prescriptionExchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", prescriptionDlq)
                .build();
    }

    @Bean
    public Queue prescriptionDlq() {
        return QueueBuilder.durable(prescriptionDlq).build();
    }

    @Bean
    public Queue registerQueue() {
        return QueueBuilder.durable(registerQueue)
                .withArgument("x-dead-letter-exchange", registerExchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", registerDlq)
                .build();
    }

    @Bean
    public Queue registerDlq() {
        return QueueBuilder.durable(registerDlq).build();
    }

    @Bean
    public Binding auditBinding(DirectExchange auditExchange, Queue auditQueue) {
        return BindingBuilder.bind(auditQueue).to(auditExchange).with(auditRoutingKey);
    }

    @Bean
    public Binding auditDlqBinding(DirectExchange auditDeadLetterExchange, Queue auditDlq) {
        return BindingBuilder.bind(auditDlq).to(auditDeadLetterExchange).with(String.valueOf(auditDlq));
    }

    @Bean
    public Binding prescriptionBinding(DirectExchange prescriptionExchange, Queue prescriptionQueue) {
        return BindingBuilder.bind(prescriptionQueue).to(prescriptionExchange).with(prescriptionRoutingKey);
    }

    @Bean
    public Binding prescriptionDlqBinding(DirectExchange prescriptionDeadLetterExchange, Queue prescriptionDlq) {
        return BindingBuilder.bind(prescriptionDlq).to(prescriptionDeadLetterExchange).with(String.valueOf(prescriptionDlq));
    }

    @Bean
    public Binding registerBinding(DirectExchange registerExchange, Queue registerQueue) {
        return BindingBuilder.bind(registerQueue).to(registerExchange).with(registerRoutingKey);
    }

    @Bean
    public Binding registerDlqBinding(DirectExchange registerDeadLetterExchange, Queue registerDlq) {
        return BindingBuilder.bind(registerDlq).to(registerDeadLetterExchange).with(String.valueOf(registerDlq));
    }
}
