package com.neuedu.hisweb.mq;

import com.neuedu.hisweb.entity.vo.RegParam;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegisterPeakProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${his.mq.register-exchange}")
    private String exchange;
    @Value("${his.mq.register-routing-key}")
    private String routingKey;

    public RegisterPeakProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(RegParam param) {
        rabbitTemplate.convertAndSend(exchange, routingKey, param);
    }
}
