package one.digitalinnovation.robot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "related_candle_id")
    private Candle relatedCandle;

    private String orderSide;

    private LocalDateTime timestamp;

    private String quantity;

    private String price;

    private Boolean test;

    private String error;

}
