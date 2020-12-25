package com.rj.core.huawei.pay;

import com.huawei.hms.support.log.common.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * author: jansir
 * e-mail: 369394014@qq.com
 * date: 2020/6/8.
 */
public class HwAtDemo {
    // TODO: The values of (clientId, clientSecret, tokenUrl) should be replaced with the actual one.
    private static final String clientSecret = "3de76a1fee6b612509bec43df6238358"; //your client secret
    // !!! the client id is your APP ID
    private static final String clientId = "100708671"; //your app id
    private static final String tokenUrl = "https://oauth-login.cloud.huawei.com/oauth2/v2/token"; //token url to get the authorization
    /**
     * The accessToken.
     */
    private static String accessToken;
    /**
     * Gets App Level AccessToken.
     *
     * @return the App Level AccessToken
     * @throws Exception the exception
     */
    public static String getAppAT() throws Exception {
        //fetch accessToken
        String grant_type = "client_credentials";
        String msgBody = MessageFormat.format("grant_type={0}&client_secret={1}&client_id={2}", grant_type, URLEncoder.encode(clientSecret, "UTF-8"), clientId);
        String response = httpPost(tokenUrl, "application/x-www-form-urlencoded; charset=UTF-8", msgBody, 5000, 5000, null);
        System.out.println(response);
        JSONObject obj = new JSONObject(response);
        accessToken = obj.getString("access_token");
        // TODO: display the accessToken in console, you can remove it
        System.out.println(accessToken);
        return accessToken;
    }
    /**
     * Build Authorization in Header
     *
     * @param appAt appAt
     * @return headers
     */
    public static Map<String, String> buildAuthorization(String appAt) {
        String oriString = MessageFormat.format("APPAT:{0}", appAt);
        String authorization =
                MessageFormat.format("Basic {0}", Base64.encode(oriString.getBytes(StandardCharsets.UTF_8)));
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authorization);
        headers.put("Content-Type", "application/json; charset=UTF-8");
        return headers;
    }
    /**
     * Http post function.
     *
     * @param httpUrl the http url
     * @param data the data
     * @param connectTimeout the connect timeout
     * @param readTimeout the read timeout
     * @param headers the headers
     * @return the response as string
     * @throws IOException the io exception
     */
    public static String httpPost(String httpUrl, String contentType, String data, int connectTimeout, int readTimeout, Map<String, String> headers) throws IOException {
        OutputStream output = null;
        InputStream in = null;
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            URL url = new URL(httpUrl);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", contentType);
            if (headers != null) {
                for (String key : headers.keySet()) {
                    urlConnection.setRequestProperty(key, headers.get(key));
                }
            }
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.connect();
            // POST data
            output = urlConnection.getOutputStream();
            output.write(data.getBytes("UTF-8"));
            output.flush();
            // read response
            if (urlConnection.getResponseCode() < 400) {
                in = urlConnection.getInputStream();
            } else {
                in = urlConnection.getErrorStream();
            }
            inputStreamReader = new InputStreamReader(in, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder strBuf = new StringBuilder();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                strBuf.append(str);
            }
            return strBuf.toString();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (in != null) {
                in.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
