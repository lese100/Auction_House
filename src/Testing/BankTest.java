package Testing;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Provides a vehicle for testing the Bank class by instantiating a
 * general bank client and associated GUI display. Not intended for
 * public use, just testing within the Bank package.
 * created: 11/30/18 by Warren D. Craft (wdc)
 * last modified: 12/06/18 by wdc
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class BankTest extends Application {

    private BankClientWithDisplay display;
    private BankClientWithDisplay theDisplay;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The start() method required when extending a JavaFX Application class
     * and used to establish the BankClientWithDisplay class for testing
     * the Bank class. Not intended for public use, just testing.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{

        theDisplay = new BankClientWithDisplay(primaryStage);

    }

}
