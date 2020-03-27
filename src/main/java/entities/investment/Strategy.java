package entities.investment;

import entities.client.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToOne;
import javax.persistence.GenerationType;
import javax.persistence.FetchType;
import javax.persistence.Column;
import java.io.Serializable;

@Data
@Entity(name = "Strategy")
@Table(name = "strategy")
@AllArgsConstructor
@NoArgsConstructor
public class Strategy implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "stockLower")
    private double stockLower;

    @Column(name = "stockTarget")
    private double stockTarget;

    @Column(name = "stockUpper")
    private double stockUpper;

    @Column(name = "altLower")
    private double altLower;

    @Column(name = "altTarget")
    private double altTarget;

    @Column(name = "altUpper")
    private double altUpper;

    @Column(name = "ioanLower")
    private double ioanLower;

    @Column(name = "ioanTarget")
    private double ioanTarget;

    @Column(name = "ioanUpper")
    private double ioanUpper;

    @Column(name = "liquidityLower")
    private double liquidityLower;

    @Column(name = "liquidityTarget")
    private double liquidityTarget;

    @Column(name = "liquidityUpper")
    private double liquidityUpper;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "strategy")
    private Client client;
}
