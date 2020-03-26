package entities.investment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.FetchType;
import java.io.Serializable;

@Data
@Entity(name = "InvestementStrategy")
@Table(name = "investementStrategy")
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentStrategy implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "stockInvestment")
    private InvestmentDirective stockInvestment;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "altInvestment")
    private InvestmentDirective altInvestment;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ioanInvestment")
    private InvestmentDirective ioanInvestment;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "liquidityInvestment")
    private InvestmentDirective liquidityInvestment;

    public InvestmentStrategy(InvestmentDirective stockInvestment,
                              InvestmentDirective alternativeInvestment,
                              InvestmentDirective ioanInvestment,
                              InvestmentDirective liquidityInvestment) {
        this.stockInvestment = stockInvestment;
        this.altInvestment = alternativeInvestment;
        this.ioanInvestment = ioanInvestment;
        this.liquidityInvestment = liquidityInvestment;
    }
}
