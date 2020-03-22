package controllers.dashboard;

import lombok.Data;

import java.util.Date;

@Data
class DashModel {
    private Date lastUpdate;
    private String status;
}
