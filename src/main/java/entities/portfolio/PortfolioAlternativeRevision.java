package entities.portfolio;

import entities.client.client_alternative.ClientAlternative;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.envers.RevisionType;

import java.util.Date;

@Data
@AllArgsConstructor
public class PortfolioAlternativeRevision {
    private ClientAlternative clientAlternative;
    private Date revisionDate;
    private RevisionType revisionType;
}
