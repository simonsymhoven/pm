package controllers.dashboard;

import com.jfoenix.controls.JFXButton;
import entities.Client;
import entities.ClientStock;
import entities.Stock;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import json.JSONReaderImpl;
import lombok.extern.log4j.Log4j2;
import org.json.simple.parser.JSONParser;
import sql.EntityPortfolioImpl;
import sql.EntityStockImpl;
import sql.EntityClientImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
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
    public PieChart pieChart3;
    @FXML
    public Label lastUpdateStatus;
    @FXML
    public Label lastUpdateDate;
    @FXML
    public Label labelMarket;
    @FXML
    public Label labelStocks;
    @FXML
    public JFXButton updateStock;
    @FXML
    public VBox vBox;


    private DashModel dashModel;
    private JSONReaderImpl jsonReader;
    private EntityStockImpl entityStock;

    Random r = new Random();

    public DashController() {
        this.dashModel = new DashModel();
        this.jsonReader = new JSONReaderImpl(new JSONParser());
        this.entityStock = new EntityStockImpl();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        update();

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

    }


    private void loadMarkets(PieChart pieChart) {
        List<Stock> stocks =  entityStock.getAll();
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
            pieChart.setLegendSide(Side.RIGHT);
            pieChart.setData(data);
        } else {
            labelMarket.setText("Es stehen noch keine Daten zur Analyse zur Verfügung.");
        }

    }

    private void loadStocks(PieChart pieChart) {
        List<Stock> stocks = entityStock.getAll();
        List<PieChart.Data> list = new ArrayList<>();

        if (stocks.size() > 0 ){
            labelStocks.setText("");
            stocks.forEach( stock ->
                    list.add(new PieChart.Data(stock.getSymbol(), stock.getShare()))
            );

            ObservableList<PieChart.Data> data = FXCollections.observableArrayList(list);
            pieChart.setLabelsVisible(false);
            pieChart.setLegendVisible(true);
            pieChart.setLegendSide(Side.RIGHT);
            pieChart.setData(data);
        } else {
            labelStocks.setText("Es stehen noch keine Daten zur Analyse zur Verfügung.");
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

                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                        loadMarkets(pieChart1);
                        loadStocks(pieChart3);
                    }));

                    timeline.setCycleCount(1);
                    timeline.play();
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
