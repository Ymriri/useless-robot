package pers.wuyou.robot.utils;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pers.wuyou.robot.common.GlobalVariable;
import pers.wuyou.robot.entity.RequestEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class HttpImageUtil {

    private static final CookieStore STORE;
    private static final CloseableHttpClient CLOSEABLE_HTTP_CLIENT;

    static {
        // 缓存Cookie
        STORE = new BasicCookieStore();
        CLOSEABLE_HTTP_CLIENT = HttpClients.custom().setDefaultCookieStore(STORE).build();
        /*.setProxy(new HttpHost("172.24.58.136",5553))*/
    }

    /**
     * get请求
     *
     * @param url 请求的URL
     */
    public static RequestEntity get(String url) {
        Map<String, String> cookies = new HashMap<>(0);
        Map<String, String> params = new HashMap<>(0);
        return get(url, params, cookies);
    }

    /**
     * get请求
     *
     * @param url     请求的URL
     * @param params  请求的参数
     * @param cookies 请求携带的cookie
     */
    public static RequestEntity get(String url, Map<String, String> params, Map<String, String> cookies) {
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                params.forEach(uriBuilder::addParameter);
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            HttpUtil.setCookies(httpGet, cookies);
            return request(httpGet);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new RequestEntity();
    }

    /**
     * 网络请求具体实现
     */
    private static RequestEntity request(HttpRequestBase httpRequestBase) {
        try {
            return GlobalVariable.THREAD_POOL.submit(() -> {
                RequestEntity requestEntity = new RequestEntity();
                httpRequestBase.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36 SE 2.X MetaSr 1.0");
                try (CloseableHttpResponse closeableHttpResponse = CLOSEABLE_HTTP_CLIENT.execute(httpRequestBase)) {
                    byte[] data = EntityUtils.toByteArray(closeableHttpResponse.getEntity());
                    requestEntity.setOtherEntity(data);
                    requestEntity.setCookies(STORE.getCookies());
                    httpRequestBase.setHeader("Cookie", "");
                    STORE.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return requestEntity;
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RequestEntity();
    }
}
