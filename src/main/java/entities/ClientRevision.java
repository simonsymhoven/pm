package entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.envers.RevisionType;

import java.util.Date;

@Data
@AllArgsConstructor
public class ClientRevision {
    Client client;
    Date revisionDate;
    RevisionType revisionType;
}