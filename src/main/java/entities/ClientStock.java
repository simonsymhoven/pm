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

        @Column(name = "quantatiy", nullable = false, columnDefinition = "int default 0")
        int quantatiy;

}
