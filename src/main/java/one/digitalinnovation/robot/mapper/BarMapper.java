package one.digitalinnovation.robot.mapper;

import com.binance.api.client.domain.market.CandlestickInterval;
import one.digitalinnovation.robot.entity.Candle;
import org.springframework.stereotype.Component;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;

import java.time.Duration;
import java.time.ZoneId;

@Component
public class BarMapper {

    public Bar candleToBar(Candle candle, CandlestickInterval interval) {
        return new BaseBar(
                Duration.ofSeconds(candlestickIntervalToSeconds(interval)),
                candle.getCloseTime().atZone(ZoneId.of("UTC")),
                candle.getOpenPrice().doubleValue(),
                candle.getHigh().doubleValue(),
                candle.getLow().doubleValue(),
                candle.getClosePrice().doubleValue(),
                candle.getVolume().doubleValue());
    }

    private Long candlestickIntervalToSeconds(CandlestickInterval interval) {
        switch (interval) {
            case ONE_MINUTE:
                return 60L;
            case THREE_MINUTES:
                return 180L;
            case FIVE_MINUTES:
                return 300L;
            case FIFTEEN_MINUTES:
                return 900L;
            case HALF_HOURLY:
                return 1800L;
            case HOURLY:
                return 3600L;
            case TWO_HOURLY:
                return 7200L;
            case FOUR_HOURLY:
                return 14400L;
            case SIX_HOURLY:
                return 21600L;
            case EIGHT_HOURLY:
                return 28800L;
            case TWELVE_HOURLY:
                return 43200L;
            case DAILY:
                return 86400L;
            case THREE_DAILY:
                return 259200L;
            case WEEKLY:
                return 604800L;
            case MONTHLY:
                return 2592000L;
            default:
                throw new RuntimeException("CandlestickInterval error");
        }
    }
}
