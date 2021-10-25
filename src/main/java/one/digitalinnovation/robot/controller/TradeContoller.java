package one.digitalinnovation.robot.controller;

import one.digitalinnovation.robot.service.BinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("trade")
public class TradeContoller {

    private final BinanceService binanceService;

    @Autowired
    public TradeContoller(BinanceService binanceService) {
        this.binanceService = binanceService;
    }

    @PostMapping("/{tradePair}")
    public void trade(@PathVariable String tradePair) {
        binanceService.setUpPairEnviroment(tradePair);
    }

    @PostMapping("/close")
    public void closeAll() throws IOException {
        binanceService.closeAll();
    }

    @GetMapping("/test")
    public String test() {
        return String.valueOf(System.currentTimeMillis());
    }

}
