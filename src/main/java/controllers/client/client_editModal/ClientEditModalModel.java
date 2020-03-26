package controllers.client.client_editModal;

import entities.client.Client;
import entities.investment.Investment;
import entities.investment.InvestmentDirective;
import lombok.Data;

import java.math.BigDecimal;

@Data
class ClientEditModalModel {
    private Client client = new Client();
    private String name;
    private String symbol;
    private String comment = "";
    private InvestmentDirective stockInvestment = new InvestmentDirective(Investment.STOCK);
    private InvestmentDirective alternativeInvestment = new InvestmentDirective(Investment.ALTERNATIVE);
    private InvestmentDirective ioanInvestment = new InvestmentDirective(Investment.IOAN);
    private InvestmentDirective liquidityInvestment = new InvestmentDirective(Investment.LIQUIDITY);
    private BigDecimal depoValue;
}
