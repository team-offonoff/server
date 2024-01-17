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

    public PageResponse(int total, Slice<T> result) {
        this.pageInfo = new TotalHavingPageInfo(
                total,
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

    public static <T> PageResponse<T> of(int total, Slice<T> result) {
        return new PageResponse<>(total, result);
    }

    @Getter @AllArgsConstructor
    static class PageInfo {

        private int page;
        private int size;
        private boolean isEmpty;
        private boolean isLast;
    }

    @Getter
    static class TotalHavingPageInfo extends PageInfo {

        private int total;

        public TotalHavingPageInfo(int total, int page, int size, boolean isEmpty, boolean isLast) {
            super(page, size, isEmpty, isLast);
            this.total = total;
        }
    }
}
