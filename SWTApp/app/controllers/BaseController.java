package controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import play.Logger;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.s_search;
import common.exception.*;

/**
 * 共通 Controller
 *
 * @author 
 * @since 
 */
public class BaseController extends Controller {

    /**
     * 
     *
     * @param call
     * @param flashKey
     * @param flashMessage
     * @return
     */
    public static Result fail(Call call, String flashKey, String flashMessage) {
        ctx().flash().put(flashKey, flashMessage);
        return redirect(call);
    }
    
    /**
     * システムエラー発生時　処理 
     *
     * @param e
     * @return
     */
    public static Result appError(AppException e) {
    	// ログにエラー内容を出力する
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
    	pw.flush();	
    	Logger.error("エラー発生：" + e.getAppMessage() + " 理由：" + e.getAppError().getReason() + "\n内容：" + sw.toString());
        ctx().flash().put("error", "処理中にエラーが発生しました。");
        return ok(s_search.render("システムエラー画面"));
    }
    
    /**
     * システムエラー発生時　処理 
     *
     * @param e
     * @return
     */
    public static Result UnknownError(Exception e) {
    	// ログにエラー内容を出力する
    	Logger.error("予期していないエラー発生：", e);
        ctx().flash().put("error", "処理中にエラーが発生しました。");
        return ok(s_search.render("システムエラー画面"));
    }
}
