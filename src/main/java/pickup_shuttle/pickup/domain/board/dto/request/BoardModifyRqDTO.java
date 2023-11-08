package pickup_shuttle.pickup.domain.board.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.util.ReflectionUtils;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.config.NotSpace;
import pickup_shuttle.pickup.domain.beverage.Beverage;
import pickup_shuttle.pickup.domain.store.Store;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public record BoardModifyRqDTO(
        @NotSpace(message = "가게" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = 60, message = "가게" + ErrorMessage.BADREQUEST_SIZE)
        String store,
        List<@NotBlank(message = "음료" + ErrorMessage.BADREQUEST_BLANK) String> beverage,
        @NotSpace(message = "위치" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = 60, message = "위치" + ErrorMessage.BADREQUEST_SIZE)
        String destination,
        @Min(value = 1, message = "픽업팁" + ErrorMessage.BADREQUEST_MIN)
        @Max(value = Integer.MAX_VALUE, message = "픽업팁" + ErrorMessage.BADREQUEST_MAX)
        Integer tip,
        @Size(max = 60, message = "요청사항" + ErrorMessage.BADREQUEST_SIZE)
        String request,
        @NotSpace(message = "마감기간" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = 60, message = "요청사항" + ErrorMessage.BADREQUEST_SIZE)
        String finishedAt
) {
    public Map<String, Object> patchValues(Store store){
        Map<String, Object> map = new HashMap<>();
        ReflectionUtils.doWithFields(BoardModifyRqDTO.class, field -> {
            Object value = field.get(this);
            if (value != null) {
                switch (field.getName()){
                    case "store": map.put("store", store); break;
                    case "beverage" :  map.put("beverages", beverageList((List<String>) value)); break;
                    case "finishedAt" : map.put("finishedAt", localDateTime((String) value)); break;
                    default: map.put(field.getName(), value);
                }
            }
        });

        return map;
    }


    private List<Beverage> beverageList(List<String> beverage) {
        return beverage.stream().map(
                        b -> Beverage.builder()
                                .name(b)
                                .build())
                .toList();
    }

    private LocalDateTime localDateTime(String stringTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(stringTime, formatter);
        return localDateTime;
    }
}