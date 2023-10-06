package pickup_shuttle.pickup.domain.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pickup_shuttle.pickup.domain.board.dto.request.BoardAgreeRqDTO;
import pickup_shuttle.pickup.domain.board.dto.request.BoardWriteRqDTO;
import pickup_shuttle.pickup.security.service.JwtService;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private JwtService jwtService;

    private  List<String> beverags = new ArrayList<>();

    @BeforeEach
    void beforEach() throws Exception {
        beverags.add("아이스 아메리카노");
        beverags.add("핫 아메리카노");
    }
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

    @Test // 공고글 상세 조회 (매칭 전)
    void testBoardDetailBefore() throws Exception {
        //given
        String boardId = "1";

        //when
        ResultActions resultActions = mvc.perform(
                get("/articles/before/{boardId}", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
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
        Long boardId = 1L;

        //when
        ResultActions resultActions = mvc.perform(
                get("/articles/after/{boardId}", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testBoardList : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.boardId").value("1"));
        resultActions.andExpect(jsonPath("$.response.shopName").value("전남대 후문 스타벅스"));
        resultActions.andExpect(jsonPath("$.response.tip").value("1000"));
        resultActions.andExpect(jsonPath("$.response.isMatch").value("true"));
        resultActions.andExpect(jsonPath("$.response.pickerPhoneNumber").value("010-0000-1234"));
    }

    @Test
    void testWrite() throws Exception{
        //given
        String accessToken = jwtService.createAccessToken("1111");
        System.out.println("accessToken은 " + accessToken);
        BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                .store("starbucks")
                .beverage(beverags)
                .destination("공과대학 7호관 팬도로시")
                .tip(1000)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishedAt("2023-10-01 19:00")
                .build();
        String requestBody = om.writeValueAsString(boardWriteRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("access_token", accessToken))
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testWrite : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.boardId").value("6"));
    }
    @Test
    void testWriteNotFoundStore() throws Exception{
        //given
        String accessToken = jwtService.createAccessToken("0000");
        BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                .store("팬도로시")
                .beverage(beverags)
                .destination("공과대학 7호관 팬도로시")
                .tip(1000)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishedAt("2023-10-01 19:00")
                .build();
        String requestBody = om.writeValueAsString(boardWriteRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("access_token", accessToken))
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testWriteNotFoundStore : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("가게가 존재하지 않습니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));
    }
    @Test
    void testWriteBlankStore() throws Exception{
        //given
        String accessToken = jwtService.createAccessToken("0000");
        BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                .store(" ")
                .beverage(beverags)
                .destination("공과대학 7호관 팬도로시")
                .tip(1000)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishedAt("2023-10-01 19:00")
                .build();
        String requestBody = om.writeValueAsString(boardWriteRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("access_token", accessToken))
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
        String accessToken = jwtService.createAccessToken("0000");
        beverags.add("");
        BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                .store("starbucks")
                .beverage(beverags)
                .destination("공과대학 7호관 팬도로시")
                .tip(1000)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishedAt("2023-10-01 19:00")
                .build();
        String requestBody = om.writeValueAsString(boardWriteRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("access_token", accessToken))
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testWriteBlankBeverage : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("음료명에 빈 문자열 or null이 입력 되었습니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));
    }

    @Test
    void testWriteBlankDestination() throws Exception{
        //given
        String accessToken = jwtService.createAccessToken("0000");
        BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                .store("starbucks")
                .beverage(beverags)
                .destination(" ")
                .tip(1000)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishedAt("2023-10-01 19:00")
                .build();
        String requestBody = om.writeValueAsString(boardWriteRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("access_token", accessToken))
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
        String accessToken = jwtService.createAccessToken("0000");
        BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                .store("starbucks")
                .beverage(beverags)
                .destination("공과대학 7호관 팬도로시")
                .tip(-100)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishedAt("2023-10-01 19:00")
                .build();
        String requestBody = om.writeValueAsString(boardWriteRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("access_token", accessToken))
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
        String accessToken = jwtService.createAccessToken("0000");
        BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                .store("starbucks")
                .beverage(beverags)
                .destination("공과대학 7호관 팬도로시")
                .tip(1000)
                .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                .finishedAt(" ")
                .build();
        String requestBody = om.writeValueAsString(boardWriteRqDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/write")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("access_token", accessToken))
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testWriteBlankFinishAt : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("마감기간이 공백입니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));
    }
    @Test
    void testBoardAgree() throws Exception{
        //given
        Long boardId = 1L;
        String accessToken = jwtService.createAccessToken("1111");
        BoardAgreeRqDTO requestDTO = BoardAgreeRqDTO.builder()
                .arrivalTime(30)
                .build();
        String requestBody = om.writeValueAsString(requestDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/agree/{boardId}", boardId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("access_token", accessToken))
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testWriteBlankFinishAt : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.beverage[0].name").value("핫 아메리카노"));
        resultActions.andExpect(jsonPath("$.response.beverage[1].name").value("아이스 아메리카노"));

    }

    @Test()
    @DisplayName("공고글 작성자가 매칭 수락 한 경우")
    void testFailBoardAgree() throws Exception {
        Long boardId = 1L;
        String accessToken = jwtService.createAccessToken("0000");
        BoardAgreeRqDTO requestDTO = BoardAgreeRqDTO.builder()
                .arrivalTime(30)
                .build();
        String requestBody = om.writeValueAsString(requestDTO);

        //when
        ResultActions resultActions = mvc.perform(
                post("/articles/agree/{boardId}", boardId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("access_token", accessToken))
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testWriteBlankFinishAt : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("공고글 작성자는 매칭 수락을 할 수 없습니다"));
        resultActions.andExpect(jsonPath("$.error.status").value(400));

    }
}