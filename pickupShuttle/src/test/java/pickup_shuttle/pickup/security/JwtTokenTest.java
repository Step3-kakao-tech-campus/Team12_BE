package pickup_shuttle.pickup.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.UserJPARepository;
import pickup_shuttle.pickup.domain.user.UserRequest;
import pickup_shuttle.pickup.domain.user.security.JwtTokenUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class JwtTokenTest {

    String mySecretKey = "Test-Secret-Key-12";
    long expireTimeMs = 1000 * 60 * 60;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserJPARepository userJPARepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Before
    public void join(){
        // User user = User.builder().uid("asdf1234").pwd(passwordEncoder.encode("1111")).phoneNumber("010-1234-1234").nickname("닉네임").name("이기준").build();
        // user.setRole("일반");//   -> User 엔티티에 @Setter 를 붙여야 테스트가 가능하다.
        // userJPARepository.save(user);
    }

    @Test
    public void jwtTest(){
        //String jwtToken = JwtTokenUtil.createToken(user.getUid(), mySecretKey, expireTimeMs);
        //System.out.println("jwtToken: " + jwtToken);
    }

    // 성공 케이스 (User Entity의 @Setter를 붙여야 테스트 가능)
    @Test
    public void loginTestSuccess() throws Exception{
        // given
        UserRequest.LoginDTO loginDTO = new UserRequest.LoginDTO();
        loginDTO.setUid("asdf1234");
        loginDTO.setPassword("1111");

        // when
        ResultActions resultActions = mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(loginDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("응답 테스트: " + responseBody);
        System.out.println("발급된 JWT 토큰: " + resultActions.andReturn().getResponse().getHeader("Authorization"));

        // then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response").value(IsNull.nullValue()));
        resultActions.andExpect(jsonPath("$.error").value(IsNull.nullValue()));


    }

    // 실패 케이스 (User Entity의 @Setter를 붙여야 테스트 가능)
    @Test
    public void loginTestFail() throws Exception{
        // given
        UserRequest.LoginDTO loginDTO = new UserRequest.LoginDTO();
        loginDTO.setUid("testest");
        loginDTO.setPassword("1113");

        // when
        ResultActions resultActions = mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(loginDTO))
                .contentType(MediaType.APPLICATION_JSON));

        // eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("응답 테스트: " + responseBody);
        System.out.println("발급된 JWT 토큰: " + resultActions.andReturn().getResponse().getHeader("Authorization"));

        // then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.response").value(IsNull.nullValue()));
        resultActions.andExpect(jsonPath("$.error.message").value("아이디가 잘못되었습니다. : testest"));
        resultActions.andExpect(jsonPath("$.error.status").value("400"));


    }

}
