package common.exception;

import common.exception.AppErrorConstants.AppError;

public class AppException extends RuntimeException {
    /**
    *
    */
   private static final long serialVersionUID = 1323981179553145001L;

   private final AppError appError;
   private String message = ""; 

   public AppException(AppError appError) {
       this.appError = appError;
   }

   public AppException(AppError appError, String message) {
       super(message);
       this.appError = appError;
   }

   public AppException(AppError appError, Throwable cause) {
       super(cause);
       this.appError = appError;
   }
   
   public AppException(AppError appError, Throwable cause, String message) {
       
	   super(cause);
	   //System.out.println(cause.toString());
       this.message = message;
       this.appError = appError;
   }

   /**
    * @return the appError
    */
   public AppError getAppError() {
       return appError;
   }
   
   public String getAppMessage() {
       return message;
   }
}
