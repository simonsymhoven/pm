package entities;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "Clients")
@Table(name = "clients")

@Data
public class Client {
    public Client(){

    }

    public Client(int id, String name, String symbol, int strategy, BigDecimal depoValue) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.strategy = strategy;
        this.depoValue = depoValue;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    public int id;

    @Column(name = "name")
    public String name;

    @Column(name = "symbol")
    public String symbol;

    @Column(name = "strategy")
    public int strategy;

    @Column(name = "depoValue")
    public BigDecimal depoValue;

}
