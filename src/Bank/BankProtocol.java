package Bank;

import Utility.*;

import java.util.ArrayList;

/**
 * Message-handling protocol for messages received by a Bank from an Agent
 * or Auction House.
 * created: 11/28/18 by Warren D. Craft (wdc)
 * last modified: 12/06/18 by wdc
 * @author Liam Brady (lb)
 * @author Warren D Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class BankProtocol implements PublicAuctionProtocol {

    private Bank bank;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * The public constructor for a BankProtocol, which implements the
     * PublicAuctionProtocol interface and provides elaboration for the
     * required handleMessage() method.
     * @param bank Bank object.
     */
    public BankProtocol(Bank bank) {
        // hand the protocol a reference to the creator Bank
        this.bank = bank;
    }


    // ****************************** //
    //   Override Methods             //
    // ****************************** //

    /**
     * Provides the handleMessage() method required in implementing the
     * PublicAuctionProtocol, establishing the appropriate actions and reply
     * messages for messages coming in from Agent and Auction House clients.
     */
    @Override
    public Message handleMessage(Message msgReceived) {

        Message msgToSend = null;
        Object msgContent = msgReceived.getMessageContent();
        switch (msgReceived.getMessageIdentifier()) {

            // cases listed in alphabetical order by Message identifier

            case ADD_FUNDS:
                int theAcctNum;
                double amtToAdd;
                BankAccount updatedBankAccount;
                if ( msgContent instanceof IDRecord ) {
                    IDRecord idRecord = (IDRecord) msgContent;
                    theAcctNum = idRecord.getNumericalID();
                    amtToAdd = idRecord.getInitialBalance();
                    updatedBankAccount = bank.addFunds(theAcctNum, amtToAdd);
                    msgToSend = new Message<>
                        (Message.MessageIdentifier.ACKNOWLEDGED,
                         updatedBankAccount);
                } else {
                    // no valid IDRecord sent, so create a generic BankAccount
                    // to send back with an error identifier
                    updatedBankAccount = new BankAccount();
                    msgToSend = new Message<>(Message.MessageIdentifier.
                        REQUEST_FAILED,
                        updatedBankAccount);
                }
                break;

            case CHECK_FUNDS:
                if ( msgContent instanceof Bid) {
                    Bid theBid = (Bid) msgContent;
                    int theSecretKey = theBid.getSecretKey();
                    double proposedFreeze = theBid.getProposedBid();
                    boolean fundsFrozen =
                        bank.checkAndFreezeFunds(theSecretKey, proposedFreeze);
                    if (fundsFrozen) {
                        msgToSend = new Message<>
                            (Message.MessageIdentifier.CHECK_SUCCESS,
                                null);
                    } else {
                        msgToSend = new Message<>
                            (Message.MessageIdentifier.CHECK_FAILURE,
                                null);
                    }

                } else {
                    msgToSend = new Message<>
                        (Message.MessageIdentifier.CHECK_FAILURE,
                            null);
                }

                break;

            case CLOSE_REQUEST:
                if ( msgContent instanceof IDRecord ) {
                    IDRecord theIDRecord = (IDRecord) msgContent;
                    BankAccount theBankAccount =
                        bank.closeAccount(theIDRecord);
                    if ( theBankAccount.getAccountNumber() != -1 ) {
                        // request appears to have worked
                        msgToSend =
                            new Message<>(Message.MessageIdentifier.
                                CLOSE_ACCEPTED,
                                theBankAccount);
                    } else {
                        // request apparently rejected
                        msgToSend =
                            new Message<>(Message.MessageIdentifier.
                                CLOSE_REJECTED,
                                theBankAccount);
                    }
                } else {
                    // incorrect msgContent; reject request
                    msgToSend =
                        new Message<>(Message.MessageIdentifier.
                            CLOSE_REJECTED,
                            new BankAccount());
                }
                break;

            case GET_LIST_OF_AUCTION_HOUSES:
                ArrayList<IDRecord> theList = bank.getListOfAuctionHouses();
                msgToSend = new Message<>(Message.MessageIdentifier.
                    LIST_OF_AUCTION_HOUSES,
                    theList);
                break;

            case GET_SECRET_KEY:
                int aSecretKey;
                if (msgContent instanceof AccountLink) {
                    AccountLink theAccountLink = (AccountLink) msgContent;
                    aSecretKey = bank.createSecretKey(theAccountLink);
                } else {
                    // use -1 to indicate some error in the process
                    aSecretKey = -1;
                }

                msgToSend = new Message<>(Message.MessageIdentifier.
                    SECRET_KEY,
                    aSecretKey);
                break;

            case OPEN_AGENT_ACCT:
                if (msgContent instanceof IDRecord) {
                    // if msg content a valid IDRecord, prepare
                    // to reply with updated IDRecord
                    IDRecord updatedIDRecord =
                        bank.createAccount((IDRecord) msgContent);
                    msgToSend = new Message<>(Message.MessageIdentifier.
                        AGENT_ACCT_CONFIRMED,
                        updatedIDRecord);
                } else {
                    // if msg content not a valid IDRecord, prepare
                    // to simply send back original content
                    msgToSend = new Message<>(Message.MessageIdentifier.
                        ACCOUNT_DENIED,
                        msgContent);
                }
                break;

            case OPEN_AUCTIONHOUSE_ACCT:
                if (msgContent instanceof IDRecord) {
                    // if msg content a valid IDRecord, prepare
                    // to reply with updated IDRecord
                    IDRecord updatedIDRecord =
                        bank.createAccount((IDRecord) msgContent);
                    msgToSend = new Message<>(Message.MessageIdentifier.
                        AUCTIONHOUSE_ACCT_CONFIRMED,
                        updatedIDRecord);
                } else {
                    // if msg content not a valid IDRecord, prepare
                    // to simply send back original content
                    msgToSend = new Message<>(Message.MessageIdentifier.
                        ACCOUNT_DENIED,
                        msgContent);
                }
                break;

            case REQUEST_BALANCE:
                BankAccount theBankAccount;
                if ( msgContent instanceof IDRecord ) {
                    // if request supplied an IDRecord, get the BankAccount
                    theBankAccount =
                        bank.getBalance((IDRecord) msgContent);
                } else {
                    // otherwise, prepare a generic bank account object
                    theBankAccount = new BankAccount();
                }
                msgToSend =
                    new Message<>(Message.MessageIdentifier.BALANCE,
                                  theBankAccount);
                break;

            case TEST_MESSAGE:
                msgToSend = new Message<>(Message.MessageIdentifier.
                    ACKNOWLEDGED,
                    "Test Message Received");
                break;

            case TRANSFER_FUNDS:
                if ( msgContent instanceof AuctionItem) {
                    AuctionItem theAuctionItem =  (AuctionItem) msgContent;
                    Bid theBid = theAuctionItem.getBid();
                    // the Bid has the secret key and the current (winning) Bid
                    int theSecretKey = theBid.getSecretKey();
                    double amtToTransfer = theBid.getCurrentBid();
                    boolean fundsTransferred =
                        bank.transferFunds(theSecretKey, amtToTransfer);
                    if (fundsTransferred) {
                        // get agent's updated BankAccount
                        BankAccount agentBankAccount =
                            bank.getBankAccount(theSecretKey);
                        msgToSend = new Message<>
                            (Message.MessageIdentifier.TRANSFER_SUCCESS,
                                agentBankAccount);
                    } else {
                        msgToSend = new Message<>
                            (Message.MessageIdentifier.REQUEST_FAILED,
                                new BankAccount());
                    }

                } else {
                    msgToSend = new Message<>
                        (Message.MessageIdentifier.REQUEST_FAILED,
                            new BankAccount());
                }
                break;

            case UNFREEZE_FUNDS:
                if ( msgContent instanceof Bid) {
                    Bid theBid = (Bid) msgContent;
                    int theSecretKey = theBid.getSecretKey();
                    double amtToUnfreeze = theBid.getCurrentBid();
                    boolean fundsUnfrozen =
                        bank.unfreezeFunds(theSecretKey, amtToUnfreeze);
                    if (fundsUnfrozen) {
                        msgToSend = new Message<>
                            (Message.MessageIdentifier.REQUEST_SUCCEEDED,
                                null);
                    } else {
                        msgToSend = new Message<>
                            (Message.MessageIdentifier.REQUEST_FAILED,
                                null);
                    }


                } else {
                    msgToSend = new Message<>
                        (Message.MessageIdentifier.REQUEST_FAILED,
                            null);
                }
                break;

            default:
                msgToSend = new Message<>(Message.MessageIdentifier.
                    CASE_NOT_FOUND,
                    null);
                break;

        } // end switch()

        // return the message built in one of the switch() cases above
        return msgToSend;

    }

}
