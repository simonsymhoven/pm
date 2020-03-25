package controllers.investments.alternative;

import entities.alternative.Alternative;
import lombok.Data;
import yahooapi.History;
import java.util.List;

@Data
public class AlternativeModel {
    private List<Alternative> alternatives;
    private Alternative alternative;
    private List<History> history;
}
