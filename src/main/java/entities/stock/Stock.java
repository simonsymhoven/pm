package entities.stock;

import entities.client.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.UniqueConstraint;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import javax.persistence.Id;
import javax.persistence.FetchType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Entity(name = "Stock")
@Table(name = "stock", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id"),
        @UniqueConstraint(columnNames = "symbol")})
@Audited
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "symbol", unique = true)
    private String symbol;

    @Column(name = "exchange")
    private String exchange;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "change")
    private BigDecimal change;

    @Column(name = "currency")
    private String currency;

    @Column(name = "share")
    private double share;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "stocks")
    private Set<Client> clients;

    public Stock(String name, String symbol, String exchange, BigDecimal price, BigDecimal change, String currency) {
        this.name = name;
        this.symbol = symbol;
        this.exchange = exchange;
        this.price = price;
        this.change = change;
        this.currency = currency;
    }

    @NotAudited
    @Override
    public String toString() {
        return this.name;
    }

    @NotAudited
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stock)) {
            return false;
        }
        Stock stock = (Stock) o;
        return getSymbol().equals(stock.getSymbol());
    }

    @NotAudited
    @Override
    public int hashCode() {
        return getId();
    }
}
