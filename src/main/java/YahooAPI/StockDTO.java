package YahooAPI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StockDTO {
    String name;
    BigDecimal price;
    BigDecimal change;
    String currency;
    List<History> history;
}
