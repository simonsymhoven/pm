package entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Entity(name = "Clients")
@Table(name = "clients")

@Data
public class Client implements Serializable {
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Client_Stock",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "stock_id"))
    Set<Stock> stocks;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return getId() == client.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String toString(){
        return getName();
    }
}
