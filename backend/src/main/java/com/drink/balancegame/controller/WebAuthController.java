package com.drink.balancegame.controller;

import com.drink.balancegame.dto.UserDto;
import com.drink.balancegame.dto.TestLoginRequest;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.repository.UserRepository;
import com.drink.balancegame.security.JwtTokenProvider;
import com.drink.balancegame.service.DtoConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 웹 전용 인증 관련 컨트롤러
 */
@RestController
@RequestMapping("/api/web/auth")
@RequiredArgsConstructor
public class WebAuthController {
    
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final DtoConversionService dtoConversionService;
    
    /**
     * 웹 전용 테스트 로그인 (간단한 사용자명/비밀번호)
     * User-Agent를 통해 웹 접속인지 자동 확인 (개발 중에는 검증 비활성화)
     */
    @PostMapping("/test-login")
    public ResponseEntity<?> testLogin(@RequestBody TestLoginRequest request, HttpServletRequest httpRequest) {
        // User-Agent 확인하여 웹 브라우저인지 검증 (개발 중에는 주석 처리)
        String userAgent = httpRequest.getHeader("User-Agent");
        // if (!isWebBrowser(userAgent)) {
        //     return ResponseEntity.status(400).body("웹 브라우저에서만 접근 가능합니다");
        // }
        
        // 간단한 테스트용 로그인 (실제 운영에서는 사용 안함)
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        
        if (user.isPresent()) {
            String token = jwtTokenProvider.createToken(user.get().getId(), user.get().getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", dtoConversionService.convertToUserDto(user.get()));
            response.put("platform", "web");
            response.put("detectedUserAgent", userAgent);
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("웹 로그인 실패");
        }
    }
    
    /**
     * 웹 전용 현재 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // "Bearer " 제거
            Long userId = jwtTokenProvider.getUserId(jwt);
            
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                return ResponseEntity.ok(dtoConversionService.convertToUserDto(user.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    /**
     * 웹 전용 토큰 유효성 검증
     */
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // "Bearer " 제거
            boolean isValid = jwtTokenProvider.validateToken(jwt);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("platform", "web");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("platform", "web");
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 웹 전용 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT는 stateless하므로 서버에서 할 작업은 없지만, 
        // 클라이언트에서 토큰을 삭제하도록 응답
        Map<String, Object> response = new HashMap<>();
        response.put("message", "웹 로그아웃 성공");
        response.put("platform", "web");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * User-Agent를 통해 웹 브라우저인지 확인하는 유틸리티 메서드
     */
    private boolean isWebBrowser(String userAgent) {
        if (userAgent == null) return false;
        
        String lowerUserAgent = userAgent.toLowerCase();
        
        // 일반적인 웹 브라우저들의 User-Agent 패턴 확인
        return lowerUserAgent.contains("chrome") ||
               lowerUserAgent.contains("firefox") ||
               lowerUserAgent.contains("safari") ||
               lowerUserAgent.contains("edge") ||
               lowerUserAgent.contains("opera") ||
               lowerUserAgent.contains("mozilla");
    }
}