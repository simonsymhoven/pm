package controllers.client.client_auditModal;

import entities.ClientRevision;
import lombok.Data;

import java.util.List;

@Data
public class ClientAuditModalModel {
    private List<ClientRevision> revisions;
}
