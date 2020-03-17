package controllers.dashboard;

import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j2;
import sql.EntityStockImpl;

@Log4j2
public class Update extends Task<Boolean> {

        private EntityStockImpl entityAktien;

        Update() {
            this.entityAktien = new EntityStockImpl();
        }

        @Override
        protected Boolean call() {
            return entityAktien.updateAll();
        }


}
