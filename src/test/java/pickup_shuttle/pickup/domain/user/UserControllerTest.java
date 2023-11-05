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
import pickup_shuttle.pickup.domain.user.dto.request.UserAuthApproveRqDTO;
import pickup_shuttle.pickup.domain.user.repository.UserRepository;
import pickup_shuttle.pickup.security.service.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
            String accessToken = "Bearer " + jwtService.createAccessToken("2"); //USER

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
            String accessToken = "Bearer " + jwtService.createAccessToken("7");

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
            String accessToken = "Bearer " + jwtService.createAccessToken("3"); //STUDENT

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
    @Test
    @DisplayName("성공 : 마이페이지 조회")
    void tesMyPage() throws Exception{
        //given
        String accessToken = "Bearer " + jwtService.createAccessToken("2"); //USER

        //when
        ResultActions resultActions = mvc.perform(
                get("/mypage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testMyPage : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.role").value("ROLE_USER"));
        resultActions.andExpect(jsonPath("$.response.nickname").value("배경"));
    }

    @Test
    @DisplayName("성공 : 학생 인증 목록 보기")
    void testGetAuthList() throws Exception {
        //given
        String accessToken = "Bearer " + jwtService.createAccessToken("1"); //ADMIN

        //when
        ResultActions resultActions = mvc.perform(
                get("/admin/auth/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testGetAuthList : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.content[0].userId").value(2));
        resultActions.andExpect(jsonPath("$.response.content[0].nickname").value("배경"));

    }
    @Test
    @DisplayName("성공 : 학생 인증 상세보기")
    void testGetAuthDetail() throws Exception {
        //given
        String accessToken = "Bearer " + jwtService.createAccessToken("1"); //ADMIN
        Long userId = 2L; // 학생 인증을 신청한 일반회원
        //when
        ResultActions resultActions = mvc.perform(
                get("/admin/auth/list/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testGetAuthDetail : " + responseBody);
        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.nickname").value("배경"));
    }
    @Nested
    class testAuthApprove{
        @Test
        @DisplayName("성공 : 학생 인증 승인")
        void testAuthApprove() throws Exception {
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("1"); //ADMIN
            Long userId = 2L; // 학생 인증을 신청한 일반회원
            UserAuthApproveRqDTO requestDTO = UserAuthApproveRqDTO.builder()
                    .userId(userId)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/admin/auth/approval")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testAuthApprove : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response").value("학생 인증이 승인되었습니다"));
        }
        @Test
        @DisplayName("실패 : 일반 등급이 아닌 회원의 학생 인증을 시도하는 경우")
        void testAuthApproveInvalidRole() throws Exception {
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("1"); //ADMIN
            Long userId = 7L; // GUEST
            UserAuthApproveRqDTO requestDTO = UserAuthApproveRqDTO.builder()
                    .userId(userId)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/admin/auth/approval")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testAuthApproveInvalidRole : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("일반 회원이 아닙니다"));
        }
    }
}
