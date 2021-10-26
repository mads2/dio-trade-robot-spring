<h2>Binance crypto trading bot: Trade cryptos at Binance with customized strategies</h2>

This Springboot application listens to the Binance orderbook in the selected pairs, store some values, calculate indicators using TA-Lib and create orders based on predefined rules.

** Note: The orders are being handled by the ```api/v3/order/test``` endpoint, so they're not real.

The stack consists of:
* Spring Boot 2.5.4
* MySql 8 and JPA
* Mapstruct
* TaLib
* Binance Java Api
* Aws RDS
* Aws EC2
* Java 11

To run locally:

```shell script
mvn spring-boot:run 
```

To start a automatically trading a new pair:

```
POST:     http://localhost:8080/trade/{pair}
```
To stop trading all pairs:
```
GET:     http://localhost:8080/trade/close
```


Info about recent trades made by the API may be avaliable at:
 https://mjy4eq48aa.execute-api.us-east-1.amazonaws.com/
