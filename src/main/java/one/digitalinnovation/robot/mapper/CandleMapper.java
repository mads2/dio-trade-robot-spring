package one.digitalinnovation.robot.mapper;


import com.binance.api.client.domain.event.CandlestickEvent;
import one.digitalinnovation.robot.entity.Candle;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class CandleMapper {

    public Candle candlestickEventToCandle(CandlestickEvent event) {
        return Candle.builder()
                .numberOfTrades(event.getNumberOfTrades())
                .symbol(event.getSymbol())
                .openTime(getDateTimeFromEpoch(event.getOpenTime()))
                .openPrice(new BigDecimal(event.getOpen()))
                .build();
    }

    private LocalDateTime getDateTimeFromEpoch(long eventTime) {
        return Instant.ofEpochMilli(eventTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
