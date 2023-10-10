package pickup_shuttle.pickup.domain.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
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
import pickup_shuttle.pickup.domain.board.dto.request.BoardAgreeRqDTO;
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

    private  List<String> beverags = new ArrayList<>();

    @BeforeEach
    void beforEach() throws Exception {
        beverags.add("아이스 아메리카노");
        beverags.add("핫 아메리카노");
    }
    @Test //공고글 목록 조회
    void testBoardList() throws Exception {
        //given
        String accessToken = "Bearer " + jwtService.createAccessToken("2");
        String lastBoardId = "";
        String size = "10";

        //when
        ResultActions resultActions = mvc.perform(
                get("/articles")
                        .queryParam("lastBoardId", lastBoardId)
                        .queryParam("size", size)
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
        resultActions.andExpect(jsonPath("$.response.shopName").value("전남대 후문 스타벅스"));
        resultActions.andExpect(jsonPath("$.response.tip").value("1000"));
    }

    @Test // 공고글 상세 조회 (매칭 후)
    void testBoardDetailAfter() throws Exception {
        //given
        String accessToken = "Bearer " + jwtService.createAccessToken("1");
        Long boardId = 1L;

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
        resultActions.andExpect(jsonPath("$.response.boardId").value("1"));
        resultActions.andExpect(jsonPath("$.response.shopName").value("전남대 후문 스타벅스"));
        resultActions.andExpect(jsonPath("$.response.tip").value("1000"));
        resultActions.andExpect(jsonPath("$.response.isMatch").value("true"));
        resultActions.andExpect(jsonPath("$.response.pickerPhoneNumber").value("010-0000-1234"));
    }

    @Nested
    class testBoardWrite{
        @Test
        @DisplayName("성공")
        void testWrite() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("1");
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
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testWrite : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response.boardId").value("6"));
        }
        @Test
        @DisplayName("실패 : 서비스에서 제공하지 않는 가게의 경우")
        void testWriteNotFoundStore() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("1");
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
                            .header("Authorization", accessToken)
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
        @DisplayName("실패 : 가게가 공백인 경우")
        void testWriteBlankStore() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("1");
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
            String accessToken = "Bearer " + jwtService.createAccessToken("1");
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
            String accessToken = "Bearer " + jwtService.createAccessToken("1");
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
            String accessToken = "Bearer " + jwtService.createAccessToken("1");
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
            String accessToken = "Bearer " + jwtService.createAccessToken("1");
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
        // teardown.sql의 44번째 , 46번째 Query를 주석처리 하고 테스트를 진행한다.
        void testBoardAgree() throws Exception{
            //given
            Long boardId = 1L;
            String accessToken =  "Bearer " + jwtService.createAccessToken("2");
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
            resultActions.andExpect(jsonPath("$.response.beverage[0].name").value("핫 아메리카노"));
            resultActions.andExpect(jsonPath("$.response.beverage[1].name").value("아이스 아메리카노"));

        }

        @Test()
        @DisplayName("공고글 작성자가 매칭 수락 한 경우")
        void testFailBoardAgree() throws Exception {
            Long boardId = 1L;
            String accessToken = "Bearer " + jwtService.createAccessToken("1");
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
        @DisplayName("공고글이 이미 매칭 된 경우")
        // teardown.sql의 44번째 , 46번째 Query 주석을 풀고  테스트를 진행한다.
        void testFailBoardAgree2() throws Exception{
            //given
            Long boardId = 1L;
            String accessToken = "Bearer " + jwtService.createAccessToken("2");
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
    }
    @Nested
    class testBoardDelete {
        @Test
        @DisplayName("성공")
        void testBoardDelete() throws Exception {
            // given
            Long boardId = 4L;
            String accessToken = "Bearer " + jwtService.createAccessToken("2");
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
            Long boardId = 1L;
            String accessToken = "Bearer " + jwtService.createAccessToken("1");
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
            Long boardId = 1L;
            String accessToken = "Bearer " + jwtService.createAccessToken("2");
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
            String accessToken = "Bearer " + jwtService.createAccessToken("2");
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
            resultActions.andExpect(jsonPath("$.error.message").value("공고글을 찾을 수 없습니다"));
        }
    }
}