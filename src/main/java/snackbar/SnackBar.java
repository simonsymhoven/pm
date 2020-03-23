package snackbar;

import com.jfoenix.controls.JFXSnackbar;
import javafx.event.EventHandler;
import javafx.scene.layout.AnchorPane;



public class SnackBar {

    private AnchorPane pane;

    public SnackBar(AnchorPane pane) {
        this.pane = pane;
    }

    public void show(String text) {
        JFXSnackbar snackbar = new JFXSnackbar(pane);
        EventHandler handler = event -> snackbar.unregisterSnackbarContainer(pane);
        snackbar.getStylesheets().add(SnackBar.class.getResource("/fullpackstyling.css").toExternalForm());
        snackbar.show(text,  5000);
    }
}
