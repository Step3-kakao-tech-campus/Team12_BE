package pickup_shuttle.pickup._core.utils;

import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class CustomPage<T> {
    List<T> content;
    CustomPageable pageable;

    public CustomPage(Slice<T> slice) {
        this.content = slice.getContent();
        this.pageable = new CustomPageable(slice);
    }

    @Getter
    private class CustomPageable {
        private int numberOfElements;
        private boolean empty;
        private boolean last;

        public CustomPageable(Slice<T> slice){
            this.numberOfElements = slice.getNumberOfElements();
            this.empty = slice.isEmpty();
            this.last = slice.isLast();
        }
    }
}


