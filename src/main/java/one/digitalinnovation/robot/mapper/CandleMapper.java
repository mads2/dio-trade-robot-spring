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
                .duration(event.getCloseTime() - event.getOpenTime())
                .volume(new BigDecimal(event.getVolume()))
                .takerBuyBaseAssetVolume(new BigDecimal(event.getTakerBuyBaseAssetVolume()))
                .symbol(event.getSymbol())
                .eventTime(getDateTimeFromEpoch(event.getEventTime()))
                .openTime(getDateTimeFromEpoch(event.getOpenTime()))
                .closeTime(getDateTimeFromEpoch(event.getCloseTime()))
                .openPrice(new BigDecimal(event.getOpen()))
                .closePrice(new BigDecimal(event.getClose()))
                .high(new BigDecimal(event.getHigh()))
                .low(new BigDecimal(event.getLow()))
                .build();
    }

    public static LocalDateTime getDateTimeFromEpoch(long eventTime) {
        return Instant.ofEpochMilli(eventTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
