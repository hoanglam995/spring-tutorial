package com.example.homework.messaging.consumers;

import com.example.homework.messaging.messages.RegisterOTPMessage;
import com.example.homework.services.MessageProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class RegisterOTPConsumer {
    private final MessageProcessingService messageProcessingService;

    @RabbitListener(queues = "${spring.queue.send-register-otp.name}")
    public void receiveOTP(RegisterOTPMessage message) {
        log.debug("Consumer: Phone: {} OTP: {}", message.getPhone(), message.getOtp());
        this.messageProcessingService.processSendRegisterOTP(message);
    }
}
