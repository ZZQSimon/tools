/**
	* Title: HostVerifierUtil.java <br>
	* Description:[] <br>
	* Copyright (c)  2018<br>
	* Company: <br>
	* @Date 2019.10.17 <br>
	* 
	* @author Simon Zhang
	* @version V1.0
	*/
package cn.com.easyerp.framework.httpclient;

import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * @ClassName: HostVerifierUtil <br>
 * @Description: [] <br>
 * @date 2019.10.17 <br>
 * 
 * @author Simon Zhang
 */

public class HostVerifierUtil {
    /**
     * 允许所有HOST<br>
     * 相当于 SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
     * 
     * @return
     */
    public static HostnameVerifier allowAllHost() {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        return hostnameVerifier;
    }

    /**
     * 允许列表内HOST<br>
     * 
     * @return
     */
    public static HostnameVerifier allowHost(List<String> hostnameList) {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if (hostnameList.contains(hostname)) {
                    return true;
                } else {
                    HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                    return hv.verify(hostname, session);
                }
            }
        };
        return hostnameVerifier;
    }

    /**
     * 不允许列表内HOST<br>
     * 
     * @return
     */
    public static HostnameVerifier notAllowHost(List<String> hostnameList) {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if (!hostnameList.contains(hostname)) {
                    return true;
                } else {
                    HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                    return hv.verify(hostname, session);
                }
            }
        };
        return hostnameVerifier;
    }
}
