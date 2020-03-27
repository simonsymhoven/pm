package controllers.portfolio.stock.portfolio_stock_audit_modal;

import entities.portfolio.PortfolioStockRevision;
import lombok.Data;

import java.util.List;

@Data
class PortfolioStockAuditModalModel {
    private List<PortfolioStockRevision> revisions;
}
