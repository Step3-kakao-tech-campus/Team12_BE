package pickup_shuttle.pickup.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean(name = "proxyRestTemplate")
    public RestTemplate restTemplate() {
        HttpHost proxy = new HttpHost("krmp-proxy.9rum.cc", 3128);
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TimeValue.ofMilliseconds(5000).toTimeout())
                .setResponseTimeout(TimeValue.ofMilliseconds(10000).toTimeout())
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setProxy(proxy)
                .setDefaultRequestConfig(config)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }
}
