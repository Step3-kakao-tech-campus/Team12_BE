package pickup_shuttle.pickup.domain.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.UserRepository;
import pickup_shuttle.pickup.domain.user.UserRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Sql("classpath:db/teardown.sql")
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
    private UserRepository userJPARepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    // 성공 케이스
    @Test
    public void loginTestSuccess() throws Exception{
        // given
        //this.join();
        UserRequest.LoginDTO loginDTO = new UserRequest.LoginDTO();
        loginDTO.setUid("testid");
        loginDTO.setPassword("1234");

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

    // 실패 케이스
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
