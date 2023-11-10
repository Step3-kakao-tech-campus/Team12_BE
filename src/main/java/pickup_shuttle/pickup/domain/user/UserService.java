package pickup_shuttle.pickup.domain.user;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup._core.errors.exception.Exception500;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.domain.beverage.dto.Beverage;
import pickup_shuttle.pickup.domain.board.Board;
import pickup_shuttle.pickup.domain.board.repository.BoardRepository;
import pickup_shuttle.pickup.domain.board.repository.BoardRepositoryCustom;
import pickup_shuttle.pickup.domain.match.Match;
import pickup_shuttle.pickup.domain.match.MatchRepository;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
import pickup_shuttle.pickup.domain.user.dto.request.ApproveUserRq;
import pickup_shuttle.pickup.domain.user.dto.request.CreateUserRq;
import pickup_shuttle.pickup.domain.user.dto.request.RejectUserRq;
import pickup_shuttle.pickup.domain.user.dto.request.UpdateUserRq;
import pickup_shuttle.pickup.domain.user.dto.response.*;
import pickup_shuttle.pickup.domain.user.repository.UserRepository;
import pickup_shuttle.pickup.domain.user.repository.UserRepositoryCustom;
import pickup_shuttle.pickup.domain.utils.Utils;
import pickup_shuttle.pickup.security.service.JwtService;

