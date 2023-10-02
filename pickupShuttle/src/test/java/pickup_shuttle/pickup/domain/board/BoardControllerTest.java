package pickup_shuttle.pickup.domain.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        String boardId = "1";

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

}
