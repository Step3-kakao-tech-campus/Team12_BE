package pickup_shuttle.pickup.domain.board.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.util.ReflectionUtils;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.config.NotSpace;
import pickup_shuttle.pickup.config.ValidValue;
import pickup_shuttle.pickup.domain.beverage.Beverage;
import pickup_shuttle.pickup.domain.beverage.dto.request.BeverageRq;
import pickup_shuttle.pickup.domain.store.Store;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public record UpdateBoardRq(
        @NotSpace(message = "가게" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = ValidValue.STRING_MAX, message = "가게" + ErrorMessage.BADREQUEST_SIZE)
        String shopName,
        List<@Valid BeverageRq> beverages,
        @NotSpace(message = "위치" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = ValidValue.STRING_MAX, message = "위치" + ErrorMessage.BADREQUEST_SIZE)
        String destination,
        @Min(value = ValidValue.INTEGER_MIN, message = "픽업팁" + ErrorMessage.BADREQUEST_MIN)
        Integer tip,
        @Size(max = ValidValue.STRING_MAX, message = "요청사항" + ErrorMessage.BADREQUEST_SIZE)
        String request,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$", message = "마감기간은 yyyy-MM-dd HH:mm 형식이어야 합니다")
        String finishedAt
) {
    public Map<String, Object> patchValues(Store store){
        Map<String, Object> map = new HashMap<>();
        ReflectionUtils.doWithFields(UpdateBoardRq.class, field -> {
            Object value = field.get(this);
            if (value != null) {
                switch (field.getName()){
                    case "shopName": map.put("store", store); break;
                    case "beverages" :  map.put("beverages", beverages((List<BeverageRq>) value)); break;
                    case "finishedAt" : map.put("finishedAt", localDateTime((String) value)); break;
                    default: map.put(field.getName(), value);
                }
            }
        });

        return map;
    }

    private List<Beverage> beverages(List<BeverageRq> beverageRqs) {
        return beverageRqs.stream().map(
                        b -> Beverage.builder()
                                .name(b.name())
                                .build())
                .toList();
    }

    private LocalDateTime localDateTime(String stringTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(stringTime, formatter);
        return localDateTime;
    }
}