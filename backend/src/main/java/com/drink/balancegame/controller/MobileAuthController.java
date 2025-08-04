package com.drink.balancegame.controller;

import com.drink.balancegame.dto.UserDto;
import com.drink.balancegame.dto.TestLoginRequest;
import com.drink.balancegame.entity.User;
import com.drink.balancegame.repository.UserRepository;
import com.drink.balancegame.security.JwtTokenProvider;
import com.drink.balancegame.service.DtoConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 모바일 전용 인증 관련 컨트롤러
 */
@RestController
@RequestMapping("/api/mobile/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 모바일은 다양한 origin에서 접근 가능
public class MobileAuthController {
    
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final DtoConversionService dtoConversionService;
    
    /**
     * 모바일 전용 테스트 로그인 (간단한 사용자명/비밀번호)
     * User-Agent를 통해 모바일 접속인지 자동 확인 (개발 중에는 검증 비활성화)
     */
    @PostMapping("/test-login")
    public ResponseEntity<?> testLogin(@RequestBody TestLoginRequest request, HttpServletRequest httpRequest) {
        // User-Agent 확인하여 모바일 앱인지 검증 (개발 중에는 주석 처리)
        String userAgent = httpRequest.getHeader("User-Agent");
        // if (!isMobileApp(userAgent)) {
        //     return ResponseEntity.status(400).body("모바일 앱에서만 접근 가능합니다");
        // }
        
        // 간단한 테스트용 로그인 (실제 운영에서는 사용 안함)
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        
        if (user.isPresent()) {
            String token = jwtTokenProvider.createToken(user.get().getId(), user.get().getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", dtoConversionService.convertToUserDto(user.get()));
            response.put("platform", "mobile");
            response.put("detectedUserAgent", userAgent);
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("모바일 로그인 실패");
        }
    }
    
    /**
     * 모바일 전용 현재 사용자 정보 조회
     */
    @GetMapping("/me")
    @Transactional(readOnly = true)
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
     * 모바일 전용 토큰 유효성 검증
     */
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // "Bearer " 제거
            boolean isValid = jwtTokenProvider.validateToken(jwt);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("platform", "mobile");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("platform", "mobile");
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 모바일 전용 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT는 stateless하므로 서버에서 할 작업은 없지만, 
        // 클라이언트에서 토큰을 삭제하도록 응답
        Map<String, Object> response = new HashMap<>();
        response.put("message", "모바일 로그아웃 성공");
        response.put("platform", "mobile");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 모바일 전용 토큰 갱신
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // "Bearer " 제거
            Long userId = jwtTokenProvider.getUserId(jwt);
            
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                String newToken = jwtTokenProvider.createToken(user.get().getId(), user.get().getEmail());
                
                Map<String, Object> response = new HashMap<>();
                response.put("token", newToken);
                response.put("platform", "mobile");
                response.put("message", "토큰 갱신 성공");
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("사용자를 찾을 수 없습니다");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("토큰 갱신 실패");
        }
    }
    
    /**
     * 모바일 전용 앱 버전 확인
     */
    @GetMapping("/app-info")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAppInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("platform", "mobile");
        response.put("supportedVersion", "1.0.0");
        response.put("minimumVersion", "1.0.0");
        response.put("message", "모바일 앱 정보");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * User-Agent를 통해 모바일 앱인지 확인하는 유틸리티 메서드
     */
    private boolean isMobileApp(String userAgent) {
        if (userAgent == null) return false;
        
        String lowerUserAgent = userAgent.toLowerCase();
        
        // 모바일 디바이스 또는 앱의 User-Agent 패턴 확인
        return lowerUserAgent.contains("android") ||
               lowerUserAgent.contains("iphone") ||
               lowerUserAgent.contains("ipad") ||
               lowerUserAgent.contains("mobile") ||
               lowerUserAgent.contains("balancegame-app") || // 커스텀 앱 User-Agent
               (lowerUserAgent.contains("webview") && (lowerUserAgent.contains("android") || lowerUserAgent.contains("ios")));
    }
}