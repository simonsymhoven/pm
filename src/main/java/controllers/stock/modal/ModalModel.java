package controllers.stock.modal;

import entities.Stock;
import lombok.Data;

@Data
public class ModalModel {
    private String symbol;
    private Stock stock;
}
