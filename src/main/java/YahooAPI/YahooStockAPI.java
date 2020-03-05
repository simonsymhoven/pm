package YahooAPI;

import lombok.extern.log4j.Log4j2;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
public class YahooStockAPI {
    public StockDTO getStock(String stockName) {
        StockDTO dto = null;
        try {
            Stock stock = YahooFinance.get(stockName);
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.add(Calendar.YEAR, Integer.parseInt("-1"));
            List<History> history = new ArrayList<>();
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

            dto = new StockDTO(
                    stock.getName(),
                    stock.getQuote().getPrice(),
                    stock.getQuote().getChange(),
                    stock.getCurrency(),
                    history
            );

        } catch (IOException e) {
           log.error(e);
        }

        return dto;
    }


    private String convertDate(Calendar cal) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(cal.getTime());
    }


}
