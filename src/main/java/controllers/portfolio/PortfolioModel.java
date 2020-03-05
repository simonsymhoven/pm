package controllers.portfolio;

import entities.Aktie;
import javafx.scene.input.DataFormat;
import lombok.Data;

import java.util.List;

@Data
class PortfolioModel {
    private List<Aktie> aktienList;
    private Aktie aktie;
    final DataFormat AKTIE_LIST = new DataFormat("AktieList");
}
