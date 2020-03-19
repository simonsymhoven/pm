package entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ClientStockKey implements Serializable {
    @Column(name = "client_id")
    int clientID;

    @Column(name = "stock_id")
    int stockID;


}
