package ee.opngo.pms.exception;

import lombok.Data;

@Data
public class PmsException extends Exception {

    private PmsExceptionCode code;

    public PmsException(String msg) {
        super(msg);
        this.code = PmsExceptionCode.DEFAULT;
    }

    public PmsException(String msg, PmsExceptionCode code) {
        super(msg);
        this.code = code;
    }

    public enum PmsExceptionCode {
        DEFAULT,
        VEHICLE_NOT_FOUND,
        ASSET_NOT_FOUND,
        ALREADY_PARKED,
        BAD_REQUEST,
        INCORRECT_SESSION_STATUS;
    }
}
