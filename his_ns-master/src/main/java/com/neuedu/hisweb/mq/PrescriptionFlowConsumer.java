package com.neuedu.hisweb.mq;

import com.neuedu.hisweb.entity.mq.PrescriptionFlowMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PrescriptionFlowConsumer {

    @RabbitListener(queues = "${his.mq.prescription-queue}")
    public void handle(PrescriptionFlowMessage message) {
        if (message == null) {
            return;
        }
        log.info("处方流转异步推送: ids={}, state={}, userId={}, time={}",
                message.getPrescriptionIds(), message.getState(), message.getUserId(), message.getTime());
    }
}
