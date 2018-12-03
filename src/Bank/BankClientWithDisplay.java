package Bank;

import Utility.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * JavaFX-based display class for representing and displaying information
 * about a BankClient (which itself is used for testing a Bank object).
 * created: 11/30/18 by wdc
 * last modified: 12/02/18 by wdc (adding fxnality to GUI)
 * previously modified: 12/01/18 by wdc
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class BankClientWithDisplay {

    private CommunicationService communicationService;
    private BankProxyForTesting bankProxyForTesting;
    private IDRecord clientIDRecord;
    private BankAccount myBankAccount;

    private BorderPane borderPane;

    // mutable components of the GUI display
    private Label textLabelBank = new Label("BANK: My Bank");
    private TextField textFieldBankHost = new TextField("localhost");
    private TextField textFieldBankPort = new TextField("1234");
    private TextField textFieldAccountNumber = new TextField("unknown");
    private ChoiceBox choiceBoxAccountType =
        new ChoiceBox(FXCollections.observableArrayList(
            "unknown", "AGENT", "AUCTION_HOUSE", "BANK", "OTHER"));
    private Label textLabelTotalBalance = new Label("unknown");
    private Label textLabelFrozen = new Label("unknown");
    private Label textLabelNonFrozen = new Label("unknown");
    private TextField textFieldAccountLinkAgent = new TextField("unknown");
    private TextField textFieldAccountLinkAH = new TextField("unknown");
    private TextField textFieldAccountLinkSecretKey = new TextField("unknown");
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
        stage.setMinHeight(500);
        // stage.setMaxWidth(400);
        stage.setMaxHeight(500);

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
        Label textLabelAccountNumberTitle = new Label("Account #: ");
        HBox hBoxAccountNumber =
            new HBox(textLabelAccountNumberTitle, textFieldAccountNumber);
        hBoxAccountNumber.setSpacing(10);
        hBoxAccountNumber.setAlignment(Pos.CENTER_LEFT);
        Label textLabelAccountType = new Label("Account Type: ");
        choiceBoxAccountType.setValue("unknown");
        HBox hBoxAccountType =
            new HBox(textLabelAccountType, choiceBoxAccountType);
        hBoxAccountType.setSpacing(10);
        hBoxAccountType.setAlignment(Pos.CENTER_LEFT);
        Label textLabelTotalBalanceTitle = new Label("Total Balance: ");
        HBox hBoxTotalBalance =
            new HBox(textLabelTotalBalanceTitle, textLabelTotalBalance);
        Label textLabelFrozenTitle = new Label("Frozen: ");
        HBox hBoxFrozen =
            new HBox(textLabelFrozenTitle, textLabelFrozen);
        Label textLabelNonFrozenTitle = new Label("Non-Frozen: ");
        HBox hBoxNonFrozen =
            new HBox(textLabelNonFrozenTitle, textLabelNonFrozen);
        Label textLabelAccountLink = new Label("For Account Links: ");
        Label textLabelAccountLinkAgent = new Label("Agent acct #: ");
        HBox hBoxAccountLinkAgent =
            new HBox(textLabelAccountLinkAgent, textFieldAccountLinkAgent);
        hBoxAccountLinkAgent.setSpacing(10);
        hBoxAccountLinkAgent.setAlignment(Pos.CENTER_LEFT);
        Label textLabelAccountLinkAH = new Label("AH acct #: ");
        HBox hBoxAccountLinkAH =
            new HBox(textLabelAccountLinkAH, textFieldAccountLinkAH);
        hBoxAccountLinkAH.setSpacing(10);
        hBoxAccountLinkAH.setAlignment(Pos.CENTER_LEFT);
        Label textLabelAccountLinkSecretKey = new Label("AL secret key: ");
        HBox hBoxAccountLinkSecretKey =
            new HBox(textLabelAccountLinkSecretKey,
                     textFieldAccountLinkSecretKey);
        hBoxAccountLinkSecretKey.setSpacing(10);
        hBoxAccountLinkSecretKey.setAlignment(Pos.CENTER_LEFT);


        vBox.getChildren().addAll(
            textLabelBank, hBoxBankHost, hBoxBankPort, hBoxAccountNumber,
            hBoxAccountType, hBoxTotalBalance, hBoxFrozen, hBoxNonFrozen,
            textLabelAccountLink, hBoxAccountLinkAgent, hBoxAccountLinkAH,
            hBoxAccountLinkSecretKey);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(10);

        // HBox to hold contents for bottom panel
        HBox hBoxBottomButtons = new HBox();
        // Contents for bottom panel
        Button btnConnectToBank = new Button("Connect to Bank");
        setButtonHandlers(btnConnectToBank);
        Button btnSendTestMsg = new Button("Send Test Msg");
        setButtonHandlers(btnSendTestMsg);
        Button btnOpenAgentAccount = new Button("Open Agent Account");
        setButtonHandlers(btnOpenAgentAccount);
        Button btnOpenAHAccount = new Button("Open AH Account");
        setButtonHandlers(btnOpenAHAccount);
        Button btnCheckBalance = new Button("Check Balance");
        setButtonHandlers(btnCheckBalance);
        Button btnAddFunds = new Button("Add $100");
        setButtonHandlers(btnAddFunds);
        Button btnFreezeFunds = new Button("Freeze $100");
        setButtonHandlers(btnFreezeFunds);
        Button btnUnFreezeFunds = new Button("Un-Freeze $100");
        setButtonHandlers(btnUnFreezeFunds);
        Button btnGetListOfAuctionHouses = new Button("Get AH List");
        setButtonHandlers(btnGetListOfAuctionHouses);
        Button btnGetSecretKey = new Button("Get Secret Key");
        setButtonHandlers(btnGetSecretKey);

        FlowPane flowPaneBottomButtons = new FlowPane();
        flowPaneBottomButtons.getChildren().addAll(
            btnConnectToBank, btnSendTestMsg, btnOpenAgentAccount,
            btnOpenAHAccount, btnCheckBalance,
            btnAddFunds, btnFreezeFunds, btnUnFreezeFunds,
            btnGetListOfAuctionHouses, btnGetSecretKey);
        flowPaneBottomButtons.setPadding(new Insets(10, 10, 10, 10));
        flowPaneBottomButtons.setHgap(10);
        flowPaneBottomButtons.setVgap(10);


        // borderPane to hold entire contents
        borderPane = new BorderPane();

        // put btns/controls in bottom
        // borderPane.setBottom(hBoxBottomButtons);
        borderPane.setBottom(flowPaneBottomButtons);
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

            case "Open Agent Account":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Opening an account ...");
                        openAccount(IDRecord.RecordType.AGENT);
                    }
                });
                break;

            case "Open AH Account":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Opening an account ...");
                        openAccount(IDRecord.RecordType.AUCTION_HOUSE);
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

            case "Add $100":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("BankClient.Case: Add $100");
                        addFunds();
                    }
                });
                break;

            case "Freeze $100":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Checking & Freezing Funds");
                        checkAndFreezeFunds();
                    }
                });
                break;

            case "Un-Freeze $100":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Un-Freezing Funds");
                        unfreezeFunds();
                    }
                });
                break;

            case "Get AH List":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Getting AH List!");
                        getListOfAuctionHouses();
                    }
                });
                break;

            case "Get Secret Key":
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Getting a Secret Key!");
                        getSecretKey();
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
                bankProxyForTesting = new BankProxyForTesting(communicationService);
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
        msgReply = bankProxyForTesting.sendTestMessage();
        System.out.println("The reply message was: " + msgReply);
        textAreaOutput.appendText("\nResult from sendTestMessage(): " +
            msgReply);
    }

    private void openAccount (IDRecord.RecordType recordType) {
        System.out.println("Attempting to open a Bank Account for: " +
            recordType.toString());
        // create a temporary simulated IDRecord for the process
        IDRecord tempIDRecord = new IDRecord(recordType, "unknown", 0.00,
            "unknown", 0);
        IDRecord returnedIDRecord;
        System.out.println("Initial Account ID is: " +
            tempIDRecord.getNumericalID());
        returnedIDRecord = bankProxyForTesting.openBankAccount(tempIDRecord, 1000.00);
        if ( returnedIDRecord instanceof IDRecord ) {
            int newAccountNumber = returnedIDRecord.getNumericalID();
            IDRecord.RecordType newAcctType = returnedIDRecord.getRecordType();
            System.out.println("The reply message was an IDRecord with " +
                "Account # " + newAccountNumber);
            textAreaOutput.appendText("\nResult from openAccount(): " +
                "\nAccount # " + newAccountNumber + " (" +
                     newAcctType.toString() + ")");
            textFieldAccountNumber.setText(
                String.format("%d", newAccountNumber));
            choiceBoxAccountType.setValue(newAcctType.toString());
//            switch(newAcctType) {
//                case AGENT:
//                    choiceBoxAccountType.setValue("agent");
//                    break;
//                case AUCTION_HOUSE:
//                    choiceBoxAccountType.setValue("auction house");
//                    break;
//                default:
//                    choiceBoxAccountType.setValue("unknown");
//                    break;
//            }
        } else {
            System.out.println("Returning object is not an IDRecord.");
        }
        clientIDRecord = returnedIDRecord;
        // update the displayed account information

        // augment display with label for account #

    }

    private void checkBalance() {
        // using information from GUI for acct # and acct type
        // and construct a temporary simulated IDRecord
        IDRecord tempIDRecord;
        int tempAcctNumber =
            Integer.parseInt(textFieldAccountNumber.getText());
        System.out.println("Attempting to check Bank balance(s) " +
            "for account # " + tempAcctNumber );
        String acctTypeString = choiceBoxAccountType.getValue().toString();
        // construct a temporary simulated IDRecord
        IDRecord.RecordType tempRecordType;
        switch(acctTypeString){
            case "agent":
                tempRecordType = IDRecord.RecordType.AGENT;
                break;
            case "auction house":
                tempRecordType = IDRecord.RecordType.AUCTION_HOUSE;
                break;
            default:
                tempRecordType = IDRecord.RecordType.AGENT;
                break;
        }
        tempIDRecord = new IDRecord(tempRecordType, "unknown", 0.00,
            "unknown", 0);
        tempIDRecord.setNumericalID(tempAcctNumber);
        System.out.println("BCWD: checkBalance(): tempAcctNumber = " +
            tempAcctNumber);
        myBankAccount = bankProxyForTesting.checkBalance(tempIDRecord);
        System.out.println("BCWD: checkBalance(): myBankAccount " +
            "has type: " + myBankAccount.getAccountType().toString());
        System.out.println("Bank balance is: $" +
                           myBankAccount.getTotalBalance() );

        // update GUI display
        String totalBalanceString =
            String.format("$%.2f", myBankAccount.getTotalBalance());
        String frozenBalanceString =
            String.format("$%.2f", myBankAccount.getTotalFrozen());
        String unFrozenBalanceString =
            String.format("$%.2f", myBankAccount.getTotalUnfrozen());
        textLabelTotalBalance.setText(totalBalanceString);
        textLabelFrozen.setText(frozenBalanceString);
        textLabelNonFrozen.setText(unFrozenBalanceString);
        System.out.println("Balance checked on acct type: " +
            myBankAccount.getAccountType().toString());
        choiceBoxAccountType.setValue(
            myBankAccount.getAccountType().toString()
        );
    }

    /**
     * Gets a list of Auction Houses currently having accounts with
     * the Bank, and updates client GUI with the information.
     */
    public void getListOfAuctionHouses () {
        ArrayList<IDRecord> listOfAuctionHouses =
            bankProxyForTesting.getListOfAuctionHouses();
        textAreaOutput.appendText(
            "\nActive Auction Houses: "
        );
        for (IDRecord idRecord : listOfAuctionHouses) {
            String tempAHName = idRecord.getName();
            int tempAHAcctNum = idRecord.getNumericalID();
            textAreaOutput.appendText("\n" + tempAHName +
                ": Acct # " + tempAHAcctNum);
        }
    }

    public void getSecretKey() {

        // use the agent and ah fields in display to generate a
        // AccountLink
        int agentAcct = Integer.parseInt(textFieldAccountLinkAgent.getText());
        int ahAcct = Integer.parseInt(textFieldAccountLinkAH.getText());
        AccountLink theAccountLink = new AccountLink(agentAcct, ahAcct);
        int aSecretKey = bankProxyForTesting.getSecretKey(theAccountLink);
        System.out.println("BCWD.getSecretKey(): The secret key is: " +
            aSecretKey);
        textFieldAccountLinkSecretKey.setText("" + aSecretKey);
    }

    public void checkAndFreezeFunds() {
        // use the current secret key field in display to generate
        // a simulated Bid object
        double minBid = 10.00;
        double currentBid = 9.99;
        int secretKey =
            Integer.parseInt(textFieldAccountLinkSecretKey.getText());
        double proposedBid = 100.00; // so we desire to freeze $100
        Bid theBid = new Bid(minBid);
        theBid.setBidState(Bid.BidState.BIDDING);
        theBid.setCurrentBid(currentBid);
        theBid.setSecretKey(secretKey);
        theBid.setProposedBid(proposedBid);

        boolean fundsFrozen = bankProxyForTesting.checkAndFreezeFunds(theBid);
        System.out.println("BCWD.checkAndFreezeFunds(): fundFrozen = " +
             fundsFrozen);

    }

    public void unfreezeFunds() {
        // use the current secret key field in display to generate
        // a simulated Bid object
        double minBid = 10.00;
        double currentBid = 100.00; // so we desire to unfreeze $100
        int secretKey =
            Integer.parseInt(textFieldAccountLinkSecretKey.getText());
        double proposedBid = 100.00;
        Bid theBid = new Bid(minBid);
        theBid.setBidState(Bid.BidState.BIDDING);
        theBid.setCurrentBid(currentBid);
        theBid.setSecretKey(secretKey);
        theBid.setProposedBid(proposedBid);

        boolean fundsUnfrozen = bankProxyForTesting.unfreezeFunds(theBid);
        System.out.println("BCWD.checkAndFreezeFunds(): fundsUnfrozen = " +
            fundsUnfrozen);

    }

    public void addFunds () {

        // using information from GUI for acct # and acct type
        // and construct a temporary simulated IDRecord
        IDRecord tempIDRecord;
        int tempAcctNumber =
            Integer.parseInt(textFieldAccountNumber.getText());
        System.out.println("Attempting to add funds " +
            "to account # " + tempAcctNumber );
        String acctTypeString = choiceBoxAccountType.getValue().toString();
        // construct a temporary simulated IDRecord
        IDRecord.RecordType tempRecordType;
        switch(acctTypeString){
            case "AGENT":
                tempRecordType = IDRecord.RecordType.AGENT;
                break;
            case "AUCTION_HOUSE":
                tempRecordType = IDRecord.RecordType.AUCTION_HOUSE;
                break;
            default:
                tempRecordType = IDRecord.RecordType.AGENT;
                break;
        }
        tempIDRecord = new IDRecord(tempRecordType, "unknown", 100.00,
            "unknown", 0);
        tempIDRecord.setNumericalID(tempAcctNumber);
        System.out.println(
            "BCWD.addFunds(): tempIDRecord has following details: ");
        System.out.println("tempAcctNumber = " + tempAcctNumber);
        System.out.println("additional funds requested: $" +
            tempIDRecord.getInitialBalance());

        // call bank/bankproxy
        myBankAccount = bankProxyForTesting.addFunds(tempIDRecord);

        System.out.println("BCWD.addFunds(): myBankAccount " +
            "has type: " + myBankAccount.getAccountType().toString());
        System.out.println("Updated Bank balance is: $" +
            myBankAccount.getTotalBalance() );

        // update GUI display
        String totalBalanceString =
            String.format("$%.2f", myBankAccount.getTotalBalance());
        String frozenBalanceString =
            String.format("$%.2f", myBankAccount.getTotalFrozen());
        String unFrozenBalanceString =
            String.format("$%.2f", myBankAccount.getTotalUnfrozen());
        textLabelTotalBalance.setText(totalBalanceString);
        textLabelFrozen.setText(frozenBalanceString);
        textLabelNonFrozen.setText(unFrozenBalanceString);
        choiceBoxAccountType.setValue(
            myBankAccount.getAccountType().toString()
        );
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
