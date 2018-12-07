package AuctionHouse;

import Utility.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.BindException;
import java.net.ConnectException;
import java.util.*;

/**
 * Provides the structure and functionality of a simulated AuctionHouse
 * accessible to Agents. Creates, then keeps track of AuctionItems and their
 * associated Bid objects. An AuctionHouse connects with a Bank to establish
 * a BankAccount, where it stores it's funds from selling AuctionItems to
 * Agents. Connected Agents can bid on AuctionItems if they have sufficient
 * funds to do so.
 * created: 11/30/18 by thf
 * last modified: 12/02/18 by thf
 * previously modified: 12/02/18 by thf
 * @author Liam Brady
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class AuctionHouse {

    private final int NUM_AUCTION_ITEMS = 15;
    private final long BID_TIMER = 30000;

    private IDRecord idRecord;
    private AuctionDisplay display;
    private BankProxy bankProxy;
    private List<AuctionItem> auctions;
    private Map<Integer, AgentProxy> connectedAgents;

    private double amountOwed;
    private double bankBalance;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Constructor for the AuctionHouse. After the user has filled in
     * necessary setup information (Name, HostName, Port, BankHostName,
     * BankPort), an AuctionHouse is created and passed a reference of its
     * display.
     * @param display The AuctionHouse GUI class
     * @param name A string name to describe the AuctionHouse
     * @param hostName The hostname of the pc this auction house is running on
     * @param port The chosen port number for this auction house to run on
     * @param bankHostName The hostname of the pc the bank runs on
     * @param bankPort The port number the bank is running on
     * @throws IOException
     */
    public AuctionHouse(AuctionDisplay display, String name, String hostName,
                        int port, String bankHostName, int bankPort)
            throws IOException {

        connectedAgents = new HashMap<>();

        this.display = display;

        idRecord = new IDRecord(Utility.IDRecord.RecordType.AUCTION_HOUSE,
                name, 0.0, hostName, port);

        CommunicationService cs;
        try{
            cs = new CommunicationService(bankHostName, bankPort);
            display.updateConsoleDisplay("Connected to Bank's Notification " +
                    "Server. — Host: " +
                    bankHostName + " Port: " + bankPort);
        }catch(ConnectException e){
            cs = null;
            display.updateConsoleDisplay("Connection to Bank failed. " +
                    "Fake account information supplied.");
        }

        bankProxy = new BankProxy(cs);
        idRecord = bankProxy.openAccount(idRecord);

        List<String> names = createAuctionNames();
        List<Bid> bids = createBids();
        createAuctionItems(names, bids);

        display.setupAHLabelInfo(name, idRecord.getNumericalID());
        updateDisplay();

        PublicAuctionProtocol auctionProtocol = new AuctionHouseProtocol(this);

        try{
            NotificationServer ns = new NotificationServer(port,
                    auctionProtocol);
            Thread notificationServer = new Thread(ns);
            notificationServer.start();
        }catch(BindException e){
            System.out.println("Port already in use. Terminating session. " +
                    "Please relaunch.");
            bankProxy.closeAccount(idRecord);
            System.exit(3);
        }

    }

    // ****************************** //
    //   Private Methods              //
    // ****************************** //

    /**
     * Creates a new AuctionItem with the same values as the passed
     * AuctionItem. A copy of the contained bid is also made.
     * @param auctionItem to be copied
     * @return AuctionItem the copy of the passed auctionItem
     */
    private AuctionItem createCopyAuctionItem(AuctionItem auctionItem){
        Bid bid = auctionItem.getBid();

        Bid copyBid = new Bid(bid.getMinBid());
        copyBid.setCurrentBid(bid.getCurrentBid());
        copyBid.setBidState(bid.getBidState());
        copyBid.setProposedBid(bid.getProposedBid());
        copyBid.setSecretKey(bid.getSecretKey());

        AuctionItem copyAuctionItem = new AuctionItem(auctionItem.getHouseID(),
                auctionItem.getItemID(), auctionItem.getItemName(), copyBid);

        return copyAuctionItem;
    }

    /**
     * Does a linear search through the list of auctions, returning the
     * first AuctionItem that matches the itemOfInterest.
     * @param itemOfInterest AuctionItem of interest
     * @return Matching AuctionItem to the passed argument
     */
    private AuctionItem findMatchingAuctionItem(AuctionItem itemOfInterest){
        for(AuctionItem ai : auctions){
            if(ai.equals(itemOfInterest)){
                return ai;
            }
        }
        return null;
    }

    /**
     * Using an AuctionFileReader, randomly selects adjective/noun pairs
     * from resource text files to be concatenated and returned as a list
     * of String names for AuctionItems.
     * @return Randomly generated List<String> AuctionItem names
     */
    private List<String> createAuctionNames(){
        List<String> auctionNames = new ArrayList<>();

        AuctionFileReader adjectivesReader =
                new AuctionFileReader("adjectives.txt");
        AuctionFileReader nounsReader = new AuctionFileReader("nouns.txt");

        List<String> adjectives = new ArrayList<>();
        List<String> nouns = new ArrayList<>();

        Random rand = new Random();

        if(adjectivesReader.fileExists()){
            String nextLine = adjectivesReader.getNextLine();
            while(nextLine != null){
                adjectives.add(nextLine);
                nextLine = adjectivesReader.getNextLine();
            }
            adjectivesReader.closeFileReader();
        }

        if(nounsReader.fileExists()){
            String nextLine = nounsReader.getNextLine();
            while(nextLine != null){
                nouns.add(nextLine);
                nextLine = nounsReader.getNextLine();
            }
            nounsReader.closeFileReader();
        }

        if(!adjectives.isEmpty() && !nouns.isEmpty()){
            for(int i = 0; i < NUM_AUCTION_ITEMS; i++){
                auctionNames.add(adjectives.get(rand.nextInt(adjectives.size()))
                        + " " + nouns.get(rand.nextInt(nouns.size())));
            }
        }

        return auctionNames;
    }

    /**
     * Creates random bid objects by choosing a random double [0.00, 999.99]
     * inclusive to be used as the starting minBid.
     * @return List<Bid> to create AuctionItems.
     */
    private List<Bid> createBids(){
        List<Bid> bids = new ArrayList<>();
        Random rand = new Random();

        for(int i = 0; i < NUM_AUCTION_ITEMS; i++){
            Bid bid = new Bid(round(rand.nextInt(1000) + rand.nextDouble(), 2));
            bids.add(bid);
        }

        return bids;
    }

    /**
     * Using the randomly generated List of names and List of bids, creates
     * a list of AuctionItems to be used as inventory of this AuctionHouse.
     * @param names List of names of AuctionItems
     * @param bids List of bid objects to be used when creating the
     *             AuctionItems.
     */
    private void createAuctionItems(List<String> names, List<Bid> bids){
        auctions = new ArrayList<>();

        for(int i = 0; i < NUM_AUCTION_ITEMS; i++){
            AuctionItem ai = new AuctionItem(idRecord.getNumericalID(), i+1,
                    names.get(i), bids.get(i));
            auctions.add(ai);
        }
    }


    /**
     * Adapted from an answer found on StackOverflow:
     *
     * https://stackoverflow.com/questions/2808535
     * /round-a-double-to-2-decimal-places
     *
     * Rounds a decimal value to "places" decimal places.
     * @param value value to be rounded
     * @param places number of places to round to:
     *               Example - value = 2.3423 places = 2
     *                         result = 2.34
     * @return newly rounded double
     */
    private double round(double value, int places){
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Debugging print method. Prints all AuctionItems to the console.
     */
    private void printAuctionItems(){
        for(AuctionItem ai : auctions){
            System.out.println("ITEM ID: " + ai.getItemID() +
                    "\t BID STATE: " + ai.getBid().getBidState() +
                    "\t MIN BID: $" + ai.getBid().getMinBid() +
                    "\t CURRENT BID: $" + ai.getBid().getCurrentBid() +
                    "\t ITEM NAME: " + ai.getItemName());
        }
    }

    // ****************************** //
    //   Public Methods               //
    // ****************************** //

    /**
     * This method is called when an agent requests to join the AuctionHouse.
     * A CommunicationService is established with the agent to allow for
     * future notifications about bids, which is then passed to an AgentProxy,
     * whose reference will then be stored in a HashMap, with it's secretKey
     * (found in the IDRecord's numericalID field) as the key, and the
     * AgentProxy reference as the value.
     * @param agentInfo IDRecord of the agent requesting to join the
     *                  AuctionHouse
     * @throws IOException
     */
    public void joinAuctionHouse(IDRecord agentInfo) throws IOException{
        display.updateConsoleDisplay("Agent [" + agentInfo.getName() + "] " +
                "has connected." +
                " — Host: " + agentInfo.getHostname() + " Port: " +
                agentInfo.getPortNumber());

        CommunicationService cs = new CommunicationService
                (agentInfo.getHostname(), agentInfo.getPortNumber());

        display.updateConsoleDisplay("Connected to Agent [" +
                agentInfo.getName() + "]'s Notification Server." +
                " — Host: " + agentInfo.getHostname() + " Port: " +
                agentInfo.getPortNumber());

        AgentProxy ap = new AgentProxy(cs);

        connectedAgents.put(agentInfo.getNumericalID(), ap);
    }

    /**
     * Creates an AuctionHouseInventory object which contains this
     * AuctionHouse's account number, and a current up-to-date list
     * of AuctionItems. This object is then sent to each connected agent,
     * usually to let them know of any changes that have been made in the
     * AuctionHouse.
     */
    public void updateAgentsAboutChanges(){
        AuctionHouseInventory ahi =
                new AuctionHouseInventory(idRecord.getNumericalID(), auctions);

        for(AgentProxy ap : connectedAgents.values()){
            ap.updateAuctions(ahi);
        }
    }

    /**
     * Updates the AuctionHouse GUI with the current set of AuctionItems.
     */
    public void updateDisplay(){
        display.updateAuctionItemDisplay(auctions);
    }

    /**
     * This method is called anytime an agent requests to make a bid on an
     * AuctionItem in this AuctionHouse. The agent sends a copy of the
     * AuctionItem they are interested in bidding on, and include the details
     * of their bid in the attached Bid object (in the secretKey field — to
     * identify who is making the bid, and the proposedBid field — to say
     * how much they want to bid).
     *
     * Once a matching AuctionItem reference is found, a synchronized operation
     * on that item commences. Checking first if the agent's bid was high
     * enough (proposedBid >= minBid), then if the agent has sufficient funds
     * in their account (by checking with the bank), and finally starting a
     * BidTimer and notifying the agent of a successful bid in the case that
     * all other tests checked out.
     *
     * If an item is being outbid, the previous bidder will be notified that
     * they were outbid.
     *
     * If a successful bid is placed, all agents are notified of the changes.
     *
     * @param itemOfInterest copy of an AuctionItem that the agent wants to
     *                       place a bid on. Contains the proposedBid and
     *                       secretKey fields in the contained Bid object.
     * @return MessageIdentifier that needs to be sent back to the bidding
     *                           agent.
     * @throws IOException
     */
    public Message.MessageIdentifier makeBid
            (AuctionItem itemOfInterest) throws IOException{

        AuctionItem auctionItem = findMatchingAuctionItem(itemOfInterest);
        AuctionItem oldAuctionItem;


        synchronized(auctionItem){

            if(itemOfInterest.getBid().getProposedBid() <
                    auctionItem.getBid().getMinBid()){
                return Message.MessageIdentifier.BID_REJECTED_INADEQUATE;
            }

            if(!bankProxy.checkAgentFunds(itemOfInterest.getBid())){
                return Message.MessageIdentifier.BID_REJECTED_NSF;
            }else{
                oldAuctionItem = createCopyAuctionItem(auctionItem);

                //Updates auction item's current bidder
                auctionItem.getBid().setSecretKey
                        (itemOfInterest.getBid().getSecretKey());
                //Updates auction item's current high bid
                auctionItem.getBid().setCurrentBid
                        (round(itemOfInterest.getBid().getProposedBid(), 2));
                //Updates auction item's new min bid
                auctionItem.getBid().setMinBid
                        (round(itemOfInterest.getBid().
                                getProposedBid() + .01,  2));
                //Updates auction item's bid state
                auctionItem.getBid().setBidState(Bid.BidState.BIDDING);
            }
        }

        //updates the AH server gui
        updateDisplay();

        updateAgentsAboutChanges();

        auctionItem.startTimer(BID_TIMER,
                connectedAgents.get(auctionItem.getBid().getSecretKey()), this);

        //if true, there was a previous bidder, so they are notified about
        //being outbidded. The bank then unfreezes their funds.
        if(oldAuctionItem.getBid().getSecretKey() != 0){
            connectedAgents.get(oldAuctionItem.getBid().getSecretKey()).
                    notifyOutbidded(oldAuctionItem);

            bankProxy.unfreezeAgentFunds(oldAuctionItem.getBid());
        }

        return Message.MessageIdentifier.BID_ACCEPTED;
    }

    /**
     * Called whenever an agent requests to leave the AuctionHouse. If any
     * bids are currently in a BIDDING status by that agent, the request
     * will be denied. Otherwise, it'll be approved.
     * @param idRecord IDRecord of the agent requesting to leave, including
     *                 their secretKey in the numericalID field.
     * @return True if agent is permitted to leave, else false.
     */
    public boolean requestToLeaveAuctionHouse(IDRecord idRecord){

        for(AuctionItem ai : auctions){
            if(ai.getBid().getSecretKey() == idRecord.getNumericalID() &&
                    ai.getBid().getBidState() == Bid.BidState.BIDDING){
                return false;
            }
        }

        connectedAgents.remove(idRecord.getNumericalID());
        display.updateConsoleDisplay("Agent [" + idRecord.getName() +
                "] has disconnected.");

        return true;
    }

    /**
     * Checks if it's safe for the AuctionHouse to close, and leave the bank.
     * @return True if no agents are still connected to the AuctionHouse,
     *         else false.
     */
    public boolean safeToClose(){
        if(connectedAgents.isEmpty()){
            bankProxy.closeAccount(idRecord);
            return true;
        }
        return false;
    }

    /**
     * Adds to the current amount owed by any agents who have won an Auction.
     * The amountOwed is then presented on the display.
     * @param owed the amount to be added to amountOwed.
     */
    public void updateAmountOwed(double owed){
        amountOwed += owed;

        display.updateAmountOwed(amountOwed);
    }

    /**
     * Requests an updated balance by messaging the bank. If the balance
     * received is different that the previous balance (AuctionHouse balances
     * can only ever go up), then the difference between the newBalance and
     * oldBalance is removed from amountOwed. The current Bank Balance and
     * amount owed totals are then updated on the AuctionHouse GUI.
     */
    public void updateBankBalance(){
        double currentBalance = bankProxy.
                checkFunds(idRecord).getTotalBalance();
        double newFunds = currentBalance - bankBalance;

        bankBalance = currentBalance;

        display.updateBankBalance(currentBalance);

        updateAmountOwed(-newFunds);
    }

    // ****************************** //
    //   Getter(s) & Setter(s)        //
    // ****************************** //

    /**
     * Returns the list of AuctionItems in this auction.
     * @return List<AuctionItem>
     */
    public List<AuctionItem> getAuctions(){
        return auctions;
    }

    /**
     * Returns the current IDRecord associated with this AuctionHouse.
     * @return IDRecord
     */
    public IDRecord getIdRecord(){
        return idRecord;
    }

}
