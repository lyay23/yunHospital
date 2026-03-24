package com.neuedu.hisweb.mq;

import com.neuedu.hisweb.entity.OperationLog;
import com.neuedu.hisweb.service.OperationLogService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AuditLogConsumer {
    private final OperationLogService operationLogService;

    public AuditLogConsumer(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @RabbitListener(queues = "${his.mq.audit-queue}")
    public void handle(OperationLog log) {
        if (log == null) {
            return;
        }
        operationLogService.save(log);
    }
}
