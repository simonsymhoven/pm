package YahooAPI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {
    String symbol;
    String date;
    BigDecimal highPrice;
    BigDecimal lowPrice;
    BigDecimal closedPrice;
}
