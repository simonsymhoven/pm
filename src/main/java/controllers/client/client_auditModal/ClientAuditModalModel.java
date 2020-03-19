package controllers.client.client_auditModal;

import entities.ClientRevision;
import lombok.Data;

import java.util.List;

@Data
class ClientAuditModalModel {
    private List<ClientRevision> revisions;
}
