package pickup_shuttle.pickup.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.security.JwtTokenUtil;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JwtTokenTest {

    String mySecretKey = "Test-Secret-Key-12";
    long expireTimeMs = 1000 * 60 * 60;
    User user = User.builder().uid("asdf1234").pwd("1111").phoneNumber("010-1234-1234").nickname("닉네임").name("이기준").build();

    @Test
    public void jwtTest(){
        String jwtToken = JwtTokenUtil.createToken(user.getUid(), mySecretKey, expireTimeMs);
        System.out.println("jwtToken: " + jwtToken);
    }

}
