package cc.bodyplus.health.net.cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by shihoo_wang on 2017/3/12.
 * 自动管理cookie
 */

public class MyCookieJar implements CookieJar {

    private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.put(HttpUrl.parse(url.host()), cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url.host());
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }
}
