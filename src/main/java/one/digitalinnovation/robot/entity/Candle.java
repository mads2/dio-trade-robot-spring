package one.digitalinnovation.robot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class Candle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private Long duration;

    private Long numberOfTrades;
    private BigDecimal volume;
    private BigDecimal takerBuyBaseAssetVolume;

    private LocalDateTime eventTime;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;

    private BigDecimal openPrice;
    private BigDecimal closePrice;

    private BigDecimal high;
    private BigDecimal low;

    public void prettyPrint() {
        log.info("Symbol: " + this.symbol);
        log.info("NumberOfTrades: " + this.numberOfTrades);
        log.info("volume: " + this.volume);
        log.info("takerBuyBaseAssetVolume: " + this.takerBuyBaseAssetVolume);
        log.info("eventTime: " + this.eventTime);
        log.info("OpenTime: " + this.openTime);
        log.info("closeTime: " + this.closeTime);
        log.info("Open: " + this.openPrice);
        log.info("Close: " + this.closePrice);
        log.info("High: " + this.high);
        log.info("Low: " + this.low);
        log.info("");
    }

}


