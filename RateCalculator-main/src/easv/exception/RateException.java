package easv.exception;


public class RateException extends Exception {
    private ErrorCode errorCode;

    public RateException(String message, Throwable cause, ErrorCode code) {
        super(message, cause);
        this.errorCode = code;
    }
    public RateException(String message){
        super(message);
    }
    public RateException(ErrorCode code){
        super();
        this.errorCode=code;
    }
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
