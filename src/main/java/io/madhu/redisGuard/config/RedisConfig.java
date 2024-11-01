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
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.KeyStore;

@Configuration
public class RedisConfig {

    private final ResourceLoader resourceLoader;
    private final String pemCertificate = "";

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.ssl.enable}")
    private boolean isSslEnabled;

    @Value("${spring.data.redis.host}")
    private String hostName;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.ssl-config.jks-password}")
    private String jksPassword;

    @Value("${spring.data.redis.ssl-config.jks-path}")
    private String jksPath;

    @Value("${spring.data.redis.database}")
    private Integer database;

    public RedisConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() throws IOException {
        // Redis standalone configuration
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(hostName, port);
        redisConfig.setUsername(username);
        redisConfig.setPassword(password);
        redisConfig.setDatabase(database);
        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();

        if (isSslEnabled) {
            SslOptions sslOptions = SslOptions.builder()
                    .trustManager(resourceLoader.getResource("classpath:redis_cert.pem").getFile())
                    .build();

          /*  SslOptions sslOptions = SslOptions.builder()
                    .trustManager((SslOptions.Resource) new ByteArrayInputStream(pemCertificate.getBytes()))
                    .build(); */

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

    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}


