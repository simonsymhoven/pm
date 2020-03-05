package controllers.aktien.modal;

import entities.Stock;
import lombok.Data;

@Data
public class ModalModel {
    private String symbol;
    private Stock stock;
}
