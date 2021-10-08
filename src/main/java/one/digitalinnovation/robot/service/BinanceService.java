package one.digitalinnovation.robot.service;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.market.CandlestickInterval;
import lombok.extern.slf4j.Slf4j;
import one.digitalinnovation.robot.entity.Candle;
import one.digitalinnovation.robot.mapper.CandleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Closeable;

@Service
@Slf4j
public class BinanceService {

    private final BinanceApiWebSocketClient socketClient;
    private final CandleService candleService;
    private final CandleMapper candleMapper;

    @Autowired
    public BinanceService(BinanceApiWebSocketClient socketClient, CandleService candleService, CandleMapper candleMapper) {
        this.socketClient = socketClient;
        this.candleService = candleService;
        this.candleMapper = candleMapper;
    }

    @PostConstruct
    public Closeable getCandles() {
    //public Closeable getCandles(String pair) {
        log.info("Starting monitoring");
        Closeable closeable = socketClient.onCandlestickEvent("solusdt", CandlestickInterval.ONE_MINUTE, this::processCandle);
        System.out.println(closeable);
        return closeable;
    }

    private void processCandle(CandlestickEvent candleEvent) {
        Candle candle = candleMapper.candlestickEventToCandle(candleEvent);
        if(candleEvent.getBarFinal()) {
            candle.prettyPrint();
            candleService.saveCandle(candle);
        }
    }



}
