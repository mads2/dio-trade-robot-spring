package one.digitalinnovation.robot.enums;

public enum TradeStatus {

    BOUGHT(true),
    SOLD(false);

    boolean tradeStatus;

    TradeStatus(boolean tradeStatus) {
        this.tradeStatus = tradeStatus;
    }
}
