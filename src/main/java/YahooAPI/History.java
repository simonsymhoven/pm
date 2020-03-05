package YahooAPI;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class History {
    String symbol;
    String date;
    BigDecimal highPrice;
    BigDecimal lowPrice;
    BigDecimal closedPrice;
}
