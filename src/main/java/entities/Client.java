package entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Entity(name = "Clients")
@Table(name = "clients")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client implements Serializable {

    public Client(String name, String symbol, int strategy, BigDecimal depoValue) {
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

    @NotAudited
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
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
    public String toString(){
        return getName();
    }
}