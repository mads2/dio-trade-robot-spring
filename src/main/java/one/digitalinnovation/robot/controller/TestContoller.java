package one.digitalinnovation.robot.controller;

import one.digitalinnovation.robot.service.BinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
public class TestContoller {

    @Autowired
    private BinanceService binanceService;

    @GetMapping("/test")
    public void test() {
        binanceService.getCandles();
    }

}
