package controllers.dashboard;

import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j2;
import sql.EntityPortfolioImpl;
import sql.EntityStockImpl;

@Log4j2
public class Update extends Task<Boolean> {

        private EntityStockImpl entityStock;
        private EntityPortfolioImpl entityPortfolio;

        Update() {
            this.entityPortfolio = new EntityPortfolioImpl();
            this.entityStock = new EntityStockImpl();
        }

        @Override
        protected Boolean call() {
            if (entityStock.updateAll()) {
                entityPortfolio.getAll().forEach(clientStock -> {
                    double shareValue = (clientStock.getStock().getShare() / 100.0)
                            * (clientStock.getClient().getStrategy() / 100.0) * 100.0;
                    clientStock.setShareTarget(shareValue);
                    double valueNewStock = clientStock.getStock().getPrice().doubleValue() * clientStock.getQuantity();
                    double clientDepoValue = clientStock.getClient().getDepoValue().doubleValue();
                    double shareValueIst = 100;
                    if (clientDepoValue != 0) {
                        shareValueIst = (valueNewStock / clientDepoValue) * 100.0;
                    }
                    clientStock.setShareActual(shareValueIst);
                    clientStock.setDiffRelative(clientStock.getShareTarget() - clientStock.getShareActual());
                    clientStock.setDiffAbsolute(
                            (int) ((clientStock.getDiffRelative() * clientStock.getClient().getDepoValue().doubleValue())
                                    / clientStock.getStock().getPrice().doubleValue() / 100.0));
                    entityPortfolio.update(clientStock);
                });
                return true;
            }
            return false;
        }

}
