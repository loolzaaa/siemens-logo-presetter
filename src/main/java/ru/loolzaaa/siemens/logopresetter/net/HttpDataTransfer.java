package ru.loolzaaa.siemens.logopresetter.net;

import lombok.Getter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import ru.loolzaaa.siemens.logopresetter.config.DeviceType;
import ru.loolzaaa.siemens.logopresetter.util.LogoMath;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class HttpDataTransfer extends TcpDataTransfer {
    private static final int Auth_First = 0;
    private static final int Auth_Second = 1;
    private static final int Process = 2;
    private static final String User_Name = "LSC User";
    private static final int AJAX_SUCCESS = 700;
    private static final int AJAX_NO_PASSWORD_REQUIRED = 710;
    private static String password = null;
    private static int passwordLength = 0;
    private static long PW_TIME_STAMP = 0L;
    private static final int CACHE_TIME = 30000;
    private static Timer pwCacheTimer;
    private long private_key1_1 = 0L;
    private long private_key1_2 = 0L;
    private long private_key2_1 = 0L;
    private long private_key2_2 = 0L;
    private byte[] bytesFromPrivateKey1 = new byte[8];
    private byte[] bytesFromPrivateKey2 = new byte[8];
    private long server_key = 0L;
    private String login_reference = "";
    private String user_reference = "";
    private final boolean isHttps;
    private final String ipAddress;
    private final int portNr;
    private boolean isTransFinished = true;
    private byte[] responseContent;
    private static CloseableHttpClient httpClient;
    private static final HashMap<String, UserSession> referenceCacheMap = new HashMap<>();
    private static HttpClientBuilder clientBuilder;

    public static DataTransfer openConnection(String ipAddress, int portNr) throws IOException {
        DataTransfer dt;
        try {
            dt = new HttpDataTransfer(true, ipAddress, portNr);
        } catch (IOException e) {
            dt = new HttpDataTransfer(false, ipAddress, portNr);
        }
        return dt;
    }

    private HttpDataTransfer(boolean isHttps, String ipAddress, int port) throws IOException {
        this.isHttps = isHttps;
        this.ipAddress = ipAddress;
        this.portNr = this.getDefaultPort(isHttps, port);
        this.verifyConnection();
    }

    private int getDefaultPort(boolean isHttps, int port) {
        if (isHttps) {
            return port == -1 ? 8443 : port;
        } else {
            return port == -1 ? 8080 : port;
        }
    }

    private boolean getAuthorization4Exception() throws IOException {
        return getAuthorization(false);
    }

    private void clearLoginInfo() {
        private_key1_1 = (long)(Math.random() * 4.294967296E9);
        private_key1_2 = (long)(Math.random() * 4.294967296E9);

        int i;
        for(i = 0; i < 4; ++i) {
            bytesFromPrivateKey1[i] = (byte)((int)((private_key1_1 & (long)(-16777216 >> i * 8)) >> 24 - i * 8));
        }

        for(i = 0; i < 4; ++i) {
            bytesFromPrivateKey1[i + 4] = (byte)((int)((private_key1_2 & (long)(-16777216 >> i * 8)) >> 24 - i * 8));
        }

        private_key2_1 = (long)(Math.random() * 4.294967296E9);
        private_key2_2 = (long)(Math.random() * 4.294967296E9);

        for(i = 0; i < 4; ++i) {
            bytesFromPrivateKey2[i] = (byte)((int)((private_key2_1 & (long)(-16777216 >> i * 8)) >> 24 - i * 8));
        }

        for(i = 0; i < 4; ++i) {
            bytesFromPrivateKey2[i + 4] = (byte)((int)((private_key2_2 & (long)(-16777216 >> i * 8)) >> 24 - i * 8));
        }

        server_key = 0L;
        login_reference = "";
        user_reference = "";
        referenceCacheMap.remove(ipAddress);
    }

    private String getURLPrefix() {
        return isHttps ? "https://" : "http://";
    }

    private String jointUrlPath(String ipAdress, int port, int processType) {
        StringBuilder sb = new StringBuilder();
        if (processType != Auth_First && processType != Auth_Second) {
            sb.append(getURLPrefix()).append(ipAdress).append(":").append(port).append("/RPC");
        } else {
            sb.append(getURLPrefix()).append(ipAdress).append(":").append(port).append("/Ajax");
        }

        return sb.toString();
    }

    private void verifyConnection() throws IOException {
        String requestURL = getURLPrefix() + ipAddress + ":" + portNr + "/main.shtm";
        HttpUriRequest httpRequest = new HttpGet(requestURL);
        HttpResponse httpResponse = httpClient.execute(httpRequest);

        try {
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new IOException("connect exception");
            }
        } finally {
            EntityUtils.consume(httpResponse.getEntity());
        }

    }

    private boolean popSetPasswordPanel() {
        //TODO
//        String infoMsg = Language.getString("dialog.getPassword.label.access", "LSC access password: ", this.ipAddress);
//        LSCPasswordPanel passwordPanel = new LSCPasswordPanel("panel.InputPassword.title", infoMsg, (String)null);
        String password = null; //passwordPanel.getInputPassword();
        if (password == null) {
            return false;
        } else {
            setPassWord(password);
            return true;
        }
    }

    private HttpPost buildHttpRequest(String ipAdress, int port, int processType) {
        HttpPost request = new HttpPost(jointUrlPath(ipAdress, port, processType));
        if (processType == Auth_First) {
            request.setHeader("Security-Hint", "p");
            byte[] tempBytes = ("UAMCHAL:3,4," + private_key1_1 + "," + private_key1_2 + "," + private_key2_1 + "," + private_key2_2 + "," + User_Name).getBytes();
            request.setEntity(new ByteArrayEntity(isHttps ? tempBytes : LogoMath.base64_encode(LogoMath.asyEncrypt(tempBytes)).getBytes()));
        } else if (processType == Auth_Second) {
            String passWordToken = getPassword() + "+" + server_key;
            if (passWordToken.length() > 32) {
                passWordToken = passWordToken.substring(0, 32);
            }

            long lPassWordToken = LogoMath.getHashValue(passWordToken) ^ server_key;
            long lServerToken = private_key1_1 ^ private_key1_2 ^ private_key2_1 ^ private_key2_2 ^ server_key;
            String unEncryptedStr = "UAMLOGIN:" + User_Name + "," + lPassWordToken + "," + lServerToken;
            request.setHeader("Security-Hint", login_reference);
            request.setEntity(new ByteArrayEntity(isHttps ? unEncryptedStr.getBytes() : LogoMath.base64_encode(LogoMath.triSymEncrypt(unEncryptedStr.getBytes(), bytesFromPrivateKey1, bytesFromPrivateKey2)).getBytes()));
        } else {
            request.setHeader("Security-Hint", user_reference);
        }

        return request;
    }

    private void clearPwCache() {
        password = null;
        passwordLength = 0;
        PW_TIME_STAMP = 0L;
    }

    private boolean getAuthorization(boolean needClearPassword) throws IOException {
        if (System.currentTimeMillis() - PW_TIME_STAMP > (long)CACHE_TIME) {
            clearPwCache();
        }

        if (!"".equals(user_reference) && !needClearPassword) {
            return true;
        } else if (referenceCacheMap.containsKey(ipAddress)) {
            user_reference = referenceCacheMap.get(ipAddress).getUserReference();
            bytesFromPrivateKey1 = referenceCacheMap.get(ipAddress).getPrivateKey1();
            bytesFromPrivateKey2 = referenceCacheMap.get(ipAddress).getPrivateKey2();
            return true;
        } else {
            clearLoginInfo();
            if (!executeLogInStep1()) {
                return false;
            } else if (user_reference != null && !user_reference.isEmpty()) {
                return true;
            } else {
                boolean haveInputPassword = false;
                if (password == null) {
                    if (!popSetPasswordPanel()) {
                        throw new CancellationException();
                    }

                    haveInputPassword = true;
                } else if (needClearPassword) {
                    if (!popSetPasswordPanel()) {
                        throw new CancellationException();
                    }

                    haveInputPassword = true;
                }

                if (!executeLogInStep1()) {
                    return false;
                } else {
                    return executeLogInStep2(haveInputPassword);
                }
            }
        }
    }

    private Boolean executeLogInStep1() throws IOException {
        HttpUriRequest httpRequest = buildHttpRequest(ipAddress, portNr, Auth_First);
        HttpResponse httpResponse = httpClient.execute(httpRequest);

        try {
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                return false;
            } else {
                String responseStr = getResponseString(httpResponse.getEntity());
                String[] parameters = responseStr.split(",");
                try {
                    if (LogoMath.parseString2Long(parameters[0]) == AJAX_SUCCESS) {
                        if (parameters.length < 3) {
                            return false;
                        }

                        login_reference = parameters[1];
                        server_key = LogoMath.parseString2Long(parameters[2]);
                    } else {
                        if (LogoMath.parseString2Long(parameters[0]) != AJAX_NO_PASSWORD_REQUIRED) {
                            return false;
                        }

                        if (parameters.length < 2) {
                            return false;
                        }

                        user_reference = parameters[1];
                        referenceCacheMap.put(ipAddress, new UserSession(user_reference, bytesFromPrivateKey1, bytesFromPrivateKey2));
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
                return true;
            }
        } finally {
            EntityUtils.consume(httpResponse.getEntity());
        }
    }

    private Boolean executeLogInStep2(boolean haveInputPassword) throws IOException {
        HttpUriRequest httpRequest2 = buildHttpRequest(ipAddress, portNr, Auth_Second);
        HttpResponse httpResponse2 = httpClient.execute(httpRequest2);

        try {
            if (httpResponse2.getStatusLine().getStatusCode() == 200) {
                String responseStr2 = getResponseString(httpResponse2.getEntity());
                String[] parameters2 = responseStr2.split(",");
                if (parameters2.length >= 2) {
                    try {
                        if (LogoMath.parseString2Long(parameters2[0]) != AJAX_SUCCESS) {
                            if (!haveInputPassword) {
                                return getAuthorization(true);
                            }

                            return false;
                        }

                        user_reference = parameters2[1];
                        referenceCacheMap.put(ipAddress, new UserSession(user_reference, bytesFromPrivateKey1, bytesFromPrivateKey2));
                    } catch (NumberFormatException e) {
                        return false;
                    }

                    return true;
                }

                if (!haveInputPassword) {
                    return getAuthorization(true);
                }

                return false;
            }
        } finally {
            EntityUtils.consume(httpResponse2.getEntity());
        }
        return false;
    }

    private String getResponseString(HttpEntity httpEntity) throws IOException {
        byte[] bcdBytes = isHttps ? EntityUtils.toByteArray(httpEntity) : LogoMath.base64_decode(EntityUtils.toString(httpEntity));
        return new String(isHttps ? bcdBytes : LogoMath.delRearNilElement(LogoMath.triSymDecrypt(bcdBytes, bytesFromPrivateKey1, bytesFromPrivateKey2)));
    }

    public boolean prepareDt() throws IOException {
        boolean ret = getAuthorization4Exception();
        if (ret) {
            getHardware();
        }
        return ret;
    }

    public void setPassWord(String pw) {
        passwordLength = pw.length();
        PW_TIME_STAMP = System.currentTimeMillis();
        String fake_1 = UUID.randomUUID().toString().trim().replaceAll("-", "").substring(0, 13) + (int)(Math.random() * 10.0);
        String fake_2 = (int)(Math.random() * 10.0) + UUID.randomUUID().toString().trim().replaceAll("-", "").substring(0, 15 - passwordLength);
        password = fake_1 + pw + fake_2;
        if (null != pwCacheTimer) {
            pwCacheTimer.cancel();
        }

        pwCacheTimer = new Timer("clearPwCache");
        pwCacheTimer.schedule(new TimerTask() {
            public void run() {
                clearPwCache();
                System.gc();
            }
        }, CACHE_TIME);
    }

    private String getPassword() {
        return password.substring(14, 14 + passwordLength);
    }

    public synchronized byte[] readBytes() {
        isTransFinished = true;
        notifyAll();
        return responseContent;
    }

    public synchronized void writeBytes(byte[] bytes) throws IOException {
        if (!isTransFinished) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("break");
            }
        }

        isTransFinished = false;

        try {
            boolean ret = getAuthorization4Exception();
            if (!ret) {
                isTransFinished = true;
                throw new IOException("Authorize failed");
            } else {
                HttpPost httppost = buildHttpRequest(ipAddress, portNr, Process);
                httppost.setEntity(new ByteArrayEntity(isHttps ? bytes : LogoMath.triSymEncrypt(bytes, bytesFromPrivateKey1, bytesFromPrivateKey2)));
                HttpResponse httpResponse = httpClient.execute(httppost);

                try {
                    byte[] result = EntityUtils.toByteArray(httpResponse.getEntity());
                    responseContent = isHttps ? result : LogoMath.triSymDecrypt(result, bytesFromPrivateKey1, bytesFromPrivateKey2);
                    if (httpResponse.getStatusLine().getStatusCode() == 403) {
                        isTransFinished = true;
                        throw new IOException("Token expired");
                    }
                } finally {
                    EntityUtils.consume(httpResponse.getEntity());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            isTransFinished = true;
            notifyAll();
            throw e;
        }
    }

    private static void rebuildHttpClient() {
        httpClient = clientBuilder.build();
    }

    public void rebuildConnection() throws IOException {
        if (null != httpClient) {
            httpClient.close();
        }
        rebuildHttpClient();
    }

    public void closePort() {
        try {
            httpClient.close();
            rebuildHttpClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DeviceType getDeviceType() {
        try {
            boolean ret = prepareDt();
            if (ret) {
                return super.getDeviceType();
            } else {
                throw new IllegalStateException("Can't prepare data transfer");
            }
        } catch (IllegalStateException e) {
            clearLoginInfo();
            return getDeviceType();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            TrustManager[] wrappedTrustManagers = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String t) {}
                public void checkServerTrusted(X509Certificate[] certs, String t) {}
            } };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, wrappedTrustManagers, null);
            SSLConnectionSocketFactory sslConSocFactory = new SSLConnectionSocketFactory(sc, new NoopHostnameVerifier());
            clientBuilder = HttpClients.custom();
            clientBuilder.setSSLSocketFactory(sslConSocFactory);
            clientBuilder.setMaxConnPerRoute(1);
            clientBuilder.setConnectionTimeToLive(30L, TimeUnit.MINUTES);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).build();
            clientBuilder.setDefaultRequestConfig(requestConfig);
            httpClient = clientBuilder.build();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    @Getter
    private static class UserSession {
        private final String userReference;
        private final byte[] privateKey1;
        private final byte[] privateKey2;

        private UserSession(String userReference, byte[] privateKey1, byte[] privateKey2) {
            this.userReference = userReference;
            this.privateKey1 = privateKey1;
            this.privateKey2 = privateKey2;
        }
    }
}
