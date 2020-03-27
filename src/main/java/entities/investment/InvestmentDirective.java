package entities.investment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity(name = "InvestementDirective")
@Table(name = "investementDirective")
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentDirective implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Enumerated
    @Column(name = "name")
    private Investment investment;

    @Column(name = "lowerLimit")
    private double lowerLimit;

    @Column(name = "target")
    private double target;

    @Column(name = "upperLimit")
    private double upperLimit;

    public InvestmentDirective(Investment investment) {
        this.investment = investment;
    }
}
