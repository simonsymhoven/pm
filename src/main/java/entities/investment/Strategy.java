package entities.investment;

import entities.client.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "Strategy")
@Table(name = "strategy")
@AllArgsConstructor
@NoArgsConstructor
public class Strategy implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "stock")
    private InvestmentDirective stockInvestment;

    @Column(name = "alternative")
    private InvestmentDirective altInvestment;

    @Column(name = "ioan")
    private InvestmentDirective ioanInvestment;

    @Column(name = "liquidity")
    private InvestmentDirective liquidityInvestment;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "strategy")
    private Client client;

    public Strategy(InvestmentDirective stockInvestment,
                    InvestmentDirective alternativeInvestment,
                    InvestmentDirective ioanInvestment,
                    InvestmentDirective liquidityInvestment) {
        this.stockInvestment = stockInvestment;
        this.altInvestment = alternativeInvestment;
        this.ioanInvestment = ioanInvestment;
        this.liquidityInvestment = liquidityInvestment;
    }
}
