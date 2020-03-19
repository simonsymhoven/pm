package yahooAPI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {
    private String symbol;
    private String date;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closedPrice;
}
