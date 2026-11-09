package org.example.expert.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class PasswordEncoderTest {

    // 서비스, 레포지토리 등 의존하는 객체가 없기 때문에 @InjectMocks 사용 불필요 판단
    // 중첩되는 코드는 없기 때문에 @BeforeEach 사용 X라고 판단
    private PasswordEncoder passwordEncoder;

    // Lv 4-1: 테스트코드 연습(예상대로 성공하는지에 대한 케이스 테스트)
    // 원래 PassWordEncoder 클래스의 matches()의 순서와 테스트 코드에서 사용한 matches 매개변수 순서 불일치
    // 해결 -> matches()의 인자값 순서 바꾸기
    // 기존 -> boolean matches = passwordEncoder.matches(encodedPassword, rawPassword);
    // 수정 후 -> boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
    @Test
    @DisplayName("원본 비밀번호와 해시 처리된 비밀번호 값을 비교했을 때 같은지 테스트")
    void matches_메서드가_정상적으로_동작한다() {
        // given
        passwordEncoder = new PasswordEncoder();

        String rawPassword = "testPassword"; // 원본 비번
        String encodedPassword = passwordEncoder.encode(rawPassword);// 해쉬처리된 비번

        // when
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        // then
        assertTrue(matches);
    }
}