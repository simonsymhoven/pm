package entities;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class ClientStockKey implements Serializable {
    @Column(name = "client_id")
    int client_id;

    @Column(name = "stock_id")
    int stock_id;
}
