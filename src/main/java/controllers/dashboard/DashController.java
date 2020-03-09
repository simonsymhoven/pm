package controllers.dashboard;

import entities.Client;
import entities.Stock;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import sql.EntityStockImpl;
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
    public Label labelMarket;
    @FXML
    public Label labelClients;
    @FXML
    public Button updateStock;
    @FXML
    public VBox vBox;

    private DashModel dashModel;
    private JSONReaderImpl jsonReader;
    private EntityStockImpl entityAktien;
    private EntityClientImpl entityClient;

    Random r = new Random();

    public DashController() {
        this.dashModel = new DashModel();
        this.jsonReader = new JSONReaderImpl(new JSONParser());
        this.entityAktien = new EntityStockImpl();
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
    }

    private void loadMarkets(PieChart pieChart) {
        List<Stock> stocks =  entityAktien.getAll();
        if (stocks.size() > 0 ) {
            labelMarket.setText("");
            Map<String, Integer> map = new HashMap<>();
            stocks.forEach(aktie -> {
                if (map.containsKey(aktie.getExchange())) {
                    Integer value = map.get(aktie.getExchange());
                    map.put(aktie.getExchange(), ++value);
                } else {
                    map.put(aktie.getExchange(), 1);
                }
            });

            List<PieChart.Data> list = new ArrayList<>();
            map.forEach((key, value) -> {
                list.add(new PieChart.Data(key, value));
            });

            ObservableList<PieChart.Data> data = FXCollections.observableArrayList(list);
            pieChart.setLabelsVisible(false);
            pieChart.setLegendVisible(true);
            pieChart.setLegendSide(Side.LEFT);
            pieChart.setData(data);
        } else {
            labelMarket.setText("Es stehen noch keine Daten zur Analyse zur Verfügung.");
        }

    }

    private void loadClients(PieChart pieChart) {
        List<Client> clients = entityClient.getAll();
        List<PieChart.Data> list = new ArrayList<>();

        if (clients.size() > 0 ){
            labelClients.setText("");
            clients.forEach( client ->
                    list.add(new PieChart.Data(client.getName(), client.getDepoValue().doubleValue()))
            );

            ObservableList<PieChart.Data> data = FXCollections.observableArrayList(list);
            pieChart.setLabelsVisible(false);
            pieChart.setLegendVisible(true);
            pieChart.setLegendSide(Side.RIGHT);
            pieChart.setData(data);
        } else {
            labelClients.setText("Es stehen noch keine Daten zur Analyse zur Verfügung.");
        }

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
