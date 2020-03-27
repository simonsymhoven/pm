package controllers.investments.stocks.stock_audit_modal;

import entities.stock.StockRevision;
import lombok.Data;

import java.util.List;


@Data
class StockAuditModalModel {
    private List<StockRevision> revisions;
}
