package one.digitalinnovation.robot.service;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.market.CandlestickInterval;
import lombok.extern.slf4j.Slf4j;
import one.digitalinnovation.robot.entity.Candle;
import one.digitalinnovation.robot.mapper.BarMapper;
import one.digitalinnovation.robot.mapper.CandleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DoubleNum;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.time.ZoneId;

@Service
@Slf4j
public class BinanceService {

    private final BinanceApiWebSocketClient socketClient;
    private final CandleService candleService;
    private final CandleMapper candleMapper;
    private final BarMapper barMapper;
    private Integer RSI_LAP = 0;
    private final Integer RSI_PERIOD = 14;
    private BarSeries series = new BaseBarSeriesBuilder().withName("mySeries").withNumTypeOf(DoubleNum.class).build();
    private final TaLib taLib;
    private final CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;

    @Autowired
    public BinanceService(BinanceApiWebSocketClient socketClient, CandleService candleService, CandleMapper candleMapper, BarMapper barMapper, TaLib taLib) {
        this.socketClient = socketClient;
        this.candleService = candleService;
        this.candleMapper = candleMapper;
        this.barMapper = barMapper;
        this.taLib = taLib;
    }

    public Closeable getCandles() {
    //public Closeable getCandles(String pair) {
        log.info("Starting monitoring");
        Closeable closeable = socketClient.onCandlestickEvent("solusdt", interval, this::processCandle);
        System.out.println(closeable);
        return closeable;
    }

    private void processCandle(CandlestickEvent candleEvent) {
        Candle candle = candleMapper.candlestickEventToCandle(candleEvent);
        if(candleEvent.getBarFinal()) {
            candleService.saveCandle(candle);
            RSI_LAP++;
            System.out.println("RSI_LAP: " + RSI_LAP);
            candle.prettyPrint();
            series.addBar(barMapper.candleToBar(candle, interval));
            if(RSI_PERIOD.equals(RSI_LAP)) {
                System.out.println(taLib.calculateRSI(series, RSI_PERIOD));
                RSI_LAP = 0;
                series = new BaseBarSeriesBuilder().withName("mySeries").withNumTypeOf(DoubleNum.class).build();
            }
        }
    }

}
