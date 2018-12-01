package Bank;

import Utility.BankAccount;
import Utility.CommunicationService;
import Utility.IDRecord;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX-based display class for representing and displaying information
 * about a BankClient (which itself is used for testing a Bank object).
 * created: 11/30/18 by wdc
 * last modified: 12/01/18 by wdc
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class BankClientWithDisplay {

    private CommunicationService communicationService;
    private BankProxy bankProxy;
    private IDRecord clientIDRecord;
    private BankAccount myBankAccount;

    private BorderPane borderPane;

    private Label textLabelBank = new Label("BANK: My Bank");
    private TextField textFieldBankHost = new TextField("localhost");
    private TextField textFieldBankPort = new TextField("1234");
    private Label textLabelTotalBalance = new Label("unknown");
    private Label textLabelFrozen = new Label("unknown");
    private Label textLabelNonFrozen = new Label("unknown");
    private TextArea textAreaOutput = new TextArea("Output Area");

    private boolean connectedToBank = false;

    /**
     * Public constructor for the BankClientWithDisplay
     *
     * @param stage
     */
    public BankClientWithDisplay(Stage stage) {
        clientIDRecord = new IDRecord(IDRecord.RecordType.AGENT,
            "Test Bank Client", 1000.00, "localhost", 1111);
        initDisplay(stage);
    }

    private void initDisplay(Stage stage) {
        stage.setMinWidth(400);
        stage.setMinHeight(400);
        // stage.setMaxWidth(400);
        stage.setMaxHeight(400);

        // VBox to hold contents for left side
        VBox vBox = new VBox();
        // Contents for left side
        Label textLabelBankHost = new Label("host: ");
        HBox hBoxBankHost = new HBox(textLabelBankHost, textFieldBankHost);
        hBoxBankHost.setSpacing(10);
        hBoxBankHost.setAlignment(Pos.CENTER);
        Label textLabelBankPort = new Label("port: ");
        HBox hBoxBankPort = new HBox(textLabelBankPort, textFieldBankPort);
        hBoxBankPort.setSpacing(10);
        hBoxBankPort.setAlignment(Pos.CENTER);
        Label textLabelTotalBalanceTitle = new Label("Total Balance: ");
        HBox hBoxTotalBalance =
            new HBox(textLabelTotalBalanceTitle, textLabelTotalBalance);
        Label textLabelFrozenTitle = new Label("Frozen: ");
        HBox hBoxFrozen =
            new HBox(textLabelFrozenTitle, textLabelFrozen);
        Label textLabelNonFrozenTitle = new Label("Non-Frozen: ");
        HBox hBoxNonFrozen =
            new HBox(textLabelNonFrozenTitle, textLabelNonFrozen);
        vBox.getChildren().addAll(
            textLabelBank, hBoxBankHost, hBoxBankPort,
            hBoxTotalBalance, hBoxFrozen, hBoxNonFrozen);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(10);

        // HBox to hold contents for bottom panel
        HBox hBoxBottomButtons = new HBox();
        // Contents for bottom panel
        Button btnConnectToBank = new Button("Connect to Bank");
        setButtonHandlers(btnConnectToBank);
        Button btnSendTestMsg = new Button("Send Test Msg");
        setButtonHandlers(btnSendTestMsg);
        Button btnOpenAccount = new Button("Open Account");
        setButtonHandlers(btnOpenAccount);
        Button btnCheckBalance = new Button("Check Balance");
        setButtonHandlers(btnCheckBalance);
        Button btnAddFunds = new Button("Add Funds");
        setButtonHandlers(btnAddFunds);
        Button btnFreezeFunds = new Button("Freeze Funds");
        setButtonHandlers(btnFreezeFunds);
        Button btnUnFreezeFunds = new Button("Un-Freeze Funds");
        setButtonHandlers(btnUnFreezeFunds);
        hBoxBottomButtons.getChildren().addAll(
            btnConnectToBank, btnSendTestMsg, btnOpenAccount, btnCheckBalance,
            btnAddFunds, btnFreezeFunds, btnUnFreezeFunds
        );
        hBoxBottomButtons.setPadding(new Insets(10, 10, 10, 10));
        hBoxBottomButtons.setSpacing(10);
        // update for later: setDiabled(true) for all btns except Connect
        // then update those when connection made to Bank


        // borderPane to hold entire contents
        borderPane = new BorderPane();

        // put btns/controls in bottom
        borderPane.setBottom(hBoxBottomButtons);
        // put info displays in left
        borderPane.setLeft(vBox);
        // put text area in center for output
        borderPane.setCenter(textAreaOutput);

        stage.setScene(new Scene(borderPane));
        stage.show();

    }

    /**
     * Sets up the EventHandlers for various buttons, using a switch()
     * on the original button text string
     *
     * @param button
     */
    private void setButtonHandlers(Button button) {

        String buttonText = button.getText();

        switch (buttonText) {

            case "Connect to Bank":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Calling connectToBank()");
                        try {
                            connectToBank();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                break;

            case "Send Test Msg":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Sending Test Message ...");
                        sendTestMessage();
                    }
                });
                break;

            case "Open Account":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Opening an account ...");
                        openAccount();
                    }
                });
                break;

            case "Check Balance":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Checking Balance!");
                        checkBalance();
                    }
                });
                break;

            case "Add Funds":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Adding Funds");
                    }
                });
                break;

            case "Freeze Funds":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Freezing Funds");
                    }
                });
                break;

            case "Un-Freeze Funds":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Un-Freezing Funds");
                    }
                });
                break;

            default:
                break;

        } // end switch()

    } // end setButtonHandlers()

    private void connectToBank() throws Exception {

        if (connectedToBank) {
            System.out.println("Already connected to Bank!");
        } else {

            System.out.println(
                "BankClientWithDisplay: connectToBank(): connecting!"
            );
            String hostName = textFieldBankHost.getText();
            int portNumber = Integer.parseInt(textFieldBankPort.getText());
            try {
                communicationService =
                    new CommunicationService(hostName, portNumber);
                bankProxy = new BankProxy(communicationService);
                connectedToBank = true;
                System.out.println("Now connected to Bank at: " +
                    hostName + " (port " + portNumber + ")");
                // activate the remaining buttons

            } catch (Exception e) {
                System.out.println("Unable to connect to Bank!");
                connectedToBank = false;
            }


        }

    }

    private void sendTestMessage () {

        String msgReply;
        msgReply = bankProxy.sendTestMessage();
        System.out.println("The reply message was: " + msgReply);
        textAreaOutput.appendText("\nResult from sendTestMessage(): " +
            msgReply);
    }

    private void openAccount () {
        System.out.println("Attempting to open a Bank Account.");
        IDRecord returnedIDRecord;
        System.out.println("Initial Account ID is: " +
            clientIDRecord.getNumericalID());
        returnedIDRecord = bankProxy.openBankAccount(clientIDRecord, 1000.00);
        if ( returnedIDRecord instanceof IDRecord ) {
            int newAccountNumber = returnedIDRecord.getNumericalID();
            System.out.println("The reply message was an IDRecord with " +
                "Account # " + newAccountNumber);
            textAreaOutput.appendText("\nResult from openAccount(): " +
                "Account # " + newAccountNumber);
        } else {
            System.out.println("Returning object is not an IDRecord.");
        }
        clientIDRecord = returnedIDRecord;
        // update the displayed account information
        // augment display with label for account #

    }

    private void checkBalance() {
        System.out.println("Attempting to check Bank balance(s) " +
            "for account # " + clientIDRecord.getNumericalID() );
        myBankAccount = bankProxy.checkBalance(clientIDRecord);
        System.out.println("Bank balance is: $" +
                           myBankAccount.getTotalBalance() );
        String totalBalanceString =
            String.format("$%.2f", myBankAccount.getTotalBalance());
        String frozenBalanceString =
            String.format("$%.2f", myBankAccount.getTotalFrozen());
        String unFrozenBalanceString =
            String.format("$%.2f", myBankAccount.getTotalUnfrozen());
        textLabelTotalBalance.setText(totalBalanceString);
        textLabelFrozen.setText(frozenBalanceString);
        textLabelNonFrozen.setText(unFrozenBalanceString);
    }

    public void notifyUser() {
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
