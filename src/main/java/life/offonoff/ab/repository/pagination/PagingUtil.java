package life.offonoff.ab.repository.pagination;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class PagingUtil {

    public static <T> Slice<T> toSlice(List<T> result, Pageable pageable) {
        boolean hasNext = false;
        if (result.size() > pageable.getPageSize()) {
            hasNext = true;
            result.remove(result.size() - 1);
        }
        return new SliceImpl<>(result, pageable, hasNext);
    }
}
