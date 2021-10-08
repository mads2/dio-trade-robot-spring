package one.digitalinnovation.robot.service;

import one.digitalinnovation.robot.entity.Candle;
import one.digitalinnovation.robot.repository.CandleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CandleService {

    private final CandleRepository candleRepository;

    @Autowired
    public CandleService(CandleRepository candleRepository) {
        this.candleRepository = candleRepository;
    }

    public void saveCandle(Candle candle) {
        candleRepository.save(candle);
    }
}
