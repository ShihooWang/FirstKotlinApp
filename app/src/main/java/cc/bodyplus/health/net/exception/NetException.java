package cc.bodyplus.health.net.exception;

/**
 * Created by Fussen on 2017/8/22.
 */

public class NetException extends RuntimeException {
    private int errorCode;

    private String message;

    public NetException(int code, String msg) {
        super(msg);
        this.errorCode = code;
        this.message = msg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
