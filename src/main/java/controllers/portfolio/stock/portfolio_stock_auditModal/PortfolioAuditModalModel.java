package controllers.portfolio.stock.portfolio_stock_auditModal;

import entities.portfolio.PortfolioAlternativeRevision;
import lombok.Data;
import java.util.List;

@Data
class PortfolioAuditModalModel {
    private List<PortfolioAlternativeRevision> revisions;
}
