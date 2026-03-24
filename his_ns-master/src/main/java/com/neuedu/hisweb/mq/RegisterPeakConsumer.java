package com.neuedu.hisweb.mq;

import com.neuedu.hisweb.entity.vo.RegParam;
import com.neuedu.hisweb.service.IRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RegisterPeakConsumer {
    private final IRegisterService registerService;

    public RegisterPeakConsumer(IRegisterService registerService) {
        this.registerService = registerService;
    }

    @RabbitListener(queues = "${his.mq.register-queue}")
    public void handle(RegParam param) {
        if (param == null) {
            return;
        }
        boolean ok = registerService.saveRegister(param);
        if (!ok) {
            throw new RuntimeException("挂号削峰处理失败");
        }
        log.info("挂号削峰处理完成: {}", param.getRegister() != null ? param.getRegister().getId() : null);
    }
}
