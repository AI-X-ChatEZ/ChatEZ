package aix.project.chatez.myservice;

import jakarta.annotation.PostConstruct;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenSearchClient {

    @Value("${open-search.hostname}")
    private String hostnameInstance;

    @Value("${open-search.username}")
    private String usernameInstance;

    @Value("${open-search.password}")
    private String passwordInstance;

    private static String HOSTNAME;
    private static final int PORT = 443;
    private static final String SCHEME = "https";
    private static String USERNAME;
    private static String PASSWORD;

    @PostConstruct
    public void init() {
        HOSTNAME = hostnameInstance;
        USERNAME = usernameInstance;
        PASSWORD = passwordInstance;
    }

    public static RestHighLevelClient createClient() {

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(USERNAME, PASSWORD));

        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(HOSTNAME, PORT, SCHEME))
                        .setHttpClientConfigCallback(httpClientBuilder ->
                                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
        );
    }
}