package pickup_shuttle.pickup.domain.user;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup._core.errors.exception.Exception500;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
import pickup_shuttle.pickup.domain.user.dto.repository.UserRepository;
import pickup_shuttle.pickup.domain.user.dto.repository.UserRepositoryCustom;
import pickup_shuttle.pickup.domain.user.dto.request.UserModifyRqDTO;
import pickup_shuttle.pickup.domain.user.dto.request.SignUpRqDTO;
import pickup_shuttle.pickup.domain.user.dto.response.UserAuthListRpDTO;
import pickup_shuttle.pickup.domain.user.dto.response.UserGetImageUrlRpDTO;
import pickup_shuttle.pickup.domain.user.dto.response.UserMyPageRpDTO;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AmazonS3 amazonS3;
    private final UserRepositoryCustom userRepositoryCustom;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.dir}")
    private String dir;
    @Transactional
    public void signup(SignUpRqDTO signUpRqDTO, CustomOauth2User customOauth2User){
        String bankName = signUpRqDTO.bankName();
        String accountNum = signUpRqDTO.accountNum();
        Optional<User> user = userRepository.findBySocialId(customOauth2User.getName());
        user.get().setRole(UserRole.USER);
        customOauth2User.setBankName(bankName);
        customOauth2User.setAccountNum(accountNum);
        user.get().setBank(bankName);
        user.get().setAccount(accountNum);
        return;
    }
    public String userAuthStatus(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        String userRole = user.getUserRole().getValue();
        String userUrl = user.getUrl();
        switch (userRole){
            case "ROLE_USER": return userUrl.equals("") ? "미인증":"인증 진행 중";
            case "ROLE_STUDENT": return "인증";
            default : return "미인증";
        }
    }

    @Transactional
    public boolean modifyUser(UserModifyRqDTO userModifyRqDTO, Long userId){
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            return false;
        }
        String userBankName = user.get().getBank();
        String userAccountNum = user.get().getAccount();
        if(userModifyRqDTO.account() != userAccountNum){
            user.get().setAccount(userModifyRqDTO.account());
        }
        if(userModifyRqDTO.bank() != userBankName){
            user.get().setBank(userModifyRqDTO.bank());
        }
        return true;
    }

    public long userPK(CustomOauth2User customOauth2User){
        Optional<User> user = userRepository.findBySocialId(customOauth2User.getName());
        long userPK = user.get().getUserId();
        return userPK;
    }

    @Transactional
    public void uploadImage(MultipartFile multipartFile, Long userId){
        // 메타 데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        if(!isImage(multipartFile.getContentType())){
            throw new Exception400("이미지 파일이 아닙니다");
        }
        metadata.setContentType(multipartFile.getContentType());
        // 파일 읽기
        InputStream inputStream;
        try{
            inputStream = multipartFile.getInputStream();
        }catch(Exception e){
            throw new Exception400("파일을 읽을 수 없습니다");
        }
        // 유저 검증
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        if(user.getUserRole().getValue().equals("ROLE_STUDENT"))
            throw new Exception400("이미 인증된 유저입니다");
        // 업로드
        try{
            String fileName = dir + userId + ".jpg"; // 파일명 : {userId}.jpg
            amazonS3.putObject(bucket, fileName, inputStream, metadata); // aws
            user.updateUrl(amazonS3.getUrl(bucket, fileName).toString()); // mysql
        }catch (Exception e){
            throw new Exception500("AWS 이미지 업로드를 실패했습니다");
        }
    }

    public UserGetImageUrlRpDTO getImageUrl(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        if(user.getUrl().equals("")){
            throw new Exception400("등록된 이미지가 존재하지 않습니다");
        }
        return UserGetImageUrlRpDTO.builder().url(getPresignedUrl(userId)).build(); // PreSigned URL 응답
    }

    // PreSigned URL 발급
    private String getPresignedUrl(Long userId) {

        String fileName = dir + userId + ".jpg";
        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucket, fileName);
        try{
            String presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
            return presignedUrl;
        }catch (Exception e){
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

    private boolean isImage(String fileType){
        String[] imageTypes = {"image/jpeg", "image/png", "image/gif"};
        for (String type : imageTypes) {
            if (type.equals(fileType)) {
                return true;
            }
        }
        return false;
    }
    public UserMyPageRpDTO myPage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        return UserMyPageRpDTO.builder()
                .role(user.getUserRole().getValue())
                .name(user.getName())
                .build();
    }

    public Slice<UserAuthListRpDTO> getAuthList(Long lastUserId, int size){
        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<User> userSlice = userRepositoryCustom.searchAuthList(lastUserId, pageRequest);

        List<UserAuthListRpDTO> userAuthListRpDTOList = userSlice.getContent().stream()
                .map(u -> UserAuthListRpDTO.builder()
                            .userId(u.getUserId())
                            .name(u.getName())
                            .build())
                .toList();
        return new SliceImpl<>(userAuthListRpDTOList, pageRequest, userSlice.hasNext());
    }

}

