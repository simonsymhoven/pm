package yahooapi;

import entities.Stock;
import lombok.extern.log4j.Log4j2;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
public class YahooStockAPI {
    public Stock getStock(String stockName) {
        Stock s = null;
        try {
            yahoofinance.Stock stock = YahooFinance.get(stockName);

            s = new Stock(
                    stock.getName(),
                    stock.getSymbol(),
                    stock.getStockExchange(),
                    stock.getQuote().getPrice(),
                    stock.getQuote().getChange(),
                    stock.getCurrency()
            );

        } catch (IOException e) {
           log.error(e);
        }

        return s;
    }


    public List<History> getHistory(String stockName) {
        List<History> history = new ArrayList<>();

        try {
            yahoofinance.Stock stock = YahooFinance.get(stockName);
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.add(Calendar.YEAR, Integer.parseInt("-1"));
            stock.getHistory(from, to, Interval.DAILY).forEach(item ->
                    history.add(
                            new History(
                                    item.getSymbol(),
                                    convertDate(item.getDate()),
                                    item.getHigh(),
                                    item.getLow(),
                                    item.getClose()
                            )
                    )
            );

        } catch (IOException e) {
            log.error(e);
        }

        return history;
    }




    private String convertDate(Calendar cal) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(cal.getTime());
    }


}
