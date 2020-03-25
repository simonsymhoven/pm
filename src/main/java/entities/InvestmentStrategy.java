package entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentStrategy {
    private InvestmentDirective stockInvestment;
    private InvestmentDirective altInvestment;
    private InvestmentDirective ioanInvestment;
    private InvestmentDirective liquidityInvestment;
}
