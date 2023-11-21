package life.offonoff.ab.exception;


public abstract class MemberNotFountException extends NotFoundException {
    private static final String MESSAGE = "존재하지 않는 회원 입니다.";

    public MemberNotFountException() {
        super(MESSAGE);
    }
}
