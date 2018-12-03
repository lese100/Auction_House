package AuctionHouse;

import Utility.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.ConnectException;
import java.net.SocketException;
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
    private final long BID_TIMER = 10000;

    private IDRecord idRecord;
    private AuctionDisplay display;
    private BankProxy bankProxy;
    private List<AuctionItem> auctions;
    private Map<Integer, AgentProxy> connectedAgents;

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
            display.updateConsoleDisplay("Connected to Bank Host: " +
                    bankHostName + " Port: " + bankPort);
        }catch(ConnectException e){
            cs = null;
            display.updateConsoleDisplay("Connection to Bank failed. " +
                    "Fake account information supplied.");
        }

        bankProxy = new BankProxy(cs);
        idRecord = bankProxy.openAccount(idRecord);


        System.out.println("AH Created");
        System.out.println("NAME:\t" + name);
        System.out.println("HOST:\t" + hostName);
        System.out.println("PORT:\t" + port);
        System.out.println("BHOST:\t" + bankHostName);
        System.out.println("BPORT:\t" + bankPort);

        System.out.println("MY ACCOUNT NUM:" + idRecord.getNumericalID());
        System.out.println("\n\n");


        List<String> names = createAuctionNames();
        List<Bid> bids = createBids();
        createAuctionItems(names, bids);

        printAuctionItems();

        display.setupAHLabelInfo(name, idRecord.getNumericalID());
        updateDisplay();

        PublicAuctionProtocol auctionProtocol = new AuctionHouseProtocol(this);
        NotificationServer ns = new NotificationServer(port, auctionProtocol);
        Thread notificationServer = new Thread(ns);
        notificationServer.start();
    }

    public void joinAuctionHouse(IDRecord agentInfo) throws IOException{
        display.updateConsoleDisplay("Agent \"" + agentInfo.getName() + "\" " +
                "connected to this Auction House" +
                " — Host: " + agentInfo.getHostname() + " Port: " +
                agentInfo.getPortNumber());

        CommunicationService cs = new CommunicationService
                (agentInfo.getHostname(), agentInfo.getPortNumber());

        display.updateConsoleDisplay("Connected to Agent \"" +
                agentInfo.getName() + "\"'s Notification Server" +
                " — Host: " + agentInfo.getHostname() + " Port: " +
                agentInfo.getPortNumber());

        AgentProxy ap = new AgentProxy(cs);

        connectedAgents.put(agentInfo.getNumericalID(), ap);
    }

    public List<AuctionItem> getAuctions(){
        return auctions;
    }

    public IDRecord getIdRecord(){
        return idRecord;
    }

    public void updateDisplay(){
        display.updateAuctionItemDisplay(auctions);
        System.out.println("just called");
    }

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
                System.out.println("Made it here");
                return Message.MessageIdentifier.BID_REJECTED_NSF;
            }else{
                oldAuctionItem = createCopyAuctionItem(auctionItem);

                //Updates auction item's current bidder
                auctionItem.getBid().setSecretKey
                        (itemOfInterest.getBid().getSecretKey());
                //Updates auction item's current high bid
                auctionItem.getBid().setCurrentBid
                        (itemOfInterest.getBid().getProposedBid());
                //Updates auction item's new min bid
                auctionItem.getBid().setMinBid
                        (itemOfInterest.getBid().getProposedBid() + .01);
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

    private void updateAgentsAboutChanges(){
        AuctionHouseInventory ahi =
                new AuctionHouseInventory(idRecord.getNumericalID(), auctions);

        for(AgentProxy ap : connectedAgents.values()){
            ap.updateAuctions(ahi);
        }
    }

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

    private AuctionItem findMatchingAuctionItem(AuctionItem itemOfInterest){
        for(AuctionItem ai : auctions){
            if(ai.equals(itemOfInterest)){
                return ai;
            }
        }
        return null;
    }

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

    private List<Bid> createBids(){
        List<Bid> bids = new ArrayList<>();
        Random rand = new Random();

        for(int i = 0; i < NUM_AUCTION_ITEMS; i++){
            Bid bid = new Bid(round(rand.nextInt(999) + rand.nextDouble(), 2));
            bids.add(bid);
        }

        return bids;
    }

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
     * Rounds a decimal value to two decimal places.
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

    private void printAuctionItems(){
        for(AuctionItem ai : auctions){
            System.out.println("ITEM ID: " + ai.getItemID() +
                    "\t BID STATE: " + ai.getBid().getBidState() +
                    "\t MIN BID: $" + ai.getBid().getMinBid() +
                    "\t CURRENT BID: $" + ai.getBid().getCurrentBid() +
                    "\t ITEM NAME: " + ai.getItemName());
        }
    }
}
