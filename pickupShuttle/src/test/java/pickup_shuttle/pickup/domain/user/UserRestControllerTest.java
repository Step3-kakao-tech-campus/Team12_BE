package pickup_shuttle.pickup.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    // 회원가입 성공
    @Test
    void testRegister() throws Exception{
        //given
        UserRequest.RegisterDTO registerDTO = new UserRequest.RegisterDTO();
        registerDTO.setNickname("닉네임");
        registerDTO.setAccount("3510987456722");
        registerDTO.setBank("농협");

        String requestBody = om.writeValueAsString(registerDTO);
        //when
        ResultActions resultActions = mvc.perform(
                post("/users/register")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testRegister : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response").value("회원가입 성공"));

    }

    // 회원가입 실패 : 계좌번호 자릿수가 10이상이 아닌 경우
    @Test
    void testRegisterInvalidAccountSize() throws Exception{
        //given
        UserRequest.RegisterDTO registerDTO = new UserRequest.RegisterDTO();
        registerDTO.setNickname("닉네임");
        registerDTO.setAccount("35109");
        registerDTO.setBank("농협");

        String requestBody = om.writeValueAsString(registerDTO);
        //when
        ResultActions resultActions = mvc.perform(
                post("/users/register")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testRegisterInvalidAccountSize : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("계좌번호는 최소 10자리 이상입니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));

    }

    // 회원가입 실패 : 계좌번호에 숫자외의 다른 문자가 입력된 경우
    @Test
    void testRegisterInvalidAccount() throws Exception{
        //given
        UserRequest.RegisterDTO registerDTO = new UserRequest.RegisterDTO();
        registerDTO.setNickname("닉네임");
        registerDTO.setAccount("351-0987-4567-22");
        registerDTO.setBank("농협");

        String requestBody = om.writeValueAsString(registerDTO);
        //when
        ResultActions resultActions = mvc.perform(
                post("/users/register")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testRegisterInvalidAccount : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("숫자만 입력하세요"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));

    }

    // 회원가입 실패 : 은행명이 공백인 경우
    @Test
    void testRegisterBlankBank() throws Exception{
        //given
        UserRequest.RegisterDTO registerDTO = new UserRequest.RegisterDTO();
        registerDTO.setNickname("닉네임");
        registerDTO.setAccount("3510987456722");
        registerDTO.setBank("");

        String requestBody = om.writeValueAsString(registerDTO);
        //when
        ResultActions resultActions = mvc.perform(
                post("/users/register")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testRegisterInvalidAccount : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("은행명이 공백입니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));

    }


    // 닉네임 확인 성공
    @Test
    void testCheckNickname() throws Exception{
        //given
        UserRequest.NicknameCheckDTO nicknameCheckDTO = new UserRequest.NicknameCheckDTO();
        nicknameCheckDTO.setNickname("picker");

        String requestBody = om.writeValueAsString(nicknameCheckDTO);
        //when
        ResultActions resultActions = mvc.perform(
                get("/users/duplicate/nickname")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testCheckNickname : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response").value("사용 가능한 닉네임 입니다"));
    }

    // 닉네임 확인 실패 : 닉네임이 공백인 경우
    @Test
    void testCheckNicknameBlank() throws Exception{
        //given
        UserRequest.NicknameCheckDTO nicknameCheckDTO = new UserRequest.NicknameCheckDTO();
        nicknameCheckDTO.setNickname("");

        String requestBody = om.writeValueAsString(nicknameCheckDTO);
        //when
        ResultActions resultActions = mvc.perform(
                get("/users/duplicate/nickname")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testCheckNicknameBlank : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("닉네임 값이 공백입니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));
    }

    // 닉네임 확인 실패 : 닉네임이 중복된 경우
    @Test
    void testCheckNicknameDuplicate() throws Exception{
        //given
        UserRequest.NicknameCheckDTO nicknameCheckDTO = new UserRequest.NicknameCheckDTO();
        nicknameCheckDTO.setNickname("pickupmaster");

        String requestBody = om.writeValueAsString(nicknameCheckDTO);
        //when
        ResultActions resultActions = mvc.perform(
                get("/users/duplicate/nickname")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testCheckNicknameDuplicate : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("동일한 닉네임이 존재합니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));
    }
}