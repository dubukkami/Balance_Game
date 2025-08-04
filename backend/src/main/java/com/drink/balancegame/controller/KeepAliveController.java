package com.drink.balancegame.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Railway 무료플랜 Sleep 방지를 위한 Keep-Alive 컨트롤러
 * 외부 서비스에서 주기적으로 호출하여 애플리케이션이 잠들지 않도록 함
 */
@RestController
@RequestMapping("/api")
public class KeepAliveController {
    
    /**
     * 간단한 Keep-Alive 엔드포인트
     * 최소한의 리소스로 응답하여 애플리케이션을 깨워둠
     */
    @GetMapping("/ping")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> ping() {
        return ResponseEntity.ok(Map.of(
                "status", "alive",
                "timestamp", LocalDateTime.now(),
                "service", "balance-game-community"
        ));
    }
    
    /**
     * 헬스체크와 유사하지만 더 가벼운 버전
     */
    @GetMapping("/heartbeat")
    @Transactional(readOnly = true)
    public ResponseEntity<String> heartbeat() {
        return ResponseEntity.ok("OK");
    }
}