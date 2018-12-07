package Bank;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * The Main class within the Bank package, providing the construction of a
 * Bank object. Run this to set up a Bank and open it for business.
 * created: 11/29/18 by wdc
 * last modified: 12/06/18 by wdc
 * previously modified: 12/06/18 by wdc (coord w/GUI)
 * previously modified: 12/01/18 by wdc
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class Main extends Application {

    private static Bank theBank;

    public static void main(String[] args) {

        launch(args);

    }

    /**
     * The required start() method to extend the JavaFX Application class,
     * creating the initial GUI asking for Bank information and initializing
     * the JavaFX Stage for the 2nd GUI used by the Bank once in operation.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        BankDisplay theBankDisplay = new BankDisplay(primaryStage);

        Button btnCreateBank = new Button("Create Bank");

        btnCreateBank.setOnAction(event -> {
            if(theBankDisplay.infoFilledOut()){
                theBankDisplay.openStage02();

                createBank (
                      theBankDisplay.getBankName(),
                      theBankDisplay.getBankHostName(),
                      theBankDisplay.getBankPort(),
                      theBankDisplay );

            } else {
                theBankDisplay.displayErrorMessage(
                    "Please complete the requested information first."
                );
            }
        });

        // declare the 2nd stage here in the start() so we can set up
        // its closing handler and then hand it over to the BankDisplay class
        Stage stage02 = new Stage();
        stage02.setOnCloseRequest(event -> {
            try{
                if(theBank.safeToClose()){
                    stop();
                }else{
                    theBankDisplay.displayErrorMessage(
                        "SORRY: ALL BANK CLIENTS MUST CLOSE THEIR ACCOUNTS " +
                        "BEOFORE THE BANK CAN CLOSE DOWN.");
                    event.consume();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        });

        theBankDisplay.setupBankGUIComponents(btnCreateBank, stage02);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    private static void createBank(String bankName,
                                   String hostName,
                                   int portNumber,
                                   BankDisplay theBankDisplay) {
        theBank = new Bank( bankName, hostName, portNumber,
            theBankDisplay);

    }
}
