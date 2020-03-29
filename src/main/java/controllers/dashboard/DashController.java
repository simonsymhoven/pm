package controllers.dashboard;

import entities.stock.Stock;
import javafx.application.Platform;
import json.JSONReader;
import snackbar.SnackBar;
import com.jfoenix.controls.JFXButton;
import controllers.login.LoginController;
import entities.client.Client;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j2;
import sql.EntityClientImpl;
import sql.EntityStockImpl;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class DashController implements Initializable {
    @FXML
    private Label lastUpdateStatus;
    @FXML
    private Label lastUpdateDate;
    @FXML
    private JFXButton updateStock;
    @FXML
    private PieChart chartMarket;
    @FXML
    private PieChart chartStocks;
    @FXML
    private Label labelMarkets;
    @FXML
    private Label labelStocks;
    @FXML
    private Label counterMarkets;
    @FXML
    private Label counterStocks;
    @FXML
    private Label counterClients;
    @FXML
    private Label counterValue;
    @FXML
    private Label name;
    @FXML
    private AnchorPane pane;

    private ObservableList<PieChart.Data> marketData;
    private ObservableList<PieChart.Data> stockData;

    private DashModel dashModel;
    private JSONReader jsonReader;
    private EntityStockImpl entityStock;
    private EntityClientImpl entityClient;

    public DashController() {
        this.dashModel = new DashModel();
        this.jsonReader = new JSONReader();
        this.entityStock = new EntityStockImpl();
        this.entityClient = new EntityClientImpl();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.setText(LoginController.getLoggedUser().getVorname());

        drawCounters();
        drawCharts();

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String date = jsonReader.read("lastUpdateDate").toString();
            if (!"".equals(date)) {
                dashModel.setLastUpdate(simpleDateFormat.parse(date));
                dashModel.setStatus(jsonReader.read("lastUpdateStatus").toString());
                lastUpdateDate.setText(simpleDateFormat.format(dashModel.getLastUpdate()));
                lastUpdateStatus.setText("Status: " + dashModel.getStatus());
            } else {
                lastUpdateDate.setText("-");
                lastUpdateStatus.setText("");
            }

        } catch (ParseException e) {
            log.error(e);
        }

        updateStock.setOnMouseClicked(mouseEvent -> update());
    }

    private void drawCounters() {
        List<Client> clients = entityClient.getAll();

        double value = 0;
        for (Client client : clients) {
            value += client.getCapital().doubleValue();
        }

        createCounter(clients.size(), counterClients);
        createCounter(value, counterValue);
    }

    private void setupAnimation(ObservableList<PieChart.Data> data, Label label) {
        if (data.size() == 0) {
            if (label.getId().equals("labelMarkets")) {
                label.setText("Es sind noch keine Märkte zur Auswertung vorhanden!");
            } else {
                label.setText("Es sind noch keine Aktien im Musterportfolio zur Auswertung vorhanden!");
            }
        }
        data.forEach(pieData -> pieData.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (label.getId().equals("labelMarkets")) {
                label.setText(pieData.getName() + ": " +  pieData.getPieValue() + " Stk.");
            } else {
                label.setText(pieData.getName() + ": " +  String.format("%.2f", pieData.getPieValue()) + "%");
            }
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

            tt.setOnFinished(e -> label.setText(""));
        }));
    }

    private void update() {
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

                    SnackBar snackBar = new SnackBar(pane);
                    snackBar.show("Aktienkurse wurden erfolgreich aktualisiert!");

                    drawCounters();
                    drawCharts();
                }
            } catch (Exception e) {
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

    private void drawCharts() {
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

    private ObservableList<PieChart.Data> loadMarkets() {
        List<Stock> stocks = entityStock.getAll();
        List<PieChart.Data> list = new ArrayList<>();
        if (!stocks.isEmpty()) {
            Map<String, Integer> map = new HashMap<>();
            stocks.forEach(stock -> {
                if (map.containsKey(stock.getExchange())) {
                    Integer value = map.get(stock.getExchange());
                    map.put(stock.getExchange(), ++value);
                } else {
                    map.put(stock.getExchange(), 1);
                }
            });
            map.forEach((key, value) -> list.add(new PieChart.Data(key, value)));
            createCounter(list.size(), counterMarkets);
        }
        return FXCollections.observableArrayList(list);
    }

    private ObservableList<PieChart.Data> loadClients() {
        List<Stock> stocks = entityStock.getAll();
        List<PieChart.Data> list = new ArrayList<>();

        if (!stocks.isEmpty()) {
            createCounter(stocks.size(), counterStocks);
            stocks.forEach(stock -> list.add(new PieChart.Data(stock.getSymbol(), stock.getShare())));
        }

        return FXCollections.observableArrayList(list);
    }

    private void createCounter(int size, Label label) {
        AtomicInteger count = new AtomicInteger(0);
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            if (count.get() <= size) {
                label.setText(String.valueOf(count.getAndIncrement()));
            }
        }));
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

    private void createCounter(double value, Label label) {
        AtomicReference<Double> count = new AtomicReference<>((double) 0);
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(1), e -> {
            if (count.get() <= value) {
                count.getAndSet((count.get() + 1000));
                String text = NumberFormat.getCurrencyInstance()
                        .format(new BigDecimal(count.get()))
                        .replace("EUR", "EUR ");
                label.setText(text);
            } else {
                String text = NumberFormat.getCurrencyInstance()
                        .format(BigDecimal.valueOf(value))
                        .replace("EUR", "EUR ");
                label.setText(text);
            }
        }));
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }
}
