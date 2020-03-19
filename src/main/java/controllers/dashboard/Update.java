package controllers.dashboard;

import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j2;
import sql.EntityStockImpl;

@Log4j2
public class Update extends Task<Boolean> {

        private EntityStockImpl entityStock;

        Update() {
            this.entityStock = new EntityStockImpl();
        }

        @Override
        protected Boolean call() {
            return entityStock.updateAll();
        }

}
