package one.digitalinnovation.robot.repository;

import one.digitalinnovation.robot.entity.Candle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandleRepository extends JpaRepository<Candle, Long> {
}
