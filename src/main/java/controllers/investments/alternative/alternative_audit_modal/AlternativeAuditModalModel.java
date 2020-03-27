package controllers.investments.alternative.alternative_audit_modal;

import entities.alternative.AlternativeRevision;
import lombok.Data;

import java.util.List;


@Data
class AlternativeAuditModalModel {
    private List<AlternativeRevision> revisions;
}
