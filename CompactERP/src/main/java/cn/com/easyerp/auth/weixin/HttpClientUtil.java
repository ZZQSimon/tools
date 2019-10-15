package cn.com.easyerp.auth.weixin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
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
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
    private static PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
    /*
     * static { (HttpClientUtil.connMgr = new
     * PoolingHttpClientConnectionManager()).setMaxTotal(100);
     * HttpClientUtil.connMgr.setDefaultMaxPerRoute(HttpClientUtil.connMgr.
     * getMaxTotal()); final RequestConfig.Builder configBuilder =
     * RequestConfig.custom(); configBuilder.setConnectTimeout(7000);
     * configBuilder.setSocketTimeout(7000);
     * configBuilder.setConnectionRequestTimeout(7000);
     * configBuilder.setStaleConnectionCheckEnabled(true);
     * HttpClientUtil.requestConfig = configBuilder.build(); }
     */
    static {
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(7000);
        configBuilder.setSocketTimeout(7000);
        configBuilder.setConnectionRequestTimeout(7000);
        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 7000;

    public static String doGet(final String url) {
        return doGet(url, new HashMap<String, Object>());
    }

    public static String doGet(final String url, final Map<String, Object> params) {
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
        String result = null;
        final HttpClient httpclient = (HttpClient) new DefaultHttpClient();
        try {
            final HttpGet httpPost = new HttpGet(apiUrl);
            final HttpResponse response = httpclient.execute((HttpUriRequest) httpPost);
            final int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("\u6267\u884c\u72b6\u6001\u7801 : " + statusCode);
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                final InputStream instream = entity.getContent();
                result = IOUtils.toString(instream, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String doPost(final String apiUrl) {
        return doPost(apiUrl, new HashMap<String, Object>());
    }

    public static String doPost(final String apiUrl, final Map<String, Object> params) {
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        final HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(HttpClientUtil.requestConfig);
            final List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (final Map.Entry<String, Object> entry : params.entrySet()) {
                final NameValuePair pair = (NameValuePair) new BasicNameValuePair((String) entry.getKey(),
                        (entry.getValue() == null) ? null : entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity((HttpEntity) new UrlEncodedFormEntity((Iterable) pairList, Charset.forName("UTF-8")));
            response = httpClient.execute((HttpUriRequest) httpPost);
            System.out.println(response.toString());
            final HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    public static String doPost(final String apiUrl, final Object json) {
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        final HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(HttpClientUtil.requestConfig);
            final StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity((HttpEntity) stringEntity);
            response = httpClient.execute((HttpUriRequest) httpPost);
            final HttpEntity entity = response.getEntity();
            System.out.println(response.getStatusLine().getStatusCode());
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    public static String doPostSSL(final String apiUrl, final Map<String, Object> params) {
        final CloseableHttpClient httpClient = createSSLClientDefault();
        final HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;
        try {
            httpPost.setConfig(HttpClientUtil.requestConfig);
            final List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (final Map.Entry<String, Object> entry : params.entrySet()) {
                final NameValuePair pair = (NameValuePair) new BasicNameValuePair((String) entry.getKey(),
                        entry.getValue().toString());
                pairList.add(pair);
            }
            SSLSocketFactory.getSocketFactory()
                    .setHostnameVerifier((X509HostnameVerifier) new AllowAllHostnameVerifier());
            httpPost.setEntity((HttpEntity) new UrlEncodedFormEntity((Iterable) pairList, Charset.forName("utf-8")));
            response = httpClient.execute((HttpUriRequest) httpPost);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    private static CloseableHttpClient createSSLClientDefault() {
        try {
            final SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial((KeyStore) null, (TrustStrategy) new TrustStrategy() {
                        public boolean isTrusted(final X509Certificate[] xcs, final String string) {
                            return true;
                        }
                    }).build();
            final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return HttpClients.custom().setSSLSocketFactory((LayeredConnectionSocketFactory) sslsf).build();
        } catch (KeyStoreException ex) {
        } catch (NoSuchAlgorithmException ex2) {
        } catch (KeyManagementException ex3) {
        }
        return HttpClients.createDefault();
    }

    public static String doPostSSL(final String apiUrl, final Object json) {
        final CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory((LayeredConnectionSocketFactory) createSSLConnSocketFactory())
                .setConnectionManager((HttpClientConnectionManager) HttpClientUtil.connMgr)
                .setDefaultRequestConfig(HttpClientUtil.requestConfig).build();
        final HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;
        try {
            httpPost.setConfig(HttpClientUtil.requestConfig);
            final StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity((HttpEntity) stringEntity);
            response = httpClient.execute((HttpUriRequest) httpPost);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            final SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial((KeyStore) null, (TrustStrategy) new TrustStrategy() {
                        public boolean isTrusted(final X509Certificate[] chain, final String authType)
                                throws CertificateException {
                            return true;
                        }
                    }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, (X509HostnameVerifier) new X509HostnameVerifier() {
                public boolean verify(final String arg0, final SSLSession arg1) {
                    return true;
                }

                public void verify(final String host, final SSLSocket ssl) throws IOException {
                }

                public void verify(final String host, final X509Certificate cert) throws SSLException {
                }

                public void verify(final String host, final String[] cns, final String[] subjectAlts)
                        throws SSLException {
                }
            });
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }

    public static void main(final String[] args) throws Exception {
    }
}
