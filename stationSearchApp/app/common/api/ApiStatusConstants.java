package common.api;

/**
 * ApiStatusConstants
 * 
 * 
 */
public enum ApiStatusConstants {
	OK           (20, "ok."),
    NO_RESULT    (50, "no result.");

    private Integer code;
    private String message;
    private ApiStatusConstants(Integer code){
        this.code = code;
    }
    private ApiStatusConstants(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
