package controllers.stock.stock_auditModal;

import entities.StockRevision;
import lombok.Data;

import java.util.List;


@Data
public class StockAuditModalModel {
    private List<StockRevision> revisions;
}
