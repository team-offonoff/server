package life.offonoff.ab.exception;

public class DuplicateNicknameException extends DuplicateException {

    private static final String MESSAGE = "이미 사용중인 닉네임이에요.";
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
