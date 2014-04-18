import org.apache.commons.lang3.ArrayUtils;
import play.Application;
import play.Configuration;
import play.GlobalSettings;
import play.Logger;
import play.api.mvc.EssentialFilter;
import play.api.mvc.Handler;
import play.libs.F;
import play.libs.Json;
import play.mvc.*;
import play.filters.csrf.CSRFFilter;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Globalクラス
 *
 * @author 
 * @since 
 */
public class Global extends GlobalSettings {

    @Override
    public void onStart(Application application) {
        super.onStart(application);
    }

    @Override
    public void beforeStart(Application application) {
        super.beforeStart(application);
    }

    @Override
    public void onStop(Application application) {
        super.onStop(application);
    }

    @Override
    public F.Promise<SimpleResult> onError(Http.RequestHeader requestHeader, Throwable throwable) {
        return super.onError(requestHeader, throwable);
    }

    /**
     * リクエストハンドリング
     * @param request
     * @param method
     * @return
     */
    @Override
    public Action onRequest(Http.Request request, Method method) {
        Logger.info(Json.toJson(request.headers()).toString());
        return super.onRequest(request, method);
    }

    @Override
    public Handler onRouteRequest(Http.RequestHeader requestHeader) {
        return super.onRouteRequest(requestHeader);
    }

    @Override
    public F.Promise<SimpleResult> onBadRequest(Http.RequestHeader requestHeader, String s) {
        return super.onBadRequest(requestHeader, s);
    }

    @Override
    public Configuration onLoadConfig(Configuration configuration, File file, ClassLoader classLoader) {
        return super.onLoadConfig(configuration, file, classLoader);
    }

    /**
     * スラッシュなし
     * 参考
     * http://stackoverflow.com/questions/10004746/how-to-route-urls-in-play-2-0-so-theyre-indifferent-to-an-ending-slash
     * @param requestHeader
     * @return
     */
    @Override
    public F.Promise<SimpleResult> onHandlerNotFound(Http.RequestHeader requestHeader) {
        if (hasTrailingSlash(requestHeader.path())) {
            final String path = requestHeader.path();
            return F.Promise.promise(
                    new F.Function0<SimpleResult>() {
                        public SimpleResult apply() {
                            return Action.redirect(removeLastChar(path));
                        }
                    }
            );
        }
        return super.onHandlerNotFound(requestHeader);
    }

    private static String removeLastChar(String value) {
        return value.substring(0, value.length() - 1);
    }

    private static boolean hasTrailingSlash(String value) {
        return value != null && value.endsWith("/");
    }

    /**
     * CSRFフィルター
     * @param <T>
     * @return
     */
    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{
                CSRFFilter.class
        };
    }

}
