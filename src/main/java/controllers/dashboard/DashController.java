package controllers.dashboard;

import com.jfoenix.controls.JFXButton;
import entities.Client;
import entities.ClientStock;
import entities.Stock;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
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
    public JFXButton updateStock;
    @FXML
    public VBox vBox;
    @FXML
    private PieChart chartMarket;
    @FXML
    private PieChart chartStocks;
    @FXML
    private Label labelMarkets;
    @FXML
    private Label labelStocks;

    private ObservableList<PieChart.Data> marketData;
    private ObservableList<PieChart.Data> stockData;


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

    private void setupAnimation(ObservableList<PieChart.Data> data, Label label) {
        data.stream().forEach(pieData -> {
            pieData.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                label.setText(pieData.getName() + ": " +  String.format("%.2f", pieData.getPieValue()) + "%");
                label.setLabelFor(pieData.getNode());

                Bounds b1 = pieData.getNode().getBoundsInLocal();
                double newX = (b1.getWidth()) / 2 + b1.getMinX();
                double newY = (b1.getHeight()) / 2 + b1.getMinY();
                // Make sure pie wedge location is reset
                pieData.getNode().setTranslateX(0);
                pieData.getNode().setTranslateY(0);
                // Create the animation
                TranslateTransition tt = new TranslateTransition(
                        Duration.millis(1500), pieData.getNode());
                tt.setByX(newX);
                tt.setByY(newY);
                tt.setAutoReverse(true);
                tt.setCycleCount(2);
                tt.play();

                tt.setOnFinished(e -> {
                    label.setText("");
                });
            });
        });
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
                        marketData = loadMarkets();
                        chartMarket.setData(marketData);
                        chartMarket.setLabelsVisible(false);
                        chartMarket.setLegendSide(Side.RIGHT);
                        setupAnimation(marketData, labelMarkets);

                        stockData = loadClients();
                        chartStocks.setData(stockData);
                        chartStocks.setLabelsVisible(false);
                        chartStocks.setLegendSide(Side.RIGHT);
                        setupAnimation(stockData, labelStocks);
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

    private ObservableList<PieChart.Data> loadMarkets() {
        List<Stock> stocks = entityStock.getAll();
        List<PieChart.Data> list = new ArrayList<>();
        if (!stocks.isEmpty()) {
            Map<String, Integer> map = new HashMap<>();
            stocks.forEach(aktie -> {
                if (map.containsKey(aktie.getExchange())) {
                    Integer value = map.get(aktie.getExchange());
                    map.put(aktie.getExchange(), ++value);
                } else {
                    map.put(aktie.getExchange(), 1);
                }
            });

            map.forEach((key, value) -> list.add(new PieChart.Data(key, value)));
        }
        return FXCollections.observableArrayList(list);
    }

    private ObservableList<PieChart.Data> loadClients() {
        List<Stock> stocks = entityStock.getAll();
        List<PieChart.Data> list = new ArrayList<>();

        if (!stocks.isEmpty()) {
            stocks.forEach(stock -> list.add(new PieChart.Data(stock.getSymbol(), stock.getShare())));
        }

        return FXCollections.observableArrayList(list);
    }
}
