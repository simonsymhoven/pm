package entities.client.clientStock;

import entities.client.Client;
import entities.stock.Stock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.EmbeddedId;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;


@Entity(name = "Client_Stock")
@Table(name = "client_stock")

@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientStock {
        @EmbeddedId
        private ClientStockKey id;

        @ManyToOne
        @MapsId("client_id")
        @JoinColumn(name = "client_id")
        private Client client;

        @ManyToOne
        @MapsId("stock_id")
        @JoinColumn(name = "stock_id")
        private Stock stock;

        @Column(name = "quantity", nullable = false, columnDefinition = "int default 0")
        private int quantity;

        @Column(name = "shareTarget", nullable = false, columnDefinition = "number default 0")
        private double shareTarget;

        @Column(name = "shareActual", nullable = false, columnDefinition = "number default 0")
        private double shareActual;

        @Column(name = "diffRelative", nullable = false, columnDefinition = "number default 0")
        private double diffRelative;

        @Column(name = "diffAbsolute", nullable = false, columnDefinition = "int default 0")
        private int diffAbsolute;

        @NotAudited
        public void calculate() {
                double shareValue = (getStock().getShare() / 100.0)
                        * (getClient().getInvestmentStrategy().getStockInvestment().getTarget() / 100.0) * 100.0;
                this.setShareTarget(shareValue);

                double valueNewStock = getStock().getPrice().doubleValue() * getQuantity();
                double clientDepoValue = getClient().getDepoValue().doubleValue();
                double shareValueIst = 100;
                if (clientDepoValue != 0) {
                        shareValueIst = (valueNewStock / clientDepoValue) * 100.0;
                }
                this.setShareActual(shareValueIst);
                this.setDiffRelative(getShareTarget() - getShareActual());
                this.setDiffAbsolute(
                        (int) ((getDiffRelative() * getClient().getDepoValue().doubleValue())
                                / getStock().getPrice().doubleValue() / 100.0));
        }
}

