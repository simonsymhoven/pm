package controllers.investments.alternative.alternative_add_modal;

import entities.alternative.Alternative;
import lombok.Data;

@Data
class AlternativeAddModalModel {
    private String symbol;
    private Alternative alternative;
}
