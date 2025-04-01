package com.example.homework.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // Ở đây có thể lấy username từ Security Context
        // Hoặc lấy từ SecurityContextHolder
        return Optional.of("system");
    }
}
