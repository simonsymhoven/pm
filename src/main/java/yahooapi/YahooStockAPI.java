package yahooapi;

import entities.alternative.Alternative;
import entities.stock.Stock;
import lombok.extern.log4j.Log4j2;
import org.javamoney.moneta.Money;
import sql.EntityStockImpl;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

@Log4j2
public class YahooStockAPI {
    public Stock getStock(String stockName) {
        Stock s = null;
        try {
            yahoofinance.Stock stock = YahooFinance.get(stockName);
            BigDecimal price = convertToEUR(stock.getCurrency(), stock.getQuote().getPrice());
            BigDecimal change = convertToEUR(stock.getCurrency(), stock.getQuote().getChange());
            if (stock.isValid()) {
                s = new Stock(
                        stock.getName(),
                        stock.getSymbol(),
                        stock.getStockExchange(),
                        price,
                        change,
                        stock.getCurrency()
                );
            }
        } catch (IOException e) {
           log.error(e);
        }

        return s;
    }

    public Alternative getAlternative(String alternativeName) {
        Alternative a = null;
        try {
            yahoofinance.Stock stock = YahooFinance.get(alternativeName);

            BigDecimal price = convertToEUR(stock.getCurrency(), stock.getQuote().getPrice());
            BigDecimal change = convertToEUR(stock.getCurrency(), stock.getQuote().getChange());
            if (stock.isValid()) {
                a = new Alternative(
                        stock.getName(),
                        stock.getSymbol(),
                        stock.getStockExchange(),
                        price,
                        change,
                        stock.getCurrency()
                );
            }
        } catch (IOException e) {
            log.error(e);
        }

        return a;
    }



    public List<History> getHistory(String name) {
        List<History> history = new ArrayList<>();

        try {
            yahoofinance.Stock stock = YahooFinance.get(name);
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.add(Calendar.MONTH, Integer.parseInt("-2"));
            stock.getHistory(from, to, Interval.DAILY).forEach(item ->
                    history.add(
                            new History(
                                    item.getSymbol(),
                                    convertDate(item.getDate()),
                                    convertToEUR(stock.getCurrency(), item.getHigh()),
                                    convertToEUR(stock.getCurrency(), item.getLow()),
                                    convertToEUR(stock.getCurrency(), item.getClose())
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

    private BigDecimal convertToEUR(String currencyCode, BigDecimal price) {
        CurrencyConversion euroConversion = MonetaryConversions.getConversion("EUR");

        MonetaryAmount moneyToConvert = Money.of(price, currencyCode.toUpperCase());
        MonetaryAmount moneyInEur = moneyToConvert.with(euroConversion);

        return BigDecimal.valueOf(moneyInEur.getNumber().doubleValue());
    }

    public static void main(String[] args) {
        try {
            File myObj = new File("/Users/simonsymhoven/Documents/Selbstaendigkeit/projekte/schmitz/pm/db/stocks.txt");
            Scanner myReader = new Scanner(myObj);
            EntityStockImpl entityStock = new EntityStockImpl();
            YahooStockAPI yahooStockAPI = new YahooStockAPI();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
                Stock stock = yahooStockAPI.getStock(data.split(";")[0]);
                stock.setShare(Double.valueOf(data.split(";")[1].replace(",", ".")));
                entityStock.add(stock);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }
}
