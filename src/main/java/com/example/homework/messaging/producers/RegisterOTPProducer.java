package com.example.homework.messaging.producers;

import com.example.homework.messaging.messages.RegisterOTPMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class RegisterOTPProducer {
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routeKey;

    public RegisterOTPProducer(RabbitTemplate rabbitTemplate,
                               @Value("${spring.queue.send-register-otp.exchangeName}") String exchange,
                               @Value("${spring.queue.send-register-otp.routingKey}") String routeKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routeKey = routeKey;
    }


    public void sendOTP(RegisterOTPMessage message) {
        this.rabbitTemplate.convertAndSend(exchange, routeKey, message);
        log.debug("Producer: Phone: {} OTP: {}", message.getPhone(), message.getOtp());
    }
}
