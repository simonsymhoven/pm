package entities.alternative;

import entities.client.Client;
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
import javax.persistence.Id;
import javax.persistence.FetchType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Entity(name = "Alternative")
@Table(name = "alternative")
@Audited
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alternative implements Serializable {

    public Alternative(String name, String symbol, String exchange, BigDecimal price, BigDecimal change, String currency) {
        this.name = name;
        this.symbol = symbol;
        this.exchange = exchange;
        this.price = price;
        this.change = change;
        this.currency = currency;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
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

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "alternatives")
    private Set<Client> clients;

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
        if (!(o instanceof Alternative)) {
            return false;
        }
        Alternative alternative = (Alternative) o;
        return getSymbol().equals(alternative.getSymbol());
    }

    @NotAudited
    @Override
    public int hashCode() {
        return getId();
    }
}
