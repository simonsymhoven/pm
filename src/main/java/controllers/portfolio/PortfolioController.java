package controllers.portfolio;

import entities.Aktie;
import enums.ClientState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import lombok.extern.log4j.Log4j2;
import sql.EntityAktienImpl;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Log4j2
public class PortfolioController implements Initializable {

    @FXML
    private ListView<Aktie> aktienList;
    @FXML
    private ListView<Aktie> aktienListKunde;
    @FXML
    private TextField name;
    @FXML
    private TextField symbol;


    private PortfolioModel portfolioModel;
    private EntityAktienImpl entityAktien;

    public PortfolioController() {
        this.portfolioModel = new PortfolioModel();
        this.entityAktien = new EntityAktienImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        portfolioModel.setAktienList(entityAktien.getAll());

        aktienList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        aktienListKunde.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        aktienList.setItems(FXCollections.observableList(portfolioModel.getAktienList()));

        aktienListKunde.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                portfolioModel.setAktie(newValue);
                symbol.setText(portfolioModel.getAktie().getSymbol());
                name.setText(portfolioModel.getAktie().getName());
            } else {
                name.clear();
                symbol.clear();
            }

        });


        // Add mouse event handlers for the source
        aktienList.setOnDragDetected(event -> {
            log.info("Event on Source: drag detected");
            dragDetected(event, aktienList);
        });

        aktienList.setOnDragOver(event -> {
            log.info("Event on Source: drag over");
            dragOver(event, aktienList);
        });

        aktienList.setOnDragDropped(event -> {
            log.info("Event on Source: drag dropped");
            dragDropped(event, aktienList);
        });

        aktienList.setOnDragDone(event -> {
            log.info("Event on Source: drag done");
            dragDone(event, aktienList);
        });

        // Add mouse event handlers for the target
        aktienListKunde.setOnDragDetected(event -> {
            log.info("Event on Target: drag detected");
            dragDetected(event, aktienListKunde);
        });

        aktienListKunde.setOnDragOver(event -> {
            log.info("Event on Target: drag over");
            dragOver(event, aktienListKunde);
        });

        aktienListKunde.setOnDragDropped(event -> {
            log.info("Event on Target: drag dropped");
            dragDropped(event, aktienListKunde);
        });

        aktienListKunde.setOnDragDone(event -> {
            log.info("Event on Target: drag done");
            dragDone(event, aktienListKunde);
        });


    }

    private void dragDetected(MouseEvent event, ListView<Aktie> listView) {
        int selectedCount = listView.getSelectionModel().getSelectedIndices().size();

        if (selectedCount == 0) {
            event.consume();
            return;
        }

        Dragboard dragboard = listView.startDragAndDrop(TransferMode.COPY_OR_MOVE);
        ArrayList<Aktie> selectedItem = getSelectedAktien(listView);

        ClipboardContent content = new ClipboardContent();
        content.put(portfolioModel.getAKTIE_LIST(), selectedItem);

        dragboard.setContent(content);
        event.consume();
    }

    private void dragOver(DragEvent event, ListView<Aktie> listView) {
        Dragboard dragboard = event.getDragboard();

        if (event.getGestureSource() != listView && dragboard.hasContent(portfolioModel.getAKTIE_LIST())) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    private void dragDropped(DragEvent event, ListView<Aktie> listView) {
        boolean dragCompleted = false;
        Dragboard dragboard = event.getDragboard();

        if(dragboard.hasContent(portfolioModel.getAKTIE_LIST())) {
            ArrayList<Aktie> list = (ArrayList<Aktie>)dragboard.getContent(portfolioModel.getAKTIE_LIST());
            listView.getItems().addAll(list);
            dragCompleted = true;
        }

        event.setDropCompleted(dragCompleted);
        event.consume();
    }

    private void dragDone(DragEvent event, ListView<Aktie> listView) {
        TransferMode tm = event.getTransferMode();
        log.info("TRANSFER MODE: " + tm.toString());
        if (tm == TransferMode.COPY) {
            removeSelectedAktie(listView);
        }

        event.consume();
    }


    private ArrayList<Aktie> getSelectedAktien(ListView<Aktie> listView) {
        ArrayList<Aktie> list = new ArrayList(listView.getSelectionModel().getSelectedItems());
        return list;
    }

    private void removeSelectedAktie(ListView<Aktie> listView) {
        List<Aktie> selectedList = new ArrayList<>(listView.getSelectionModel().getSelectedItems());
        log.info("Items to delete: " + selectedList);
        listView.getSelectionModel().clearSelection();
        listView.getItems().removeAll(selectedList);
    }



}
