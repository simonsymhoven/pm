package controllers.client.client_audit_modal;

import entities.client.ClientRevision;
import lombok.Data;

import java.util.List;

@Data
class ClientAuditModalModel {
    private List<ClientRevision> revisions;
}
