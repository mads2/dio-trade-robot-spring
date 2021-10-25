package one.digitalinnovation.robot.service;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.*;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.CandlestickInterval;
import lombok.extern.slf4j.Slf4j;
import one.digitalinnovation.robot.entity.Candle;
import one.digitalinnovation.robot.entity.Trade;
import one.digitalinnovation.robot.enums.TradeStatus;
import one.digitalinnovation.robot.mapper.BarMapper;
import one.digitalinnovation.robot.mapper.CandleMapper;
import one.digitalinnovation.robot.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DoubleNum;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class BinanceService {

    private final BinanceApiRestClient restClient;
    private final BinanceApiWebSocketClient socketClient;

    private final CandleService candleService;
    private final CandleMapper candleMapper;
    private final TradeRepository tradeRepository;
    private final BarMapper barMapper;
    private final TaLib taLib;

    private final CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
    private final Integer RSI_PERIOD = 14;
    private final Integer RSI_UP = 70;
    private final Integer RSI_DOWN = 30;
    private TradeStatus tradeStatus = TradeStatus.BOUGHT;
    private BarSeries series;
    private Integer RSI_LAP = 0;
    private String tradePair;
    private Double baseBalance;
    private Double quoteBalance;
    private static List<Closeable> closeableList = new ArrayList<>();

    @Autowired
    public BinanceService(BinanceApiRestClient restClient, BinanceApiWebSocketClient socketClient, CandleService candleService, CandleMapper candleMapper, TradeRepository tradeRepository, BarMapper barMapper, TaLib taLib) {
        this.restClient = restClient;
        this.socketClient = socketClient;
        this.candleService = candleService;
        this.candleMapper = candleMapper;
        this.tradeRepository = tradeRepository;
        this.barMapper = barMapper;
        this.taLib = taLib;
    }

    public void setUpPairEnviroment(String tradePair) {
        this.tradePair = tradePair.toLowerCase();
        series = new BaseBarSeriesBuilder().withName(this.tradePair).withNumTypeOf(DoubleNum.class).build();

        Account account = restClient.getAccount();
        if(!account.isCanTrade()) {
            throw new RuntimeException("Account can't trade");
        }

        ExchangeInfo exchangeInfo = restClient.getExchangeInfo();
        Optional<SymbolInfo> pairConfigsOptional = exchangeInfo.getSymbols()
                .stream().filter(symbolInfo ->
                        symbolInfo.getSymbol().equalsIgnoreCase(this.tradePair)).findFirst();
        if(pairConfigsOptional.isEmpty()) {
            throw new RuntimeException("Coin not found");
        }
        SymbolInfo pairConfigs = pairConfigsOptional.get();
        log.info("Pair filters: " + pairConfigs.getFilters());
        Optional<AssetBalance> baseAssetBalanceOptional = account.getBalances().stream().filter(assetBalance -> assetBalance.getAsset().equalsIgnoreCase(pairConfigs.getBaseAsset())).findFirst();
        Optional<AssetBalance> quoteAssetBalanceOptional = account.getBalances().stream().filter(assetBalance -> assetBalance.getAsset().equalsIgnoreCase(pairConfigs.getQuoteAsset())).findFirst();
        if(baseAssetBalanceOptional.isEmpty() || quoteAssetBalanceOptional.isEmpty()) {
            throw new RuntimeException("Asset balance not found");
        }
        //AssetBalance baseAssetBalance = baseAssetBalanceOptional.get();
        //AssetBalance quoteAssetBalance = quoteAssetBalanceOptional.get();
        this.baseBalance = 5.0;//Double.valueOf(baseAssetBalance.getFree());
        this.quoteBalance = 50.0;//Double.valueOf(quoteAssetBalance.getFree());

        //if(freeBalance < 0.01) {
        //    throw new RuntimeException("Balance too low");
        //}
        //TickerPrice price = restClient.getPrice(tradePair);

        Closeable candlesCloseable = getCandles(this.tradePair);
        closeableList.add(candlesCloseable);
    }

    public Closeable getCandles(String tradePair) {
        log.info("Start monitoring");
        return socketClient.onCandlestickEvent(tradePair, interval, this::processCandle);
    }

    private void processCandle(CandlestickEvent candleEvent) {
        //log.info("Processing candle!");
        Candle candle = candleMapper.candlestickEventToCandle(candleEvent);
        if(candleEvent.getBarFinal()) {
            candleService.saveCandle(candle);
            RSI_LAP++;
            log.info("RSI_LAP: " + RSI_LAP);
            candle.prettyPrint();
            series.addBar(barMapper.candleToBar(candle, interval));
            if(RSI_PERIOD.equals(RSI_LAP)) {
                Double rsi = taLib.calculateRSI(series, RSI_PERIOD);
                log.info("Rsi for the period: " + rsi);
                verifyTradeability(rsi, candle);
                RSI_LAP = 0;
                series = new BaseBarSeriesBuilder().withName(tradePair).withNumTypeOf(DoubleNum.class).build();
            }
        }
    }

    private void verifyTradeability(Double rsi, Candle candle) {
        if(rsi > RSI_UP) {
            if(tradeStatus != TradeStatus.BOUGHT) {
                NewOrder order = new NewOrder(tradePair.toUpperCase(), OrderSide.BUY, OrderType.MARKET, TimeInForce.GTC, quoteBalance.toString());
                order.timeInForce(null); // for test only
                log.info("Trying to buy " + quoteBalance + " of " + tradePair);
                try {
                    restClient.newOrderTest(order);
                    tradeStatus = TradeStatus.BOUGHT;

                    //baseBalance = baseBalance + quoteBalance * candle.getClosePrice().longValue();
                    //quoteBalance = 0.0;

                    saveTrade(candle, order);
                } catch (Exception e) { //
                    log.warn(e.toString());
                    saveTradeError(candle, order, e);
                }
            } else {
                log.info("Already bought");
            }
        } else if(rsi < RSI_DOWN) {
            if(tradeStatus != TradeStatus.SOLD) {
                NewOrder order = new NewOrder(tradePair.toUpperCase(), OrderSide.SELL, OrderType.MARKET, TimeInForce.GTC, baseBalance.toString());
                order.timeInForce(null); // for test only
                log.info("Trying to sell " + baseBalance + " of " + tradePair);
                try {
                    restClient.newOrderTest(order);
                    tradeStatus = TradeStatus.SOLD;

                    //quoteBalance = quoteBalance + baseBalance * candle.getClosePrice().longValue();
                    //baseBalance = 0.0;

                    saveTrade(candle, order);
                } catch (Exception e) { //
                    log.warn(e.toString());
                    saveTradeError(candle, order, e);
                }
            } else {
                log.info("Already sold");
            }
        } else {
            log.info(RSI_DOWN + " < " + rsi + " < " + RSI_UP + " - nothing to do");
        }
    }

    private void saveTrade(Candle candle, NewOrder order) {
        if(Objects.nonNull(order)) {
            Trade trade = Trade.builder()
                    .timestamp(CandleMapper.getDateTimeFromEpoch(order.getTimestamp()))
                    .orderSide(order.getSide().toString())
                    .quantity(order.getQuantity())
                    .price(order.getPrice())
                    .relatedCandle(candle)
                    .test(true)
                    .build();
            tradeRepository.save(trade);
        }
    }

    private void saveTradeError(Candle candle, NewOrder order, Exception e) {
        Trade trade = Trade.builder()
                .timestamp(CandleMapper.getDateTimeFromEpoch(order.getTimestamp()))
                .relatedCandle(candle)
                .test(true)
                .error(e.getMessage())
                .build();
        tradeRepository.save(trade);
    }

    public void closeAll() throws IOException {
        for(Closeable closeable : closeableList) {
            closeable.close();
        }
    }
}
