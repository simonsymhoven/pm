package controllers.portfolio.alternative.portfolio_alternative_auditModal;

import entities.portfolio.PortfolioAlternativeRevision;
import lombok.Data;
import java.util.List;

@Data
class PortfolioAlternativeAuditModalModel {
    private List<PortfolioAlternativeRevision> revisions;
}
