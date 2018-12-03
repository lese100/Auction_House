package Bank;

import User.Display;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Provides a vehicle for testing the Bank class by instantiating a
 * general bank client and associated GUI display.
 * created: 11/30/18 by Warren D. Craft (wdc)
 * last modified: 12/02/18 by wdc
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class BankTest extends Application {

    private Display display;
    private BankClientWithDisplay theDisplay;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        theDisplay = new BankClientWithDisplay(primaryStage);

    }

    public void notifyUser(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                display.notifyUser();
            }
        });
    }





}
