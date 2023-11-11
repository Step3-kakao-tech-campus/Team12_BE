package pickup_shuttle.pickup.domain.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.domain.RestDocsConfig;
import pickup_shuttle.pickup.domain.beverage.dto.request.BeverageRq;
import pickup_shuttle.pickup.domain.board.dto.request.AcceptBoardRq;
import pickup_shuttle.pickup.domain.board.dto.request.UpdateBoardRq;
import pickup_shuttle.pickup.domain.board.dto.request.CreateBoardRq;
import pickup_shuttle.pickup.security.service.JwtService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Sql("classpath:db/teardown.sql")
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BoardControllerTest extends RestDocsConfig {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private JwtService jwtService;

    private List<BeverageRq> beverages = new ArrayList<>();

    @BeforeEach
    void beforEach() {
        beverages.add(BeverageRq.builder()
                .name("아이스 아메리카노 1잔")
                .build());
        beverages.add(BeverageRq.builder()
                .name("핫 아메리카노 1잔")
                .build());
    }
    @Nested
    class testBoardList{
        @Test //공고글 목록 조회
        @DisplayName("성공 : limit 3인 경우 공고글 목록 조회")
        void testBoardList1() throws Exception {
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("2");
            String limit = "3";

            //when
            ResultActions resultActions = mvc.perform(
                    get("/articles")
                            .queryParam("limit", limit)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardList1 : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response.pageable.numberOfElements").value(3));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("성공 : limit 10인 경우 공고글 목록 조회")
        void testBoardList2() throws Exception {
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
            System.out.println("testBoardList2 : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("성공 : Query Parameter가 없는 경우 공고글 목록 조회")
        void testBoardList3() throws Exception {
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("2");

            //when
            ResultActions resultActions = mvc.perform(
                    get("/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardList3 : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("성공 : offset 3, limit 10 경우 공고글 목록 조회")
        void testBoardList4() throws Exception {
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("2");
            String offset = "3";
            String limit = "10";
            //when
            ResultActions resultActions = mvc.perform(
                    get("/articles")
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardList4 : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("성공 : offset이 0 경우 공고글 목록 조회")
        void testBoardList5() throws Exception {
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("2");
            String offset = "0";

            //when
            ResultActions resultActions = mvc.perform(
                    get("/articles")
                            .queryParam("offset", offset)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardList5 : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
            resultActions.andExpect(jsonPath("$.response.pageable.numberOfElements").value(0));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }

    }
    @Nested
    class testBoardDetail{
        @Test // 공고글 상세 조회 (매칭 전)
        @DisplayName("성공 : 공고글 상세 조회 (매칭 전)")
        void testBoardDetailBefore() throws Exception {
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("1");
            String boardId = "6";

            //when
            ResultActions resultActions = mvc.perform(
                    get("/articles/{boardId}", boardId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardDetailBefore : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
//            resultActions.andExpect(jsonPath("$.response.boardId").value(6));
//            resultActions.andExpect(jsonPath("$.response.shopName").value("메가MGC"));
//            resultActions.andExpect(jsonPath("$.response.tip").value(2000));
//            resultActions.andExpect(jsonPath("$.response.isMatch").value(false));
//            resultActions.andExpect(jsonPath("$.response.isRequester").value(false));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test // 공고글 상세 조회 (매칭 후)
        @DisplayName("성공 : 공고글 상세 조회 (매칭 후)")
        void testBoardDetailAfter() throws Exception {
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            Long boardId = 3L;

            //when
            ResultActions resultActions = mvc.perform(
                    get("/articles/{boardId}", boardId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardDetailAfter : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
//            resultActions.andExpect(jsonPath("$.response.boardId").value("3"));
//            resultActions.andExpect(jsonPath("$.response.shopName").value("더벤티"));
//            resultActions.andExpect(jsonPath("$.response.tip").value("2000"));
//            resultActions.andExpect(jsonPath("$.response.isMatch").value(true));
//            resultActions.andExpect(jsonPath("$.response.isRequester").value(true));
//            resultActions.andExpect(jsonPath("$.response.pickerPhoneNumber").value("010-0000-0000"));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 상세조회하려는 공고글의 boardId와 일치하는 board가 없는 경우")
        void testBoardDetailNotFoundBoard() throws Exception {
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            Long boardId = 100L;

            //when
            ResultActions resultActions = mvc.perform(
                    get("/articles/{boardId}", boardId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardDetailNotFoundBoard : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("공고글ID로 공고글을/를 찾을 수 없습니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(404));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }

    }

    @Nested
    class testBoardCreate{
        @Test
        @DisplayName("성공 : 공고글 작성하기")
        void testCreate() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("스타벅스")
                    .beverages(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreate : " + responseBody);

            //then
//            resultActions.andExpect(jsonPath("$.success").value("true"));
//            resultActions.andExpect(jsonPath("$.response.boardId").value("7"));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 서비스에서 제공하지 않는 가게의 경우")
        void testCreateNotFoundStore() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("팬도로시")
                    .beverages(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateNotFoundStore : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value(String.format(ErrorMessage.NOTFOUND_FORMAT, "가게명", "가게")));
            resultActions.andExpect(jsonPath("$.error.status").value(404));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 가게명의 길이가 60을 초과하는 경우")
        void testCreateMaxShopName() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("팬도로시팬도로시팬도로시팬도로시팬도로시팬도로시팬도로시팬도로시팬도로시팬도로시팬도로시팬도로시팬도로시팬도로시팬도로시팬도로시")
                    .beverages(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateMaxShopName : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("가게" + ErrorMessage.BADREQUEST_SIZE));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 가게명이 공백인 경우")
        void testCreateBlankShopName() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName(" ")
                    .beverages(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateBlankShopName : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("가게" + ErrorMessage.BADREQUEST_BLANK));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 음료명의 길이가 60을 초과하는 경우")
        void testCreateMaxBeverageName() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            beverages.add(BeverageRq.builder()
                    .name("아이스 아메리카노                                                                 1잔")
                    .build());
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("스타벅스")
                    .beverages(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateMaxBeverageName : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("음료" + ErrorMessage.BADREQUEST_SIZE));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 음료명이 공백인 경우")
        void testCreateBlankBeverage() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            beverages.add(BeverageRq.builder()
                    .name("")
                    .build());
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("스타벅스")
                    .beverages(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateBlankBeverage : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("음료" + ErrorMessage.BADREQUEST_BLANK));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 음료가 없는 경우")
        void testCreateBlankBeverages() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            beverages.clear();
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("스타벅스")
                    .beverages(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateBlankBeverages : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("음료" + ErrorMessage.BADREQUEST_EMPTY));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 위치의 길이가 60을 초과하는 경우")
        void testCreateMaxDestination() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("스타벅스")
                    .beverages(beverages)
                    .destination("공                                               과                         대학")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateBlankDestination : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("위치" + ErrorMessage.BADREQUEST_SIZE));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 위치가 공백인 경우")
        void testCreateBlankDestination() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("스타벅스")
                    .beverages(beverages)
                    .destination(" ")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateBlankDestination : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("위치" + ErrorMessage.BADREQUEST_BLANK));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 픽업팁이 0 이하인 경우")
        void testCreateMinTip() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("스타벅스")
                    .beverages(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(0)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateInvalidTip : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("픽업팁" + ErrorMessage.BADREQUEST_MIN));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 요청사항의 길이가 60을 초과하는 경우")
        void testCreateMaxRequest() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("스타벅스")
                    .beverages(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateMaxRequest : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("요청사항" + ErrorMessage.BADREQUEST_SIZE));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 마감기간이 공백인 경우")
        void testCreateBlankFinishAt() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("스타벅스")
                    .beverages(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateBlankFinishAt : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("마감기간" + ErrorMessage.BADREQUEST_BLANK));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 마감기간의 형식이 yyyy-MM-dd HH:mm이 아닌 경우")
        void testCreateInvalidFinishedAt() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("스타벅스")
                    .beverages(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023:11:18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateBlankFinishAt : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("마감기간은 yyyy-MM-dd HH:mm 형식이어야 합니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 공고글을 작성하려는 유저의 userId와 일치하는 유저가 없는 경우")
        void testCreateNotFoundUser() throws Exception{
            //given
            String accessToken = "Bearer " + jwtService.createAccessToken("100");
            CreateBoardRq boardCreateRqDTO = CreateBoardRq.builder()
                    .shopName("스타벅스")
                    .beverages(beverages)
                    .destination("전남대 공대 시계탑")
                    .tip(1000)
                    .request("후딱후딱 갖다주십쇼!!!!!!!!!!!!!!")
                    .finishedAt("2023-11-18 19:00")
                    .build();
            String requestBody = om.writeValueAsString(boardCreateRqDTO);

            //when
            ResultActions resultActions = mvc.perform(
                    post("/articles/write")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );

            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testCreateNotFoundUser : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value(String.format(ErrorMessage.NOTFOUND_FORMAT, "유저ID", "유저")));
            resultActions.andExpect(jsonPath("$.error.status").value(404));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
    }

    @Nested
    class testBoardAccept {
        @Test
        @DisplayName("성공 : 공고글 수락")
        void testBoardAccept() throws Exception{
            //given
            Long boardId = 6L;
            String accessToken =  "Bearer " + jwtService.createAccessToken("3");
            AcceptBoardRq requestDTO = AcceptBoardRq.builder()
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
            System.out.println("testBoardAccept : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("true"));
//            resultActions.andExpect(jsonPath("$.response.shopName").value("메가MGC"));
//            resultActions.andExpect(jsonPath("$.response.destination").value("전남대 공대7 222호관"));
//            resultActions.andExpect(jsonPath("$.response.beverages[0].name").value("카페라떼 1잔"));
//            resultActions.andExpect(jsonPath("$.response.tip").value("2000"));
//            resultActions.andExpect(jsonPath("$.response.request").value("빨리 와주세요6"));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test()
        @DisplayName("실패 : 수락하려는 공고글의 boardId로 공고글을 찾을 수 없는 경우")
        void testBoardAcceptNotFoundBoard() throws Exception {
            Long boardId = 100L;
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            AcceptBoardRq requestDTO = AcceptBoardRq.builder()
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
            System.out.println("testBoardAcceptNotFoundBoard : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value(String.format(ErrorMessage.NOTFOUND_FORMAT, "공고글ID", "공고글")));
            resultActions.andExpect(jsonPath("$.error.status").value(404));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test()
        @DisplayName("실패 : 공고글을 수락하려는 유저의 userId와 일치하는 유저가 없는 경우")
        void testBoardAcceptNotFoundUser() throws Exception {
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("100");
            AcceptBoardRq requestDTO = AcceptBoardRq.builder()
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
            System.out.println("testBoardAcceptNotFoundBoard : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value(String.format(ErrorMessage.NOTFOUND_FORMAT, "유저ID", "유저")));
            resultActions.andExpect(jsonPath("$.error.status").value(404));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test()
        @DisplayName("실패 : 도착예정시간이 0 이하인 경우")
        void testBoardAcceptMinArraivalTime() throws Exception {
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            AcceptBoardRq requestDTO = AcceptBoardRq.builder()
                    .arrivalTime(0)
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
            System.out.println("testBoardAcceptMinArraivalTime : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value( "도착예정시간" + ErrorMessage.BADREQUEST_MIN));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

        }
        @Test()
        @DisplayName("실패 : 공고글을 수락하려는 유저가 공고글의 작성자인 경우")
        void testBoardAcceptInvalidUser() throws Exception {
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            AcceptBoardRq requestDTO = AcceptBoardRq.builder()
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
            System.out.println("testBoardAcceptInvalidUser : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("공고글 작성자가 매칭 수락을 시도하는 경우 공고글을 수락 할 수 없습니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(403));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }

        @Test
        @DisplayName("실패 : 수락하려는 공고글이 이미 매칭된 경우")
        void testBoardAcceptMatchedBoard() throws Exception{
            //given
            Long boardId = 3L;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            AcceptBoardRq requestDTO = AcceptBoardRq.builder()
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
            System.out.println("testBoardAcceptMatchedBoard : " + responseBody);

            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("공고글이 이미 매칭된 경우 공고글을 수락할 수 없습니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(403));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
    }
    @Nested
    class testBoardDelete {
        @Test
        @DisplayName("성공 : 공고글 삭제")
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
//            resultActions.andExpect(jsonPath("$.response.message").value("공고글 삭제를 완료하였습니다"));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 삭제하려는 공고글의 boardId로 공고글을 찾을 수 없는 경우")
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
            resultActions.andExpect(jsonPath("$.error.message").value(String.format(ErrorMessage.NOTFOUND_FORMAT, "공고글ID", "공고글")));
            resultActions.andExpect(jsonPath("$.error.status").value(404));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 공고글을 삭제하려는 유저가 공고글의 작성자가 아닌 경우")
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
            resultActions.andExpect(jsonPath("$.error.message").value("공고글의 작성자가 아닌 경우 공고글을 삭제할 수 없습니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(403));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 삭제하려는 공고글이 이미 매칭된 경우")
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
            resultActions.andExpect(jsonPath("$.error.message").value("공고글이 이미 매칭된 경우 공고글을 삭제할 수 없습니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(403));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }

    }

    @Nested
    class testBoardUpdate{
//        @Test
//        @DisplayName("성공 : 수정 값이 하나인 경우")
//        void testBoardUpdate1() throws Exception {
//            // given
//            Long boardId = 6L;
//            String shopName = "더벤티";
//            String accessToken = "Bearer " + jwtService.createAccessToken("6");
//            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
//                    .shopName(shopName)
//                    .build();
//            String requestBody = om.writeValueAsString(requestDTO);
//            //when
//            ResultActions resultActions = mvc.perform(
//                    put("/articles/modify/{boardId}", boardId)
//                            .content(requestBody)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .header("Authorization", accessToken)
//            );
//            //eye
//            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//            System.out.println("testBoardUpdate1 : " + responseBody);
//
//            //then
//            resultActions.andExpect(jsonPath("$.success").value("true"));
//            resultActions.andExpect(jsonPath("$.response.boardId").value(boardId));
//            resultActions.andExpect(jsonPath("$.response.shopName").value(shopName)); // 수정
//            resultActions.andExpect(jsonPath("$.response.destination").value("전남대 공대7 222호관"));
//            resultActions.andExpect(jsonPath("$.response.beverages[0].name").value("카페라떼 1잔"));
//            resultActions.andExpect(jsonPath("$.response.tip").value(2000));
//            resultActions.andExpect(jsonPath("$.response.request").value("빨리 와주세요6"));
//            resultActions.andExpect(jsonPath("$.response.isMatch").value(false));
//            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//        }
//
//        @Test
//        @DisplayName("성공 : 수정 값이 둘인 경우")
//        void testBoardUpdate2() throws Exception {
//            // given
//            Long boardId = 6L;
//            String shopName = "더벤티";
//            int tip = 1;
//            String accessToken = "Bearer " + jwtService.createAccessToken("6");
//            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
//                    .shopName(shopName)
//                    .tip(tip)
//                    .build();
//            String requestBody = om.writeValueAsString(requestDTO);
//            //when
//            ResultActions resultActions = mvc.perform(
//                    put("/articles/modify/{boardId}", boardId)
//                            .content(requestBody)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .header("Authorization", accessToken)
//            );
//            //eye
//            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//            System.out.println("testBoardUpdate2 : " + responseBody);
//
//            //then
//            resultActions.andExpect(jsonPath("$.success").value("true"));
//            resultActions.andExpect(jsonPath("$.response.boardId").value(boardId));
//            resultActions.andExpect(jsonPath("$.response.shopName").value(shopName)); // 수정
//            resultActions.andExpect(jsonPath("$.response.destination").value("전남대 공대7 222호관"));
//            resultActions.andExpect(jsonPath("$.response.beverages[0].name").value("카페라떼 1잔"));
//            resultActions.andExpect(jsonPath("$.response.tip").value(tip)); // 수정
//            resultActions.andExpect(jsonPath("$.response.request").value("빨리 와주세요6"));
//            resultActions.andExpect(jsonPath("$.response.isMatch").value(false));
//            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//        }
        @Test
        @DisplayName("성공 : 공고글 전체를 수정하는 경우")
        void testBoardUpdateAll() throws Exception {
            // given
            Long boardId = 6L;
            String shopName = "더벤티";
            beverages.add(BeverageRq.builder()
                    .name("아이스티 1잔")
                    .build());
            String destination = "정보마루";
            int tip = 1;
            String request = "천천히 조심해서 오세요 :)";
            String finishedAt = "2023-11-03 07:25";
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .shopName(shopName)
                    .beverages(beverages)
                    .destination(destination)
                    .tip(tip)
                    .request(request)
                    .finishedAt(finishedAt)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdate3 : " + responseBody);

            //then
//            resultActions.andExpect(jsonPath("$.success").value("true"));
//            resultActions.andExpect(jsonPath("$.response.boardId").value(boardId));
//            resultActions.andExpect(jsonPath("$.response.shopName").value(shopName));
//            resultActions.andExpect(jsonPath("$.response.destination").value(destination));
//            resultActions.andExpect(jsonPath("$.response.beverages[0].name").value("아이스 아메리카노 1잔"));
//            resultActions.andExpect(jsonPath("$.response.tip").value(tip));
//            resultActions.andExpect(jsonPath("$.response.request").value(request));
//            resultActions.andExpect(jsonPath("$.response.finishedAt").value("1698996300"));
//            resultActions.andExpect(jsonPath("$.response.isMatch").value(false));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 수정하려는 공고글의 boardId로 공고글을 찾을 수 없는 경우")
        void testBoardUpdateNotFound() throws Exception {
            // given
            Long boardId = 100L;
            String shopName = "더벤티";
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .shopName(shopName)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateNotFound : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value(String.format(ErrorMessage.NOTFOUND_FORMAT, "공고글ID", "공고글")));
            resultActions.andExpect(jsonPath("$.error.status").value(404));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 공고글을 수정하려는 유저가 공고글의 작성자가 아닌 경우")
        void testBoardUpdateInvalidUser() throws Exception {
            // given
            Long boardId = 6L;
            String shopName = "더벤티";
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .shopName(shopName)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateInvalidUser : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("공고글의 작성자가 아닌 경우 공고글을 수정할 수 없습니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(403));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 수정하려는 공고글이 이미 매칭된 경우")
        void testBoardUpdateMatch() throws Exception {
            // given
            Long boardId = 3L;
            String shopName = "더벤티";
            String accessToken = "Bearer " + jwtService.createAccessToken("3");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .shopName(shopName)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateMatch : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("공고글이 이미 매칭된 경우 공고글을 수정할 수 없습니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(403));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 서비스에서 제공하지 않는 가게의 경우")
        void testBoardUpdateNotFoundStore() throws Exception {
            // given
            Long boardId = 6L;
            String shopName = "팬도로시";
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .shopName(shopName)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateNotFoundStore : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value(String.format(ErrorMessage.NOTFOUND_FORMAT, "가게명", "가게")));
            resultActions.andExpect(jsonPath("$.error.status").value(404));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 수정할 값이 없는 경우")
        void testBoardUpdateNoFields() throws Exception {
            // given
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateNoFields : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("수정할 값이 없는 경우 공고글을 수정할 수 없습니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(403));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 가게명이 공백인 경우")
        void testBoardUpdateBlankShopName() throws Exception {
            // given
            Long boardId = 6L;
            String shopName = " ";
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .shopName(shopName)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateBlankStore : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("가게" + ErrorMessage.BADREQUEST_BLANK));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 가게명의 길이가 60을 초과하는 경우")
        void testBoardUpdateMaxShopName() throws Exception {
            // given
            Long boardId = 6L;
            String shopName = "더                                  벤                        티";
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .shopName(shopName)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateBlankStore : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("가게" + ErrorMessage.BADREQUEST_SIZE));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 음료명이 공백인 경우")
        void testBoardUpdateBlankBeverage() throws Exception {
            // given
            Long boardId = 6L;
            beverages.add(BeverageRq.builder()
                    .name(" ")
                    .build());
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .beverages(beverages)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateBlankBeverage : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("음료" + ErrorMessage.BADREQUEST_BLANK));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 음료명의 길이가 60을 초과하는 경우")
        void testBoardUpdateMaxBeverage() throws Exception {
            // given
            Long boardId = 6L;
            beverages.add(BeverageRq.builder()
                    .name("아메리카노                                                               1잔 ")
                    .build());
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .beverages(beverages)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateBlankBeverage : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("음료" + ErrorMessage.BADREQUEST_SIZE));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 음료가 없는 경우")
        void testBoardUpdateBlankBeverages() throws Exception {
            // given
            Long boardId = 6L;
            beverages.clear();
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .beverages(beverages)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateBlankBeverage : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("음료" + ErrorMessage.BADREQUEST_EMPTY));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 위치가 공백인 경우")
        void testBoardUpdateBlankDestination() throws Exception {
            // given
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .destination(" ")
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateBlankBeverage : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("위치" + ErrorMessage.BADREQUEST_BLANK));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 위치의 길이가 60을 초과하는 경우")
        void testBoardUpdateMaxDestination() throws Exception {
            // given
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .destination("공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대공대")
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateBlankBeverage : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("위치" + ErrorMessage.BADREQUEST_SIZE));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 픽업팁이 0 이하인 경우")
        void testBoardUpdateMinTip() throws Exception {
            // given
            Long boardId = 6L;
            int tip = 0;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .tip(tip)
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateInvalidTip : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("픽업팁" + ErrorMessage.BADREQUEST_MIN));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 요청사항의 길이가 60을 초과하는 경우")
        void testBoardUpdateMaxRequest() throws Exception {
            // given
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .request("요청사항요청사항요청사항요청사항요청사항요청사항요청사항요청사항요청사항요청사항요청사항요청사항요청사항요청사항요청사항요청사항")
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateInvalidTip : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("요청사항" + ErrorMessage.BADREQUEST_SIZE));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
        @Test
        @DisplayName("실패 : 마감기간의 형식이 yyyy-MM-dd HH:mm이 아닌 경우")
        void testBoardUpdateInvalidFinishedAt() throws Exception {
            // given
            Long boardId = 6L;
            String accessToken = "Bearer " + jwtService.createAccessToken("6");
            UpdateBoardRq requestDTO = UpdateBoardRq.builder()
                    .finishedAt("2023-11-18 19:00:00")
                    .build();
            String requestBody = om.writeValueAsString(requestDTO);
            //when
            ResultActions resultActions = mvc.perform(
                    put("/articles/modify/{boardId}", boardId)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
            );
            //eye
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println("testBoardUpdateInvalidTip : " + responseBody);
            //then
            resultActions.andExpect(jsonPath("$.success").value("false"));
            resultActions.andExpect(jsonPath("$.error.message").value("마감기간은 yyyy-MM-dd HH:mm 형식이어야 합니다"));
            resultActions.andExpect(jsonPath("$.error.status").value(400));
            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
        }
    }
}