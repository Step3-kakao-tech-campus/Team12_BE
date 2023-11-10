package pickup_shuttle.pickup.domain.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pickup_shuttle.pickup._core.utils.ApiUtils;
import pickup_shuttle.pickup._core.utils.CustomPage;
import pickup_shuttle.pickup.config.Login;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
import pickup_shuttle.pickup.domain.user.dto.request.*;
import pickup_shuttle.pickup.domain.user.dto.response.*;


@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    // 은행명, 계좌번호 입력 창으로 이동
    @GetMapping("/users/register/input")
    public ModelAndView userInfoInput() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("registerInput");

        return modelAndView;
    }

    // 에러창
    @GetMapping("/errorPage")
    public ModelAndView error() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("errorPage");

        return modelAndView;
    }
    // 메인창
    @GetMapping("/")
    public ModelAndView mainPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");

        return modelAndView;
    }


    @PostMapping("/signup")
    public ResponseEntity<ApiUtils.ApiResult<CreateUserRp>> signup(@RequestBody CreateUserRq requestDTO, @AuthenticationPrincipal CustomOauth2User customOauth2User, Errors errors){
        CreateUserRp responseDTO = userService.signup(requestDTO, customOauth2User);

        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }

    @GetMapping("/login/callback")
    public ResponseEntity<ApiUtils.ApiResult<LoginUserRp>> callBack(Authentication authentication){
       LoginUserRp responseDTO = userService.login(authentication);
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 유저 인증 상태 (인증/미인증/인증 진행 중) 반환
    @GetMapping("/mypage/auth")
    public ResponseEntity<ApiUtils.ApiResult<String>> userAuthStatus(@Login Long userId){
        String status = userService.userAuthStatus(userId);
        return ResponseEntity.ok(ApiUtils.success(status));
    }

    @GetMapping("/modify")
    public ModelAndView userModify() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("userModify");

        return modelAndView;
    }

    @PutMapping("/mypage/modify")
    public ResponseEntity<ApiUtils.ApiResult<UpdateUserRp>> userModify(@Login Long id , @RequestBody @Valid UpdateUserRq updateUserRq){
        UpdateUserRp responseDTO = userService.modifyUser(updateUserRq, id);
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 이미지 업로드
    @PutMapping("/mypage/image/url")
    public ResponseEntity<ApiUtils.ApiResult<String>> uploadImage(@ModelAttribute @Valid UpdateUserImageRq requestDTO,
                                       @Login Long userId) {
        userService.uploadImage(requestDTO.image(), userId);
        return ResponseEntity.ok(ApiUtils.success("이미지 url 저장이 완료되었습니다"));
    }
    // presigendUrl(GET) 발급
    @GetMapping("/mypage/image/url")
    public ResponseEntity<ApiUtils.ApiResult<GetUserImageRp>> getImageUrl(@Login Long userId) {
        GetUserImageRp responseDTO = userService.getImageUrl(userId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }


    @GetMapping("/mypage")
    public ResponseEntity<ApiUtils.ApiResult<ReadMypageRp>> myPage(@Login Long userId) {
        ReadMypageRp responseDTO = userService.myPage(userId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }
    @GetMapping("/admin/auth/list")
    public ResponseEntity<ApiUtils.ApiResult<CustomPage<ReadUserAuthListRp>>> getAuthList(
            @RequestParam(value = "offset",required = false) Long lastUserId,
            @RequestParam(value = "limit",defaultValue = "10") int size){
        Slice<ReadUserAuthListRp> responseDTOSlice = userService.getAuthList(lastUserId, size);
        return ResponseEntity.ok(ApiUtils.success(new CustomPage<>(responseDTOSlice)));
    }
    @GetMapping("/admin/auth/list/{userId}")
    public ResponseEntity<ApiUtils.ApiResult<ReadUserAuthRp>> getAuthDetail(@PathVariable("userId") Long userId){
        ReadUserAuthRp responseDTO = userService.getAuthDetail(userId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }
    @PutMapping("/admin/auth/approval")
    public ResponseEntity<ApiUtils.ApiResult<String>> authApprove(@RequestBody @Valid ApproveUserRq requestDTO){
        String message = userService.authApprove(requestDTO);
        return ResponseEntity.ok(ApiUtils.success(message));
    }

    @PutMapping("/admin/auth/reject")
    public ResponseEntity<ApiUtils.ApiResult<RejectUserAuthRp>> authReject(@RequestBody @Valid RejectUserRq requestDTO){
        RejectUserAuthRp message = userService.authReject(requestDTO);
        return ResponseEntity.ok(ApiUtils.success(message));
    }

    @GetMapping("/mypage/requester/list")
    public ResponseEntity<ApiUtils.ApiResult<CustomPage<ReadWriterBoardListRp>>> getRequesterList(
            @Login Long userId,
            @RequestParam(value = "offset",required = false) Long lastBoardId,
            @RequestParam(value = "limit",defaultValue = "10") int size){
        Slice<ReadWriterBoardListRp> responseDTO = userService.getRequesterList(userId, lastBoardId, size);
        return ResponseEntity.ok(ApiUtils.success(new CustomPage<>(responseDTO)));
    }
    @GetMapping("/mypage/requester/detail/{boardId}")
    public ResponseEntity<ApiUtils.ApiResult<ReadWriterBoardRp>> getRequesterDetail(@PathVariable("boardId") Long boardId){
        ReadWriterBoardRp responseDTO = userService.getRequesterDetail(boardId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }
    @GetMapping("/mypage/picker/list")
    public ResponseEntity<ApiUtils.ApiResult<CustomPage<ReadPickerBoardListRp>>> myPagePickerList(@RequestParam(value = "offset",required = false) Long lastBoardId,
                                                                                                  @RequestParam(value = "limit",defaultValue = "10") int size,
                                                                                                  @Login Long userId) {
        Slice<ReadPickerBoardListRp> responseDTO = userService.myPagePickerList(lastBoardId,size,userId);
        return ResponseEntity.ok(ApiUtils.success(new CustomPage<>(responseDTO)));
    }
    @GetMapping("/mypage/picker/list/{boardId}")
    public ResponseEntity<ApiUtils.ApiResult<ReadPickerBoardRp>> myPagePickerDetail(@PathVariable("boardId") Long boardId,
                                                                                    @Login Long userId) {
        return ResponseEntity.ok(ApiUtils.success(userService.pickerBoardDetail(boardId, userId)));
    }
}