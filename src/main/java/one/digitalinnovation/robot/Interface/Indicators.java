package one.digitalinnovation.robot.Interface;

import org.ta4j.core.BarSeries;

public interface Indicators {

    Double calculateRSI(BarSeries series, int barCount);

}
