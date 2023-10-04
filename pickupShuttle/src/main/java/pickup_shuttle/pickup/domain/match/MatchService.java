package pickup_shuttle.pickup.domain.match;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pickup_shuttle.pickup.domain.user.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {
    private final MatchRepository matchRepository;

    public Match createMatch(int arrivalTime, User user) {
        Match match = Match.builder()
                .arrivalTime(arrivalTime)
                .user(user)
                .build();
        return matchRepository.save(match);
    }
}
