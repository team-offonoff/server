package life.offonoff.ab.exception;

public class IllegalImageExtension extends IllegalArgumentException {
    private static final String MESSAGE = "허용되지 않는 파일 확장자입니다.";
    private static final AbCode AB_CODE = AbCode.ILLEGAL_FILE_EXTENSION;
    private final String fileName;
    public IllegalImageExtension(String fileName) {
        super(MESSAGE);
        this.fileName = fileName;
    }

    @Override
    public String getHint() {
        return "파일[name="+fileName+"]의 확장자는 허용되지 않습니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
