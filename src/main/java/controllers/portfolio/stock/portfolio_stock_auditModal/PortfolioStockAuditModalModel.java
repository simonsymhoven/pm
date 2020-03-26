package controllers.portfolio.stock.portfolio_stock_auditModal;

import entities.portfolio.PortfolioStockRevision;
import lombok.Data;

import java.util.List;

@Data
class PortfolioStockAuditModalModel {
    private List<PortfolioStockRevision> revisions;
}
