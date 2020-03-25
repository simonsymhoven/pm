package entities.client;

import entities.alternative.Alternative;
import entities.client.investement.InvestmentStrategy;
import entities.stock.Stock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Entity(name = "Clients")
@Table(name = "clients")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client implements Serializable {
    public Client(
            String name,
            String symbol,
            InvestmentStrategy investmentStrategy,
            BigDecimal depoValue,
            String comment) {
        this.name = name;
        this.symbol = symbol;
        // STOCK DIRECTIVE
        this.strategyStocksLowerLimit = investmentStrategy.getStockInvestment().getLowerLimit();
        this.strategyStocksTargetValue = investmentStrategy.getStockInvestment().getTarget();
        this.strategyStocksUpperLimit = investmentStrategy.getStockInvestment().getUpperLimit();
        // ALTERNATIVE DIRECTIVE
        this.strategyAlternativeLowerLimit = investmentStrategy.getAltInvestment().getLowerLimit();
        this.strategyAlternativeTargetValue = investmentStrategy.getAltInvestment().getTarget();
        this.strategyAlternativeUpperLimit = investmentStrategy.getAltInvestment().getUpperLimit();
        // IOAN DIRECTIVE
        this.strategyIoanLowerLimit = investmentStrategy.getIoanInvestment().getLowerLimit();
        this.strategyIoanTargetValue = investmentStrategy.getIoanInvestment().getTarget();
        this.strategyIoanUpperLimit = investmentStrategy.getIoanInvestment().getUpperLimit();
        // LIQUIDITY DIRECTIVE
        this.strategyLiquidityLowerLimit = investmentStrategy.getLiquidityInvestment().getLowerLimit();
        this.strategyLiquidityTargetValue = investmentStrategy.getLiquidityInvestment().getTarget();
        this.strategyLiquidityUpperLimit = investmentStrategy.getLiquidityInvestment().getUpperLimit();
        this.depoValue = depoValue;
        this.comment = comment;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "symbol", unique = true)
    private String symbol;

    @Column(name = "strategyStocksLowerLimit")
    private double strategyStocksLowerLimit;

    @Column(name = "strategyStocksTargetValue")
    private double strategyStocksTargetValue;

    @Column(name = "strategyStocksUpperLimit")
    private double strategyStocksUpperLimit;

    @Column(name = "strategyAlternativeLowerLimit")
    private double strategyAlternativeLowerLimit;

    @Column(name = "strategyAlternativeTargetValue")
    private double strategyAlternativeTargetValue;

    @Column(name = "strategyAlternativeUpperLimit")
    private double strategyAlternativeUpperLimit;

    @Column(name = "strategyIoanLowerLimit")
    private double strategyIoanLowerLimit;

    @Column(name = "strategyIoanTargetValue")
    private double strategyIoanTargetValue;

    @Column(name = "strategyIoanUpperLimit")
    private double strategyIoanUpperLimit;

    @Column(name = "strategyLiquidityLowerLimit")
    private double strategyLiquidityLowerLimit;

    @Column(name = "strategyLiquidityTargetValue")
    private double strategyLiquidityTargetValue;

    @Column(name = "strategyLiquidityUpperLimit")
    private double strategyLiquidityUpperLimit;

    @Column(name = "depoValue")
    private BigDecimal depoValue;

    @Column(name = "comment")
    private String comment;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "Client_Stock",
            joinColumns = {
                    @JoinColumn(
                            name = "client_id",
                            referencedColumnName = "id"
                    )
            },
            inverseJoinColumns = {
                    @JoinColumn(
                            name = "stock_id",
                            referencedColumnName = "id"
                    )
            }
    )
    private Set<Stock> stocks;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "Client_Alternative",
            joinColumns = {
                    @JoinColumn(
                            name = "client_id",
                            referencedColumnName = "id"
                    )
            },
            inverseJoinColumns = {
                    @JoinColumn(
                            name = "alternative_id",
                            referencedColumnName = "id"
                    )
            }
    )
    private Set<Alternative> alternatives;

    @NotAudited
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        Client client = (Client) o;
        return getId() == client.getId();
    }

    @NotAudited
    @Override
    public int hashCode() {
        return getId();
    }

    @NotAudited
    @Override
    public String toString() {
        return getName();
    }
}
