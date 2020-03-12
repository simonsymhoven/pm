package entities;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity(name = "Client_Stock")
@Table(name = "client_stock")

@Audited
@Data
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
}
