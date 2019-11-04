package cn.com.easyerp.framework.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

import cn.com.easyerp.framework.enums.CS;
import cn.com.easyerp.framework.enums.HTTPStatusCode;
import cn.com.easyerp.framework.util.LogUtil;

public class HttpClientUtil {
    private static PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
    private static RequestConfig requestConfig;

    /** 连接池最大连接数 */
    private static final int MAX_TOTAL = 100;
    /** 访问超时设置 */
    private static final int MAX_TIMEOUT = 7000;
    /** 连接池中链接失效时限 10 分钟 */
    private static final int VALIDATE_AFTER_INACTIVITY_MS = 10 * 60 * 1000;

    static {
        connMgr.setMaxTotal(MAX_TOTAL);
        connMgr.setDefaultMaxPerRoute(MAX_TOTAL);
        connMgr.setValidateAfterInactivity(VALIDATE_AFTER_INACTIVITY_MS);

        Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);

        requestConfig = configBuilder.build();
    }

    public static JSONObject doGetJson(final String apiUrl) {
        final HttpGet httpGet = createHttpGetDefault(apiUrl);

        try (final CloseableHttpClient client = HttpClientBuilder.create().build();
                final CloseableHttpResponse response = client.execute((HttpUriRequest) httpGet);) {
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HTTPStatusCode.C200.code()) {
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (null == entity) {
                return null;
            }
            final String result = EntityUtils.toString(entity, CS.UTF8.set());
            return JSONObject.parseObject(result);
        } catch (ClientProtocolException e) {
            LogUtil.error(e, "doGetJson error [{0}]", e.getMessage());
            return null;
        } catch (IOException e) {
            LogUtil.error(e, "doGetJson error [{0}]", e.getMessage());
            return null;
        } finally {
            httpGet.releaseConnection();
        }
    }

    public static String doGet(final String url) {
        return doGet(url, new HashMap<String, Object>());
    }

    public static String doGet(final String url, final Map<String, Object> params) {
        final HttpGet httpGet = createHttpGetDefault(url, params);

        try (final CloseableHttpClient httpclient = HttpClientBuilder.create().build();
                final CloseableHttpResponse response = httpclient.execute((HttpUriRequest) httpGet);) {
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HTTPStatusCode.C200.code()) {
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (null == entity) {
                return null;
            }
            final InputStream instream = entity.getContent();
            return IOUtils.toString(instream, CS.UTF8.set());
        } catch (IOException e) {
            LogUtil.error(e, "doGet error [{0}]", e.getMessage());
            return null;
        }
    }

    public static String doGetSSL(String url, Map<String, Object> params) {
        final HttpGet httpGet = createHttpGetDefault(url, params);
        return doGetSSL(httpGet);
    }

    private static String doGetSSL(HttpGet httpGet) {
        try (CloseableHttpClient httpClient = createSSLClientDefault();
                CloseableHttpResponse response = httpClient.execute((HttpUriRequest) httpGet);) {
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HTTPStatusCode.C200.code()) {
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (null == entity) {
                return null;
            }
            return EntityUtils.toString(entity, CS.UTF8.set());
        } catch (KeyManagementException e) {
            LogUtil.error(e, "createSSLCloseableHttpClient error [KeyManagementException]");
            return null;
        } catch (NoSuchAlgorithmException e) {
            LogUtil.error(e, "createSSLCloseableHttpClient error [NoSuchAlgorithmException]");
            return null;
        } catch (KeyStoreException e) {
            LogUtil.error(e, "createSSLCloseableHttpClient error [KeyStoreException]");
            return null;
        } catch (Exception e) {
            LogUtil.error(e, "CloseableHttpResponse error ", e.getMessage());
            return null;
        }
    }

    private static HttpGet createHttpGetDefault(String url, final Map<String, Object> params) {
        String apiUrl = url;
        final StringBuffer param = new StringBuffer();
        int i = 0;
        for (final String key : params.keySet()) {
            if (i == 0) {
                param.append("?");
            } else {
                param.append("&");
            }
            param.append(key).append("=").append(params.get(key));
            ++i;
        }
        apiUrl += (Object) param;
        return createHttpGetDefault(apiUrl);
    }

    private static HttpGet createHttpGetDefault(final String apiUrl) {
        final HttpGet httpGet = new HttpGet(apiUrl);
        return httpGet;
    }

    public static String doPost(final String apiUrl) {
        return doPost(apiUrl, new HashMap<String, Object>());
    }

    public static String doPost(final String apiUrl, final Map<String, Object> params) {
        final HttpPost httpPost = createHttpPostDefault(apiUrl, params);
        return doPost(httpPost);
    }

    public static String doPost(final String apiUrl, final Object json) {
        final HttpPost httpPost = createHttpPostDefault(apiUrl, json);
        return doPost(httpPost);
    }

    private static String doPost(HttpPost httpPost) {
        try (final CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute((HttpUriRequest) httpPost);) {
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HTTPStatusCode.C200.code()) {
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (null == entity) {
                return null;
            }
            return EntityUtils.toString(entity, CS.UTF8.set());
        } catch (IOException e) {
            LogUtil.error(e, "doPost error [{0}]", e.getMessage());
            return null;
        }
    }

    public static String doPostSSL(final String apiUrl, final Map<String, Object> params) {
        final HttpPost httpPost = createHttpPostDefault(apiUrl, params);
        return doPostSSL(httpPost);
    }

    public static String doPostSSL(final String apiUrl, final Object json) {
        final HttpPost httpPost = createHttpPostDefault(apiUrl, json);
        return doPostSSL(httpPost);
    }

    private static String doPostSSL(HttpPost httpPost) {
        try (CloseableHttpClient httpClient = createSSLClientDefault();
                CloseableHttpResponse response = httpClient.execute((HttpUriRequest) httpPost);) {
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HTTPStatusCode.C200.code()) {
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (null == entity) {
                return null;
            }
            return EntityUtils.toString(entity, CS.UTF8.set());
        } catch (KeyManagementException e) {
            LogUtil.error(e, "createSSLCloseableHttpClient error [KeyManagementException]");
            return null;
        } catch (NoSuchAlgorithmException e) {
            LogUtil.error(e, "createSSLCloseableHttpClient error [NoSuchAlgorithmException]");
            return null;
        } catch (KeyStoreException e) {
            LogUtil.error(e, "createSSLCloseableHttpClient error [KeyStoreException]");
            return null;
        } catch (Exception e) {
            LogUtil.error(e, "CloseableHttpResponse error ", e.getMessage());
            return null;
        }
    }

    private static HttpPost createHttpPostDefault(final String apiUrl, final Map<String, Object> params) {
        final HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setConfig(HttpClientUtil.requestConfig);
        final List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
        for (final Map.Entry<String, Object> entry : params.entrySet()) {
            final NameValuePair pair = (NameValuePair) new BasicNameValuePair((String) entry.getKey(),
                    entry.getValue().toString());
            pairList.add(pair);
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity((Iterable<? extends NameValuePair>) pairList,
                CS.UTF8.set());
        httpPost.setEntity(entity);

        return httpPost;
    }

    private static HttpPost createHttpPostDefault(final String apiUrl, final Object json) {
        final HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setConfig(HttpClientUtil.requestConfig);
        final StringEntity stringEntity = new StringEntity(json.toString(), CS.UTF8.set());
        stringEntity.setContentEncoding(CS.UTF8.getName());
        stringEntity.setContentType("application/json");
        httpPost.setEntity((HttpEntity) stringEntity);
        return httpPost;
    }

    private static CloseableHttpClient createSSLClientDefault()
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        CloseableHttpClient httpClient = createSSLHttpClientBuilder().build();
        return httpClient;
    }

    private static HttpClientBuilder createSSLHttpClientBuilder()
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        HttpClientBuilder builder = HttpClients.custom();
        builder.setSSLSocketFactory((LayeredConnectionSocketFactory) createSSLConnSocketFactory());
        builder.setConnectionManager((HttpClientConnectionManager) HttpClientUtil.connMgr);
        builder.setDefaultRequestConfig(HttpClientUtil.requestConfig);
        return builder;
    }

    private static SSLConnectionSocketFactory createSSLConnSocketFactory()
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        final SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial((KeyStore) null, (TrustStrategy) new TrustStrategy() {
                    public boolean isTrusted(final X509Certificate[] chain, final String authType)
                            throws CertificateException {
                        return true;
                    }
                }).build();
        return new SSLConnectionSocketFactory(sslContext, HostVerifierUtil.allowAllHost());
    }

}
