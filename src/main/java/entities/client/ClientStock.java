package entities.client;

import entities.stock.Stock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
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
}
