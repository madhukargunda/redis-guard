/**
 * Author: Madhu
 * User:madhu
 * Date:30/10/24
 * Time:12:07 AM
 * Project: redis-guard
 */

package io.madhu.redisGuard.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.KeyStore;

@Configuration
public class RedisConfig {

    private final ResourceLoader resourceLoader;
    private final String jksPassword;
    private final String jksPath;

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.ssl.enable}")
    private boolean isSslEnabled;

    public RedisConfig(ResourceLoader resourceLoader,
                       @Value("${spring.data.redis.ssl-config.jks-password}") String jksPassword,
                       @Value("${spring.data.redis.ssl-config.jks-path}") String jksPath) {
        this.resourceLoader = resourceLoader;
        this.jksPassword = jksPassword;
        this.jksPath = jksPath;
    }

    @Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() throws IOException {
        // Redis standalone configuration
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration("localhost", 6379);
        redisConfig.setUsername("admin");
        redisConfig.setPassword("madhukar");

        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();


        if (isSslEnabled) {
            SslOptions sslOptions = SslOptions.builder()
                    .trustManager(resourceLoader.getResource("classpath:redis.pem").getFile())
                    .build();

            ClientOptions clientOptions = ClientOptions
                    .builder()
                    .sslOptions(sslOptions)
                    .protocolVersion(ProtocolVersion.RESP2)
                    .build();

                    lettuceClientConfigurationBuilder
                    .clientOptions(clientOptions)
                    .useSsl();
                    //.startTls()
                   // .build();


            // Configure SSL with LettuceClientConfiguration
       /* LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .clientOptions(ClientOptions.builder().sslOptions(SslOptions.builder().sslContext(sslContextBuilder -> {
                            try {
                                getSSLContext();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })    // Use the JKS-based SSLContext
                        .build()).build()).clientResources(clientResources()).useSsl().build(); */

            // return new LettuceConnectionFactory(redisConfig, LettuceClientConfiguration.builder()
            //       .useSsl().build());
        }
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig, lettuceClientConfigurationBuilder.build());
        return factory;
    }


    private SSLContext getSSLContext() throws Exception {
        // Load the JKS file from the Resource
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (var keyStoreStream = resourceLoader.getResource(jksPath).getInputStream()) {
            keyStore.load(keyStoreStream, jksPassword.toCharArray()); // Use injected JKS password
        }

        // Load the Truststore
//        KeyStore trustStore = KeyStore.getInstance("JKS");
//        try (var trustStoreStream = resourceLoader.getResource(jksPath).getInputStream()) {
//            trustStore.load(trustStoreStream, jksPassword.toCharArray()); // Load truststore
//        }
        // Initialize the TrustManager with the JKS
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        // Set up SSLContext with TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}


