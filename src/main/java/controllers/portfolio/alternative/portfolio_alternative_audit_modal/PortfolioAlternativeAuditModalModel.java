package controllers.portfolio.alternative.portfolio_alternative_audit_modal;

import entities.portfolio.PortfolioAlternativeRevision;
import lombok.Data;
import java.util.List;

@Data
class PortfolioAlternativeAuditModalModel {
    private List<PortfolioAlternativeRevision> revisions;
}
