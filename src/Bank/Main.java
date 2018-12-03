package Bank;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * The Main class within the Bank package, providing the construction of a
 * Bank object. Run this to set up a Bank and open it for business.
 * created: 11/29/18 by wdc
 * last modified: 12/02/18 by wdc (coord w/GUI)
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

    @Override
    public void start(Stage primaryStage) throws Exception {

        Bank theBank;
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
                theBankDisplay.displayErrorMessage();
            }
        });

        theBankDisplay.setupBankInitializeButton(btnCreateBank);
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
