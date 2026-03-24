package com.neuedu.hisweb.mq;

import com.neuedu.hisweb.entity.OperationLog;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuditLogProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${his.mq.audit-exchange}")
    private String exchange;
    @Value("${his.mq.audit-routing-key}")
    private String routingKey;

    public AuditLogProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(OperationLog log) {
        rabbitTemplate.convertAndSend(exchange, routingKey, log);
    }
}
