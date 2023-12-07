import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class NaiveTrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        // Trust all clients
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        // Trust all servers
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
