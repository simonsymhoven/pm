package controllers.portfolio.portfolio_auditModel;

import entities.PortfolioRevision;
import lombok.Data;
import java.util.List;

@Data
class PortfolioAuditModalModel {
    private List<PortfolioRevision> revisions;
}
