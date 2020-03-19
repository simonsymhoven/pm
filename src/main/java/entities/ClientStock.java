package entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity(name = "Client_Stock")
@Table(name = "client_stock")

@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientStock {
        @EmbeddedId
        ClientStockKey id;

        @ManyToOne
        @MapsId("client_id")
        @JoinColumn(name = "client_id")
        Client client;

        @ManyToOne
        @MapsId("stock_id")
        @JoinColumn(name = "stock_id")
        Stock stock;

        @Column(name = "quantity", nullable = false, columnDefinition = "int default 0")
        int quantity;

        @Column(name = "shareTarget", nullable = false, columnDefinition = "number default 0")
        double shareTarget;

        @Column(name = "shareActual", nullable = false, columnDefinition = "number default 0")
        double shareActual;

        @Column(name = "diffRelative", nullable = false, columnDefinition = "number default 0")
        double diffRelative;

        @Column(name = "diffAbsolute", nullable = false, columnDefinition = "int default 0")
        int diffAbsolute;
}

