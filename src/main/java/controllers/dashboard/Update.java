package controllers.dashboard;

import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j2;
import sql.EntityAktienImpl;

@Log4j2
public class Update extends Task<Boolean> {

        private EntityAktienImpl entityAktien;

        Update() {
            this.entityAktien = new EntityAktienImpl();
        }

        @Override
        protected Boolean call() {
            entityAktien.updateAll();
            return true;
        }


}
