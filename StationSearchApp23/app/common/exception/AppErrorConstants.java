package common.exception;

public class AppErrorConstants {
	
    /**
     * AppError
     * util系:05XXX
     * 
     */
    public static enum AppError {
    	UTIL_GEOCODE_ERROR("05001", "位置情報処理中にエラーが発生"),
        UNKNOWN_ERROR("99999", "An unknown error occurred.");

        private String statusCode;
        private String reason;
        private AppError(String statusCode){
            this.statusCode = statusCode;
        }
        private AppError(String statusCode, String reason){
            this.statusCode = statusCode;
            this.reason = reason;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public String getReason() {
            return reason;
        }
    }
}
