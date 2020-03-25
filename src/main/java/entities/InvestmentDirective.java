package entities;

import lombok.Data;

@Data
public class InvestmentDirective {
    public InvestmentDirective(Investment investment) {
        this.investment = investment;
    }

    private Investment investment;
    private double lowerLimit;
    private double target;
    private double upperLimit;
}
