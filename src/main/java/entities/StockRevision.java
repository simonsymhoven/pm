package entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.envers.RevisionType;

import java.util.Date;

@Data
@AllArgsConstructor
public class StockRevision {
    Stock stock;
    Date revisionDate;
    RevisionType revisionType;
}
