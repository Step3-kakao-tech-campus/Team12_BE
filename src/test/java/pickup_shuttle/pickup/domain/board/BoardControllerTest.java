package pickup_shuttle.pickup.domain.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.domain.board.dto.request.BoardAgreeRqDTO;
import pickup_shuttle.pickup.domain.board.dto.request.BoardModifyRqDTO;
import pickup_shuttle.pickup.domain.board.dto.request.BoardWriteRqDTO;
import pickup_shuttle.pickup.security.service.JwtService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private  List<String> beverages = new ArrayList<>();

    @BeforeEach
    void beforEach() throws Exception {
        beverages.add("아이스 아메리카노 1잔");
        beverages.add("핫 아메리카노 1잔");
    }
    @Test //공고글 목록 조회
    void testBoardList() throws Exception {
        //given
        String accessToken = "Bearer " + jwtService.createAccessToken("2");
        String limit = "10";

        //when
        ResultActions resultActions = mvc.perform(
                get("/articles")
                        .queryParam("limit", limit)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
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
        String accessToken = "Bearer " + jwtService.createAccessToken("1");
        String boardId = "1";

        //when
        ResultActions resultActions = mvc.perform(
                get("/articles/before/{boardId}", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testBoardList : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.boardId").value("1"));
        resultActions.andExpect(jsonPath("$.response.shopName").value("스타벅스"));
        resultActions.andExpect(jsonPath("$.response.tip").value("1000"));
    }

    @Test // 공고글 상세 조회 (매칭 후)
    void testBoardDetailAfter() throws Exception {
        //given
        String accessToken = "Bearer " + jwtService.createAccessToken("3");
        Long boardId = 3L;

        //when
        ResultActions resultActions = mvc.perform(
                get("/articles/after/{boardId}", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
        );

        //eye
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("testBoardList : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.boardId").value("3"));
        resultActions.andExpect(jsonPath("$.response.shopName").value("더벤티"));
        resultActions.andExpect(jsonPath("$.response.tip").value("2000"));
        resultActions.andExpect(jsonPath("$.response.isMatch").value("true"));
        resultActions.andExpect(jsonPath("$.response.pickerPhoneNumber").value("010-0000-0000"));
    }

    @Nested
    class testBoardWrite{
        @Test
        @DisplayName("성공")
        void testWrite() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                    .store("스타벅스")
                    .beverage(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardWriteRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testWrite : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response.boardId").value("7"));
        }
        @Test
        @DisplayName("실패 : 서비스에서 제공하지 않는 가게의 경우")
        void testWriteNotFoundStore() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                    .store("팬도로시")
                    .beverage(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardWriteRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testWriteNotFoundStore : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value(ErrorMessage.UNKNOWN_STORE));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
        }
        @Test
        @DisplayName("실패 : 가게가 공백인 경우")
        void testWriteBlankStore() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                    .store(" ")
                    .beverage(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardWriteRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
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
        @DisplayName("실패 : 음료가 공백인 경우")
        void testWriteBlankBeverage() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            beverages.add("");
            BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                    .store("스타벅스")
                    .beverage(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardWriteRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
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
        @DisplayName("실패 : 위치가 공백인 경우")
        void testWriteBlankDestination() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                    .store("스타벅스")
                    .beverage(beverages)
                    .destination(" ")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardWriteRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
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
        @DisplayName("실패 : 픽업팁이 음수인 경우")
        void testWriteInvalidTip() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                    .store("스타벅스")
                    .beverage(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(-1)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardWriteRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
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
        @DisplayName("실패 : 마감기간이 공백인 경우")
        void testWriteBlankFinishAt() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            BoardWriteRqDTO boardWriteRqDTO = BoardWriteRqDTO.builder()
                    .store("스타벅스")
                    .beverage(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("")
                    .build();
            String requestBody = om.writeValueAsString(boardWriteRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testWriteBlankFinishAt : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("마감기간이 공백입니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
        }
    }

    @Nested
    class testBoardAgree {
        @Test
        @DisplayName("성공")
        void testBoardAgree() throws Exception{
            //given
            Long boardId = 6L;
            String accessToken =  "Bearer " + jwtService.createAccessToken("3");
            BoardAgreeRqDTO requestDTO = BoardAgreeRqDTO.builder()
                    .arrivalTime(30)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/agree/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardAgree : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response.beverage[0].name").value("카페라떼 1잔"));

        }

        @Test()
        @DisplayName("실패 : 공고글 작성자가 매칭 수락 한 경우")
        void testFailBoardAgree() throws Exception {
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            BoardAgreeRqDTO requestDTO = BoardAgreeRqDTO.builder()
                    .arrivalTime(30)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/agree/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testFailBoardAgree : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("공고글 작성자는 매칭 수락을 할 수 없습니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(400));

        }

        @Test
        @DisplayName("실패 : 공고글이 이미 매칭 된 경우")
        void testFailBoardAgree2() throws Exception{
            //given
            Long boardId = 3L;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            BoardAgreeRqDTO requestDTO = BoardAgreeRqDTO.builder()
                    .arrivalTime(30)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/agree/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardAgree : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("공고글이 이미 매칭 됐습니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(400));

        }
        @Test
        @DisplayName("실패 : 도착 예정 시간이 60 초과인 경우")
        void testFailBoardAgree3() throws Exception{
            //given
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            BoardAgreeRqDTO requestDTO = BoardAgreeRqDTO.builder()
                    .arrivalTime(61)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/agree/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardAgree : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("도착예정 시간은 60 이하이어야 합니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(400));

        }
        @Test
        @DisplayName("실패 : 도착 예정 시간이 0 미만인 경우")
        void testFailBoardAgree4() throws Exception{
            //given
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            BoardAgreeRqDTO requestDTO = BoardAgreeRqDTO.builder()
                    .arrivalTime(-1)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/agree/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardAgree : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("도착예정 시간은 0 이상이어야 합니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(400));

        }
        @Test
        @DisplayName("실패 : 도착 예정 시간이 공백인 경우")
        void testFailBoardAgree5() throws Exception{
            //given
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            BoardAgreeRqDTO requestDTO = BoardAgreeRqDTO.builder()
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/agree/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardAgree : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("도착예정 시간이 공백입니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(400));

        }
    }
    @Nested
    class testBoardDelete {
        @Test
        @DisplayName("성공")
        void testBoardDelete() throws Exception {
            // given
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            //when
            ResultActions resultActions = mvc.perform(
                    delete("/articles/delete/{boardId}", boardId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardDelete : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response").value("공고글 삭제 완료"));
        }

        @Test
        @DisplayName("실패 : 이미 매칭된 공고를 삭제하려는 경우")
        void testBoardDeleteMatch() throws Exception {
            // given
            Long boardId = 3L;
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            //when
            ResultActions resultActions = mvc.perform(
                    delete("/articles/delete/{boardId}", boardId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardDeleteMatch : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("이미 매칭된 공고글은 삭제 할 수 없습니다"));
        }
        @Test
        @DisplayName("실패 : 공고글의 작성자가 아닌 경우")
        void testBoardDeleteInvalidUser() throws Exception {
            // given
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            //when
            ResultActions resultActions = mvc.perform(
                    delete("/articles/delete/{boardId}", boardId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardDeleteInvalidUser : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("공고글의 작성자가 아닙니다"));
        }
        @Test
        @DisplayName("실패 : 공고글이 없는 경우")
        void testBoardDeleteNotFound() throws Exception {
            // given
            Long boardId = 100L;
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            //when
            ResultActions resultActions = mvc.perform(
                    delete("/articles/delete/{boardId}", boardId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardDeleteNotFound : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value(ErrorMessage.UNKNOWN_BOARD));
        }
    }

    @Nested
    class testBoardModify{
        @Test
        @DisplayName("성공 : 수정 값이 하나인 경우")
        void testBoardModify1() throws Exception {
            // given
            Long boardId = 6L;
            String store = "더벤티";
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            BoardModifyRqDTO requestDTO = BoardModifyRqDTO.builder()
                    .store(store)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardModify1 : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response.boardId").value(boardId));
            resultActions.andExpect(jsonPath("$.response.store").value(store)); // 수정
            resultActions.andExpect(jsonPath("$.response.destination").value("전남대 공대7 222호관"));
            resultActions.andExpect(jsonPath("$.response.beverage[0].name").value("카페라떼 1잔"));
            resultActions.andExpect(jsonPath("$.response.tip").value(2000));
            resultActions.andExpect(jsonPath("$.response.request").value("빨리 와주세요6"));
            resultActions.andExpect(jsonPath("$.response.isMatch").value(false));

        }

        @Test
        @DisplayName("성공 : 수정 값이 둘인 경우")
        void testBoardModify2() throws Exception {
            // given
            Long boardId = 6L;
            String store = "더벤티";
            int tip = 1;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            BoardModifyRqDTO requestDTO = BoardModifyRqDTO.builder()
                    .store(store)
                    .tip(tip)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardModify2 : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response.boardId").value(boardId));
            resultActions.andExpect(jsonPath("$.response.store").value(store)); // 수정
            resultActions.andExpect(jsonPath("$.response.destination").value("전남대 공대7 222호관"));
            resultActions.andExpect(jsonPath("$.response.beverage[0].name").value("카페라떼 1잔"));
            resultActions.andExpect(jsonPath("$.response.tip").value(tip)); // 수정
            resultActions.andExpect(jsonPath("$.response.request").value("빨리 와주세요6"));
            resultActions.andExpect(jsonPath("$.response.isMatch").value(false));
        }
        @Test
        @DisplayName("성공 : 공고글 전체를 수정하는 경우")
        void testBoardModify3() throws Exception {
            // given
            Long boardId = 6L;
            String store = "더벤티";
            beverages.add("아이스티 1잔");
            String destination = "정보마루";
            int tip = 1;
            String request = "천천히 조심해서 오세요 :)";
            String finishedAt = "2023-11-03 07:25";
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            BoardModifyRqDTO requestDTO = BoardModifyRqDTO.builder()
                    .store(store)
                    .beverage(beverages)
                    .destination(destination)
                    .tip(tip)
                    .request(request)
                    .finishedAt(finishedAt)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardModify3 : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response.boardId").value(boardId));
            resultActions.andExpect(jsonPath("$.response.store").value(store));
            resultActions.andExpect(jsonPath("$.response.destination").value(destination));
            resultActions.andExpect(jsonPath("$.response.beverage[0].name").value("아이스 아메리카노 1잔"));
            resultActions.andExpect(jsonPath("$.response.tip").value(tip));
            resultActions.andExpect(jsonPath("$.response.request").value(request));
            resultActions.andExpect(jsonPath("$.response.finishedAt").value("1698996300"));
            resultActions.andExpect(jsonPath("$.response.isMatch").value(false));
        }

        @Test
        @DisplayName("실패 : 수정할 값이 없는 경우")
        void testBoardModifyNoFields() throws Exception {
            // given
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            BoardModifyRqDTO requestDTO = BoardModifyRqDTO.builder()
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardModifyNoFields : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("수정할 값이 없습니다"));
        }
        @Test
        @DisplayName("실패 : 가게가 없는 경우")
        void testBoardModifyNotFoundStore() throws Exception {
            // given
            Long boardId = 6L;
            String store = "팬도로시";
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            BoardModifyRqDTO requestDTO = BoardModifyRqDTO.builder()
                    .store(store)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardModifyNotFoundStore : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value(ErrorMessage.UNKNOWN_STORE));
        }
        @Test
        @DisplayName("실패 : 작성자가 아닌 경우")
        void testBoardModifyInvalidUser() throws Exception {
            // given
            Long boardId = 6L;
            String store = "더벤티";
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            BoardModifyRqDTO requestDTO = BoardModifyRqDTO.builder()
                    .store(store)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardModifyInvalidUser : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("공고글의 작성자가 아닙니다"));
        }
        @Test
        @DisplayName("실패 : 이미 매칭된 공고글의 경우")
        void testBoardModifyMatch() throws Exception {
            // given
            Long boardId = 3L;
            String store = "더벤티";
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            BoardModifyRqDTO requestDTO = BoardModifyRqDTO.builder()
                    .store(store)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardModifyMatch : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("이미 매칭된 공고글은 수정 할 수 없습니다"));
        }
        @Test
        @DisplayName("실패 : 공고글이 없는 경우")
        void testBoardModifyNotFound() throws Exception {
            // given
            Long boardId = 100L;
            String store = "더벤티";
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            BoardModifyRqDTO requestDTO = BoardModifyRqDTO.builder()
                    .store(store)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardModifyNotFound : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("공고글을 찾을 수 없습니다"));
        }
        @Test
        @DisplayName("실패 : 가게에 공백('', ' ')이 들어오는경우")
        void testBoardModifyBlankStore() throws Exception {
            // given
            Long boardId = 6L;
            String store = " ";
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            BoardModifyRqDTO requestDTO = BoardModifyRqDTO.builder()
                    .store(store)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardModifyBlankStore : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("가게가 공백입니다"));
        }
        @Test
        @DisplayName("실패 : 음료에 공백('', ' ')이 들어오는경우")
        void testBoardModifyBlankBeverage() throws Exception {
            // given
            Long boardId = 6L;
            beverages.add(" ");
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            BoardModifyRqDTO requestDTO = BoardModifyRqDTO.builder()
                    .beverage(beverages)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardModifyBlankBeverage : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("음료가 공백입니다"));
        }

        @Test
        @DisplayName("실패 : 픽업팁이 음수인 경우")
        void testBoardModifyInvalidTip() throws Exception {
            // given
            Long boardId = 6L;
            int tip = -1;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            BoardModifyRqDTO requestDTO = BoardModifyRqDTO.builder()
                    .tip(tip)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    patch("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardModifyInvalidTip : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("픽업팁이 음수입니다"));
        }
    }
}