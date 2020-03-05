package entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Entity(name = "Stock")
@Table(name = "stock")

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Stock implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    public int id;

    @Column(name = "name")
    public String name;

    @Column(name = "symbol")
    public String symbol;

    @Column(name = "exchange")
    public String exchange;

    @Column(name = "price")
    public BigDecimal price;

    @Column(name = "change")
    public BigDecimal change;

    @Column(name = "currency")
    public String currency;

    @Override
    public String toString(){
        return this.name;
    }

    public Stock(String name, String symbol, String exchange, BigDecimal price, BigDecimal change, String currency) {
        this.name = name;
        this.symbol = symbol;
        this.exchange = exchange;
        this.price = price;
        this.change = change;
        this.currency = currency;
    }

    @ManyToMany(mappedBy = "stocks", fetch = FetchType.EAGER)
    Set<Client> clients;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock)) return false;
        Stock stock = (Stock) o;
        return getSymbol().equals(stock.getSymbol());
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
