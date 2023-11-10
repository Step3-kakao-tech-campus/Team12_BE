package pickup_shuttle.pickup.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
    /*
    ADMIN - 관리자
    USER - 일반 회원(학생증 인증 전 등급)
    STUDENT - 학생증 인증 후 등급
    GUEST - 회원가입을 했지만 은행, 계좌번호 아직 입력 안한 회원(카카오로 회원가입 처리 후 바로 은행, 계좌번호 입력창으로 리다이렉트 시키기 위한 구분 용도의 역할)
     */
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    STUDENT("ROLE_STUDENT"),
    GUEST("ROLE_GUEST");

    UserRole(String value){
        this.value = value;
    }

    private String value;
}
