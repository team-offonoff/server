package life.offonoff.ab.web.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import java.util.List;

@Getter
public class PageResponse<T> {

    private PageInfo pageInfo;
    private List<T> data;

    public PageResponse(Slice<T> result) {
        this.pageInfo = new PageInfo(
                result.getNumber(),
                result.getSize(),
                !result.hasContent(),
                !result.hasNext()
        );
        this.data = result.getContent();
    }

    public static <T> PageResponse<T> of(Slice<T> result) {
        return new PageResponse<>(result);
    }

    @Getter @AllArgsConstructor
    static class PageInfo {

        private int page;
        private int size;
        private boolean isEmpty;
        private boolean isLast;
    }
}
