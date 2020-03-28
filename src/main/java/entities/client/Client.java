package entities.client;

import entities.alternative.Alternative;
import entities.investment.Strategy;
import entities.stock.Stock;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;
import javax.persistence.JoinTable;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Entity(name = "Client")
@Table(name = "client", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id"),
        @UniqueConstraint(columnNames = "symbol") })
@Audited
@Data
@NoArgsConstructor
public class Client implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "symbol", unique = true)
    private String symbol;

    @Column(name = "capital")
    private BigDecimal capital;

    @Column(name = "comment")
    private String comment;

    @NotAudited
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "strategy_id", referencedColumnName = "id")
    private Strategy strategy;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "Client_Stock",
            joinColumns = {
                    @JoinColumn(name = "client_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "stock_id", referencedColumnName = "id")
            }
    )
    private Set<Stock> stocks;

    @ManyToMany(fetch = FetchType.EAGER, cascade =  {CascadeType.PERSIST, CascadeType.MERGE})
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
