package controllers.dashboard;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import json.JSONReaderImpl;
import lombok.extern.log4j.Log4j2;
import org.json.simple.parser.JSONParser;
import sql.EntityAktienImpl;
import sql.EntityClientImpl;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public class DashController implements Initializable {
    @FXML
    public PieChart pieChart1;
    @FXML
    public PieChart pieChart2;
    @FXML
    public Label lastUpdateStatus;
    @FXML
    public Label lastUpdateDate;
    @FXML
    public Button updateStock;
    @FXML
    public VBox vBox;

    private DashModel dashModel;
    private JSONReaderImpl jsonReader;
    private EntityAktienImpl entityAktien;
    private EntityClientImpl entityClient;

    Random r = new Random();

    public DashController() {
        this.dashModel = new DashModel();
        this.jsonReader = new JSONReaderImpl(new JSONParser());
        this.entityAktien = new EntityAktienImpl();
        this.entityClient = new EntityClientImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        try {
            String date = jsonReader.read("lastUpdateDate").toString();
            dashModel.setLastUpdate(simpleDateFormat.parse(date));
            dashModel.setStatus(jsonReader.read("lastUpdateStatus").toString());

            lastUpdateDate.setText(simpleDateFormat.format(dashModel.getLastUpdate()));
            lastUpdateStatus.setText("Status: " + dashModel.getStatus());
        } catch (ParseException e) {
            log.error(e);
        }

        updateStock.setOnMouseClicked(mouseEvent -> update());

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            loadMarkets(pieChart1);
            loadClients(pieChart2);
        }));

        timeline.setCycleCount(1);
        timeline.play();

        final Label caption = new Label("");
        caption.setTextFill(Color.DARKORANGE);
        caption.setStyle("-fx-font: 24 arial;");

        timeline.setOnFinished(x -> {
            for (final PieChart.Data data : pieChart1.getData()) {
                data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                    caption.setTranslateX(e.getSceneX());
                    caption.setTranslateY(e.getSceneY());
                    caption.setText(data.getPieValue() + " %");
                });
            }
        });
    }

    private void loadMarkets(PieChart pieChart) {
        Map<String, Integer> map = new HashMap<>();
        entityAktien.getAll().forEach(aktie -> {
            if (map.containsKey(aktie.getMarket())) {
                Integer value = map.get(aktie.getMarket());
                map.put(aktie.getMarket(), ++value);
            } else {
                map.put(aktie.getMarket(), 1);
            }
        });

        List<PieChart.Data> list = new ArrayList<>();
        map.forEach((key, value) -> {
            list.add(new PieChart.Data(key, value));
        });

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(list);
        pieChart.setLabelLineLength(10);
        pieChart.setLegendSide(Side.LEFT);
        pieChart.setData(data);

    }

    private void loadClients(PieChart pieChart) {

        List<PieChart.Data> list = new ArrayList<>();
        entityClient.getAll().forEach( client ->
                list.add(new PieChart.Data(client.getSymbol(), client.getDepoValue().doubleValue()))
        );

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(list);
        pieChart.setData(data);
    }


    private void update(){
        Update task = new Update();

        task.setOnRunning(successesEvent -> {
            dashModel.setStatus("Offen");
            lastUpdateStatus.setText(dashModel.getStatus());
        });

        task.setOnSucceeded(succeededEvent -> {
            try {
                if (task.get()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                   dashModel.setStatus("Abgeschlossen");
                   lastUpdateStatus.setText(dashModel.getStatus());

                   dashModel.setLastUpdate(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
                   lastUpdateDate.setText(simpleDateFormat.format(dashModel.getLastUpdate()));

                   jsonReader.write("lastUpdateDate", simpleDateFormat.format(dashModel.getLastUpdate()));
                   jsonReader.write("lastUpdateStatus", "Abgeschlossen");
                }
            } catch (InterruptedException | ExecutionException | IOException | ParseException e) {
                log.error(e);
            }
        });

        task.setOnFailed(failedEvent -> {
            dashModel.setStatus("Abgebrochen");
            lastUpdateStatus.setText(dashModel.getStatus());
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(task);
        executorService.shutdown();
    }

}
