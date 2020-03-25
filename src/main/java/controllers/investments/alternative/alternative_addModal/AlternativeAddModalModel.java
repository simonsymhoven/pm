package controllers.investments.alternative.alternative_addModal;

import entities.alternative.Alternative;
import lombok.Data;

@Data
class AlternativeAddModalModel {
    private String symbol;
    private Alternative alternative;
    private double amount = 100.0;
}
