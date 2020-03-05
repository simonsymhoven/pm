package entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity(name = "Aktien")
@Table(name = "aktien")

@Data
@NoArgsConstructor
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
}
