package controllers;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.hibernate.envers.RevisionType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Box {
    public HBox generateAuditHBox(RevisionType revisionType, Date revisionDate, String text) {
        HBox hbox = new HBox();
        hbox.setPrefSize(550, 70);
        hbox.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 0.5;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: #344955;");
        Image img = null;

        switch (revisionType) {
            case ADD:
                img = new Image(getClass().getResourceAsStream("/icons/round_add_white_48dp.png"));
                break;
            case DEL:
                img = new Image(getClass().getResourceAsStream("/icons/round_delete_white_48dp.png"));
                break;
            case MOD:
                img = new Image(getClass().getResourceAsStream("/icons/round_edit_white_48dp.png"));
                break;
            default:
                break;
        }


        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(70);
        imageView.setFitHeight(70);

        VBox imgBox = new VBox();
        imgBox.setStyle("-fx-background-color: #344955");
        imgBox.setPrefSize(70, 70);
        imgBox.getChildren().add(imageView);

        VBox dateChangeBox = new VBox();
        dateChangeBox.setPadding(new Insets(20, 5, 5, 20));
        dateChangeBox.setPrefSize(480, 70);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm");
        HBox date = new HBox();
        date.setPrefSize(480, 35);
        Label lDate = new Label();
        String sdate =  simpleDateFormat.format(revisionDate);
        sdate = sdate.replace(" ", ", um ");
        lDate.setText("Am " + sdate + " Uhr wurde folgende Änderung durchgeführt:");
        date.getChildren().add(lDate);

        HBox change = new HBox();
        change.setPrefSize(480, 35);
        Label lChange = new Label();
        lChange.setText(text);
        change.getChildren().add(lChange);

        dateChangeBox.getChildren().addAll(date, change);

        hbox.getChildren().addAll(imgBox, dateChangeBox);
        return hbox;
    }
}
