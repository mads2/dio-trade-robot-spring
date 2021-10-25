package one.digitalinnovation.robot.repository;

import one.digitalinnovation.robot.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
}
