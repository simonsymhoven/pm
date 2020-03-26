package controllers.dashboard;

import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j2;
import sql.EntityAlternativeImpl;
import sql.EntityPortfolioStockImpl;
import sql.EntityStockImpl;

@Log4j2
public class Update extends Task<Boolean> {

        private EntityStockImpl entityStock;
        private EntityAlternativeImpl entityAlternative;
        private EntityPortfolioStockImpl entityPortfolioStock;

        Update() {
            this.entityPortfolioStock = new EntityPortfolioStockImpl();
            this.entityStock = new EntityStockImpl();
            this.entityAlternative = new EntityAlternativeImpl();
        }

        @Override
        protected Boolean call() {
            if (entityStock.updateAll() && entityAlternative.updateAll()) {
                entityPortfolioStock.getAll().forEach(clientStock -> {
                    clientStock.calculate();
                    entityPortfolioStock.update(clientStock);
                });
                return true;
            }
            return false;
        }

}
