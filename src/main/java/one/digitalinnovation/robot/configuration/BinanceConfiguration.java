package one.digitalinnovation.robot.configuration;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BinanceConfiguration {

    @Value("${binance.apikey}")
    private String auth;
    @Value("${binance.apisecret}")
    private String pass;

    @Bean
    public BinanceApiRestClient binanceRestClient() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(auth, pass);
        return factory.newRestClient();
    }

    @Bean
    public BinanceApiAsyncRestClient binanceAsyncRestClient() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(auth, pass);
        return factory.newAsyncRestClient();
    }

    @Bean
    public BinanceApiWebSocketClient binanceWebSocketClient() {
        return BinanceApiClientFactory.newInstance().newWebSocketClient();
    }

}
