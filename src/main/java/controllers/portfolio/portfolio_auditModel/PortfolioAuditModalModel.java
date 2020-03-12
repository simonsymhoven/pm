package controllers.portfolio.portfolio_auditModel;

import entities.ClientRevision;
import entities.PortfolioRevision;
import lombok.Data;

import java.util.List;

@Data
public class PortfolioAuditModalModel {
    private List<PortfolioRevision> revisions;
}
