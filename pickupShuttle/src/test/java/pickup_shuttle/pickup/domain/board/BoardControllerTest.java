package pickup_shuttle.pickup.domain.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pickup_shuttle.pickup.domain.board.dto.request.WriteRqDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BoardControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Test //공고글 목록 조회
    void testBoardList() throws Exception {
        //given
        String lastBoardId = "";
        String size = "10";

        //when
        ResultActions resultActions = mvc.perform(
                get("/articles")
                        .queryParam("lastBoardId", lastBoardId)
                        .queryParam("size", size)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testBoardList : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
    }

<<<<<<< HEAD
    @WithMockUser(username = "test", roles = {"ROLE_STUDENT"})
    @Test
    void testWrite() throws Exception{
        //given
        WriteRqDTO writeRqDTO = WriteRqDTO.builder()
                .store("starbucks")
                .beverage("아이스 아메리카노 1잔")
                .destination("공과대학 7호관 팬도로시")
                .tip(1000)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishAt("2023-10-01 19:00")
                .build();
        String requestBody = om.writeValueAsString(writeRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
=======
    @Test // 공고글 상세 조회 (매칭 전)
    void testBoardDetailBefore() throws Exception {
        //given
        String boardId = "1";

        //when
        ResultActions resultActions = mvc.perform(
                get("/articles/before/{boardId}", boardId)
>>>>>>> 7bf9a69863910e0a198ac312c3cbac9913aa5fc8
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
<<<<<<< HEAD
        System.out.println("testWrite : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.boardId").value("6"));
    }
    @Test
    void testWriteNotFoundStore() throws Exception{
        //given
        WriteRqDTO writeRqDTO = WriteRqDTO.builder()
                .store("팬도로시")
                .beverage("아이스 아메리카노 1잔")
                .destination("공과대학 7호관 팬도로시")
                .tip(1000)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishAt("2023-10-01 19:00")
                .build();
        String requestBody = om.writeValueAsString(writeRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
=======
        System.out.println("testBoardList : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.boardId").value("1"));
        resultActions.andExpect(jsonPath("$.response.shopName").value("전남대 후문 스타벅스"));
        resultActions.andExpect(jsonPath("$.response.tip").value("1000"));
    }

    @Test // 공고글 상세 조회 (매칭 후)
    void testBoardDetailAfter() throws Exception {
        //given
        String boardId = "1";

        //when
        ResultActions resultActions = mvc.perform(
                get("/articles/after/{boardId}", boardId)
>>>>>>> 7bf9a69863910e0a198ac312c3cbac9913aa5fc8
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
<<<<<<< HEAD
        System.out.println("testWriteNotFoundStore : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("가게가 존재하지 않습니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));
    }
    @Test
    void testWriteBlankStore() throws Exception{
        //given
        WriteRqDTO writeRqDTO = WriteRqDTO.builder()
                .store(" ")
                .beverage("아이스 아메리카노 1잔")
                .destination("공과대학 7호관 팬도로시")
                .tip(1000)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishAt("2023-10-01 19:00")
                .build();
        String requestBody = om.writeValueAsString(writeRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testWriteBlankStore : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("가게가 공백입니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));
    }

    @Test
    void testWriteBlankBeverage() throws Exception{
        //given
        WriteRqDTO writeRqDTO = WriteRqDTO.builder()
                .store("starbucks")
                .beverage(" ")
                .destination("공과대학 7호관 팬도로시")
                .tip(1000)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishAt("2023-10-01 19:00")
                .build();
        String requestBody = om.writeValueAsString(writeRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testWriteBlankBeverage : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("음료가 공백입니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));
    }

    @Test
    void testWriteBlankDestination() throws Exception{
        //given
        WriteRqDTO writeRqDTO = WriteRqDTO.builder()
                .store("starbucks")
                .beverage("아이스 아메리카노 1잔")
                .destination(" ")
                .tip(1000)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishAt("2023-10-01 19:00")
                .build();
        String requestBody = om.writeValueAsString(writeRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testWriteBlankDestination : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("위치가 공백입니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));
    }

    @Test
    void testWriteInvalidTip() throws Exception{
        //given
        WriteRqDTO writeRqDTO = WriteRqDTO.builder()
                .store("starbucks")
                .beverage("아이스 아메리카노 1잔")
                .destination("공과대학 7호관 팬도로시")
                .tip(-100)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishAt("2023-10-01 19:00")
                .build();
        String requestBody = om.writeValueAsString(writeRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testWriteInvalidTip : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("픽업팁이 음수입니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));
    }

    @Test
    void testWriteBlankFinishAt() throws Exception{
        //given
        WriteRqDTO writeRqDTO = WriteRqDTO.builder()
                .store("starbucks")
                .beverage("아이스 아메리카노 1잔")
                .destination("공과대학 7호관 팬도로시")
                .tip(1000)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishAt(" ")
                .build();
        String requestBody = om.writeValueAsString(writeRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testWriteBlankFinishAt : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("마감기간이 공백입니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));
=======
        System.out.println("testBoardList : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.boardId").value("1"));
        resultActions.andExpect(jsonPath("$.response.shopName").value("전남대 후문 스타벅스"));
        resultActions.andExpect(jsonPath("$.response.tip").value("1000"));
        resultActions.andExpect(jsonPath("$.response.isMatch").value("true"));
        resultActions.andExpect(jsonPath("$.response.pickerPhoneNumber").value("010-0000-1234"));
>>>>>>> 7bf9a69863910e0a198ac312c3cbac9913aa5fc8
    }

}
