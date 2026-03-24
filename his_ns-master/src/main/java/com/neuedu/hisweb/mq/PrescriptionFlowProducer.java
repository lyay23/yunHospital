package com.neuedu.hisweb.mq;

import com.neuedu.hisweb.entity.mq.PrescriptionFlowMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionFlowProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${his.mq.prescription-exchange}")
    private String exchange;
    @Value("${his.mq.prescription-routing-key}")
    private String routingKey;

    public PrescriptionFlowProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(PrescriptionFlowMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
