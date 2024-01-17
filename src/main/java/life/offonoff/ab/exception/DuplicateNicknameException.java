package life.offonoff.ab.exception;

public class DuplicateNicknameException extends DuplicateException {

    private static final String MESSAGE = "중복된 닉네임입니다.";
    private final String nickname;

    public DuplicateNicknameException(String nickname) {
        super(MESSAGE);
        this.nickname = nickname;
    }

    @Override
    public String getHint() {
        return "닉네임 [" + nickname + "]은 사용 중입니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.DUPLICATE_NICKNAME;
    }
}
