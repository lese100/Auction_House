package User;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Display {

    private Label stringLabel;
    private Label switchDictCountLabel;
    private Button getString;
    private Button switchDictionaries;
    private BorderPane borderPane;

    public Display(Stage window){
        initDisplay(window);
    }

    private void initDisplay(Stage window){
        window.setMinWidth(400);
        window.setMinHeight(400);
        window.setMaxWidth(400);
        window.setMaxHeight(400);

        stringLabel = new Label("Empty");
        switchDictCountLabel = new Label("0");

        VBox vBox = new VBox();
        vBox.getChildren().addAll(stringLabel, switchDictCountLabel);

        borderPane = new BorderPane();

        borderPane.setCenter(vBox);

        window.setScene(new Scene(borderPane));
        window.show();

    }

    public void setStringLabel(String text){
        stringLabel.setText(text);
    }

    public void setSwitchDictCountLabel(String text){
        switchDictCountLabel.setText(text);
    }

    public void setupButtons(Button stringButton, Button switchButton){
        getString = stringButton;
        switchDictionaries = switchButton;
        borderPane.setTop(getString);
        borderPane.setBottom(switchDictionaries);
    }

    public void notifyUser(){
        Stage notificationStage = new Stage();
        notificationStage.setMaxHeight(100);
        notificationStage.setMinHeight(100);
        notificationStage.setMaxWidth(300);
        notificationStage.setMinWidth(300);

        Label notify = new Label("YOU CHANGED DICTIONARIES 10 SEC AGO!");

        StackPane sp = new StackPane();
        sp.getChildren().add(notify);

        Scene scene = new Scene(sp);
        notificationStage.setScene(scene);
        notificationStage.show();
    }
}
