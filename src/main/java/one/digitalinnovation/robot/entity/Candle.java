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

    private Long numberOfTrades;

    private LocalDateTime openTime;

    private BigDecimal openPrice;

    public void prettyPrint() {
        log.info("Symbol: " + this.getSymbol());
        log.info("NumberOfTrades: " + this.getNumberOfTrades());
        log.info("OpenTime: " + this.openTime);
        log.info("Open: " + this.openPrice);
        log.info("");
    }

}


