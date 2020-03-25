package entities.alternative;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.envers.RevisionType;

import java.util.Date;

@Data
@AllArgsConstructor
public class AlternativeRevision {
    private Alternative alternative;
    private Date revisionDate;
    private RevisionType revisionType;
}
