package controllers.dashboard;

import lombok.Data;

import java.util.Date;

@Data
class DashModel {
    Date lastUpdate;
    String status;
}
