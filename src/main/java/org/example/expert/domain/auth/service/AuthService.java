package org.example.expert.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        // 제가 passwordEncoder를 잘 모르는 부분이라서 저 부분만 검색을 해보았습니다
        /* Lv 2. 조건에 맞지 않는 경우 즉시 리턴하여, 불필요한 로직의 실행을 방지하고 성능을 향상시킵니다.
        *
        * String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());를 해석하면
        * 회원가입 요청 DTO에서 비밀번호를 꺼내서 전달받은 비밀번호를
        * 원래 값으로 되돌리지 못하는 형태로 변환 시켜서 그 변환시킨 값을 encodedPassword 변수에 담는 작업이고
        *
        * UserRole userRole = UserRole.of(signupRequest.getUserRole());
        * UserRole.of안에 들어가야할 값은 회원가입하는 유저의 권한이 필요하니 요청 DTO에서 권한값을 가져온 후 그 값을 알맞는 권한 값으로 변환 후
        * 변환한 권한 값을 userRole에 담아주는 역할을 하는 코드줄입니다
        *
        * 그런데 불필요한 로직의 실행을 방지하기 위해서 이 두 코드줄 보다 밑에 있던 이미 존재하는 이메일이면 예외가 발생해서 멈추게 되는데
        * 이미 존해하는 이메일인데 비밀번호를 암호화하고 권한 저장하는 작업을 먼저 하고 이메일 존재 여부를 예외를 던지는 것보다
        * 애초에 존재하는 이메일이면 이 해싱과 권환을 지정해주는 이런 로직이 실행할 필요가 없기 때문에 이미 존재하는 이메일 일때의 예외처리를 맨 상단으로 올렸습니다
        * 그러면 존재하지 않는 이메일만 저 두 작업을 해주면 되고 또한 맨 상단에서 이미 불필요한 값이나 잘못된 값을 처리해주기 때문에 불필요한 로직 및 성능 향상이 된다고 판단해서
        * 이렇게 로직 처리 순서를 변경했습니다.
        * */

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        User newUser = new User(
                signupRequest.getEmail(),
                encodedPassword,
                userRole
        );
        User savedUser = userRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), userRole);

        return new SignupResponse(bearerToken);
    }

    @Transactional(readOnly = true)
    public SigninResponse signin(SigninRequest signinRequest) {
        User user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(
                () -> new InvalidRequestException("가입되지 않은 유저입니다."));

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new AuthException("잘못된 비밀번호입니다.");
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());

        return new SigninResponse(bearerToken);
    }
}
