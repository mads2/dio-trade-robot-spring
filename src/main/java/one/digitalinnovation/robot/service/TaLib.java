package one.digitalinnovation.robot.service;

import one.digitalinnovation.robot.Interface.Indicators;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

@Service
public class TaLib implements Indicators {

    @Override
    public Double calculateRSI(BarSeries series, int barCount) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(closePrice, barCount);
        return rsi.getValue(barCount - 1).doubleValue();
    }

}
