package entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity(name = "Aktien")
@Table(name = "aktien")

@Data
@NoArgsConstructor
public class Aktie implements Serializable {
    @Id
    @Column(name = "id")
    public int id;

    @Column(name = "name")
    public String name;

    @Column(name = "symbol")
    public String symbol;

    @Column(name = "isin")
    public String isin;

    @Column(name = "gr")
    public String gr;

    @Column(name = "sector")
    public String sector;

    @Column(name = "market")
    public String market;

    @Column(name = "buyPrice")
    public BigDecimal buyPrice;

    @Column(name = "quant")
    public int quant;

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
}