import java.io.InputStream;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserRepositoryCustom userRepositoryCustom;
    private final BoardRepository boardRepository;
    private final MatchRepository matchRepository;
    private final AmazonS3 amazonS3;
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final JwtService jwtService;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.dir}")
    private String dir;

    @Transactional
    public CreateUserRp signup(CreateUserRq createUserRq, CustomOauth2User customOauth2User) {
        String bankName = createUserRq.bankName();
        String accountNum = createUserRq.accountNum();
        User user = userRepository.findBySocialId(customOauth2User.getName()).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        user.setRole(UserRole.USER);
        customOauth2User.setBankName(bankName);
        customOauth2User.setAccountNum(accountNum);
        user.setBank(bankName);
        user.setAccount(accountNum);
        return CreateUserRp.builder()
                .message("회원가입이 완료되었습니다")
                .nickname(user.getNickname())
                .build();
    }

    public String userAuthStatus(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        String userRole = user.getUserRole().getValue();
        String userUrl = user.getUrl();
        return switch (userRole) {
            case "ROLE_USER" -> userUrl.isEmpty() ? "미인증" : "인증 진행 중";
            case "ROLE_STUDENT" -> "인증";
            default -> "미인증";
        };
    }

    @Transactional
    public UpdateUserRp modifyUser(UpdateUserRq updateUserRq, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            throw new Exception400("인증되지 않은 사용자 입니다");
        }
        String userBankName = user.get().getBank();
        String userAccountNum = user.get().getAccount();
        if(!updateUserRq.account().equals(userAccountNum)){
            user.get().setAccount(updateUserRq.account());
        }
        if(!updateUserRq.bank().equals(userBankName)){
            user.get().setBank(updateUserRq.bank());
        }
        return UpdateUserRp.builder()
                .response("회원 수정이 완료되었습니다")
                .build();
    }


    @Transactional
    public void uploadImage(MultipartFile multipartFile, Long userId) {
        // 메타 데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        if (!isImage(multipartFile.getContentType())) {
            throw new Exception400("이미지 파일이 아닙니다");
        }
        metadata.setContentType(multipartFile.getContentType());
        // 파일 읽기
        InputStream inputStream;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (Exception e) {
            throw new Exception400("파일을 읽을 수 없습니다");
        }
        // 유저 검증
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        if (user.getUserRole().getValue().equals("ROLE_STUDENT"))
            throw new Exception400("이미 인증된 유저입니다");
        // 업로드
        try {
            String fileName = dir + userId + ".jpg"; // 파일명 : {userId}.jpg
            amazonS3.putObject(bucket, fileName, inputStream, metadata); // aws
            user.updateUrl(amazonS3.getUrl(bucket, fileName).toString()); // mysql
        } catch (Exception e) {
            throw new Exception500("AWS 이미지 업로드를 실패했습니다");
        }
    }

    public GetUserImageRp getImageUrl(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        if (user.getUrl().isEmpty()) {
            throw new Exception400("등록된 이미지가 존재하지 않습니다");
        }
        return GetUserImageRp.builder().url(getPresignedUrl(userId)).build(); // PreSigned URL 응답
    }

    // PreSigned URL 발급
    private String getPresignedUrl(Long userId) {

        String fileName = dir + userId + ".jpg";
        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucket, fileName);
        try {
            return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
        } catch (Exception e) {
            throw new Exception500("presigned url 발급을 실패했습니다");
        }
    }

    // PreSigned URL(GET) 요청 생성
    private GeneratePresignedUrlRequest getGeneratePreSignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, fileName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(getPreSignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString());
        return generatePresignedUrlRequest;
    }

    // URL 만료 시간
    private Date getPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 5; // 5분
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    private boolean isImage(String fileType) {
        String[] imageTypes = {"image/jpeg", "image/png", "image/gif"};
        for (String type : imageTypes) {
            if (type.equals(fileType)) {
                return true;
            }
        }
        return false;
    }

    public ReadMypageRp myPage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        return ReadMypageRp.builder()
                .role(user.getUserRole().getValue())
                .nickname(user.getNickname())
                .build();
    }

    public Slice<ReadUserAuthListRp> getAuthList(Long lastUserId, int size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<User> userSlice = userRepositoryCustom.searchAuthList(lastUserId, pageRequest);

        List<ReadUserAuthListRp> readUserAuthListRpDTOList = userSlice.getContent().stream()
                .filter(u -> !u.getUrl().isEmpty())
                .map(u -> ReadUserAuthListRp.builder()
                        .userId(u.getUserId())
                        .nickname(u.getNickname())
                        .build())
                .toList();
        return new SliceImpl<>(readUserAuthListRpDTOList, pageRequest, userSlice.hasNext());
    }

    public ReadUserAuthRp getAuthDetail(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        if (user.getUrl().isEmpty()) {
            throw new Exception400("등록된 이미지가 존재하지 않습니다");
        }
        return ReadUserAuthRp.builder()
                .nickname(user.getNickname())
                .url(getPresignedUrl(userId))
                .build();
    }

    @Transactional
    public String authApprove(ApproveUserRq requestDTO) {
        User user = userRepository.findById(requestDTO.userId()).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        if (user.getUserRole() == UserRole.USER) {
            user.updateRole(UserRole.STUDENT);
            return "학생 인증이 승인되었습니다";
        } else throw new Exception400("일반 회원이 아닙니다");
    }

    @Transactional
    public RejectUserAuthRp authReject(RejectUserRq requestDTO) {
        User user = userRepository.findById(requestDTO.userId()).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        if (user.getUserRole() == UserRole.USER) {
           user.updateUrl("");
           return RejectUserAuthRp.builder()
                   .message("학생 인증이 거절되었습니다")
                   .build();
        } else throw new Exception400("일반 회원이 아닙니다");
    }

    public Slice<ReadWriterBoardListRp> getRequesterList(Long userId, Long lastBoardId, int size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<Board> boardSlice = userRepositoryCustom.searchRequesterList(userId, lastBoardId, pageRequest);
        List<ReadWriterBoardListRp> responseDTOList = boardSlice.getContent().stream()
                .filter(Utils::notOverDeadline)
                .map(b -> ReadWriterBoardListRp.builder()
                        .boardId(b.getBoardId())
                        .shopName(b.getStore().getName())
                        .destination(b.getDestination())
                        .finishedAt(b.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                        .tip(b.getTip())
                        .match(b.isMatch())
                        .build())
                .toList();
        return new SliceImpl<>(responseDTOList, pageRequest, boardSlice.hasNext());
    }

    public ReadWriterBoardRp getRequesterDetail(Long boardId) {
        Board board = boardRepository.m4findByBoardId(boardId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_BOARD)
        );
        List<Beverage> beverage = board.getBeverages().stream()
                .map(b -> Beverage.builder()
                        .name(b.getName())
                        .build()
                )
                .toList();
        if (board.isMatch()) {
            Match match = matchRepository.mfindByMatchId(board.getMatch().getMatchId()).orElseThrow(
                    () -> new Exception400("매칭 정보를 찾을 수 없습니다")
            );
            return ReadWriterBoardRp.builder()
                    .boardId(boardId)
                    .shopName(board.getStore().getName())
                    .destination(board.getDestination())
                    .beverage(beverage)
                    .tip(board.getTip())
                    .request(board.getRequest())
                    .finishedAt(board.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                    .isMatch(board.isMatch())
                    .pickerBank(match.getUser().getBank())
                    .pickerAccount(match.getUser().getAccount())
                    .arrivalTime(match.getMatchTime().plusMinutes(board.getMatch().getArrivalTime()).toEpochSecond(ZoneOffset.UTC))
                    .pickerPhoneNumber(match.getUser().getPhoneNumber())
                    .build();
        } else {
            return ReadWriterBoardRp.builder()
                    .boardId(boardId)
                    .shopName(board.getStore().getName())
                    .destination(board.getDestination())
                    .beverage(beverage)
                    .tip(board.getTip())
                    .request(board.getRequest())
                    .finishedAt(board.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                    .isMatch(board.isMatch())
                    .build();
        }

    }


    public Slice<ReadPickerBoardListRp> myPagePickerList(Long lastBoardId, int limit, Long userId) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        Slice<Board> boardsSlice = boardRepositoryCustom.searchAllBySlice2(lastBoardId, pageRequest, userId);
        if(boardsSlice.getContent().isEmpty()) {
            throw new Exception400("수락한 공고글이 없습니다");
        }
        return getPickerListResponseDTOs(pageRequest, boardsSlice);
    }

    private Slice<ReadPickerBoardListRp> getPickerListResponseDTOs(PageRequest pageRequest, Slice<Board> boardSlice) {
        List<ReadPickerBoardListRp> boardBoardListRpDTO = boardSlice.getContent().stream()
                .filter(Utils::notOverDeadline)
                .map(b -> ReadPickerBoardListRp.builder()
                        .boardId(b.getBoardId())
                        .shopName(b.getStore().getName())
                        .finishedAt(b.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                        .tip(b.getTip())
                        .isMatch(b.isMatch())
                        .destination(b.getDestination())
                        .build())
                .toList();
        return new SliceImpl<>(boardBoardListRpDTO,pageRequest,boardSlice.hasNext());
    }

    public ReadPickerBoardRp pickerBoardDetail(Long boardId, Long userId) {
        Board board = boardRepository.m5findByBoardId(boardId).orElseThrow(
                () -> new Exception400("매칭이 완료되지 않은 공고글입니다")
        );

        if(!board.getMatch().getUser().getUserId().equals(userId)){
            throw new Exception400("해당 공고글의 피커가 아닙니다");
        }
        List<Beverage> beverageList = board.getBeverages().stream()
                .map(b -> Beverage.builder()
                        .name(b.getName())
                        .build())
                .toList();
        return ReadPickerBoardRp.builder()
                .boardId(board.getBoardId())
                .shopName(board.getStore().getName())
                .destination(board.getDestination())
                .beverage(beverageList)
                .tip(board.getTip())
                .request(board.getRequest())
                .finishedAt(board.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                .isMatch(board.isMatch())
                .pickerBank(board.getMatch().getUser().getBank())
                .pickerAccount(board.getMatch().getUser().getAccount())
                .arrivalTime(board.getMatch().getMatchTime().plusMinutes(board.getMatch().getArrivalTime()).toEpochSecond(ZoneOffset.UTC))
                .pickerPhoneNumber(board.getMatch().getUser().getPhoneNumber())
                .build();

    }

    public LoginUserRp login(Authentication authentication) {
        CustomOauth2User customOauth2User = (CustomOauth2User) authentication.getPrincipal();
        if (customOauth2User == null) {
            throw new Exception400("인증에 실패하였습니다.");
        }
        User user = userRepository.findBySocialId(customOauth2User.getName()).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        String userPK = user.getUserId().toString();
        return LoginUserRp.builder()
                .accessToken(jwtService.createAccessToken(userPK))
                .nickname(user.getNickname())
                .userAuth(user.getUserRole().getValue())
                .build();
    }
}