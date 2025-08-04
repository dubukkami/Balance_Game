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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 인증 관련 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final DtoConversionService dtoConversionService;
    
    /**
     * 테스트 로그인 (간단한 사용자명/비밀번호)
     */
    @PostMapping("/test-login")
    public ResponseEntity<?> testLogin(@RequestBody TestLoginRequest request) {
        // 간단한 테스트용 로그인 (실제 운영에서는 사용 안함)
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        
        if (user.isPresent()) {
            String token = jwtTokenProvider.createToken(user.get().getId(), user.get().getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", dtoConversionService.convertToUserDto(user.get()));
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("로그인 실패");
        }
    }
    
    /**
     * 현재 사용자 정보 조회
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
    
}

