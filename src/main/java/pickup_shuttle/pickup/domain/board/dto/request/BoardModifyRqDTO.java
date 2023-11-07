package pickup_shuttle.pickup.domain.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import org.springframework.util.ReflectionUtils;
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
        @NotSpace(message = "가게가 공백입니다")
        String store,
        List<@NotBlank(message = "음료가 공백입니다") String> beverage,
        @NotSpace
        String destination,
        @PositiveOrZero(message = "픽업팁이 음수입니다")
        Integer tip,
        String request,
        @NotSpace(message = "마감기간이 공백입니다")
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