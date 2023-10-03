package life.offonoff.ab.exception;

public class CategoryNotFoundException extends NotFoundException {
    private static final String MESSAGE = "존재하지 않는 토픽 카테고리 입니다.";
    private static final AbCode AB_CODE = AbCode.CATEGORY_NOT_FOUND;
    private final Long categoryId;

    public CategoryNotFoundException(final Long categoryId) {
        super(MESSAGE);
        this.categoryId = categoryId;
    }

    @Override
    public String getHint() {
        return "카테고리 id ["+categoryId+"]는 존재하지 않습니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
