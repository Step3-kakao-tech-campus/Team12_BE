package pickup_shuttle.pickup.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import pickup_shuttle.pickup.security.service.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private JwtService jwtService;

    @Nested
    class testUserAuthStatus{
        @Test
        @DisplayName("성공 : 학생증 인증을 신청한 일반회원의 경우")
        void testUserAuthStatus1() throws Exception{
            //given
            String accessToken = jwtService.createAccessToken("3"); //USER

            //when
            ResultActions resultActions = mvc.perform(
                    get("/mypage/auth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testUserAuthStatus1 : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response").value("인증 진행 중"));
        }
        @Test
        @DisplayName("성공 : 학생증 인증을 신청하지 않은 일반회원의 경우")
        void testUserAuthStatus2() throws Exception{
            //given
            String accessToken = jwtService.createAccessToken("4"); //USER

            //when
            ResultActions resultActions = mvc.perform(
                    get("/mypage/auth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testUserAuthStatus2 : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response").value("미인증"));
        }
        @Test
        @DisplayName("성공 : 학생회원의 경우")
        void testUserAuthStatus3() throws Exception{
            //given
            String accessToken = jwtService.createAccessToken("5"); //STUDENT

            //when
            ResultActions resultActions = mvc.perform(
                    get("/mypage/auth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testUserAuthStatus3 : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response").value("인증"));
        }
    }

}