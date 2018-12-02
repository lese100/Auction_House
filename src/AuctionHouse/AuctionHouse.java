package AuctionHouse;

import Utility.*;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class AuctionHouse {

    private final int NUM_AUCTION_ITEMS = 15;

    private IDRecord idRecord;
    private CommunicationService cs = null;
    private AuctionDisplay display;
    private List<AuctionItem> auctions;
    private Map<Integer, AgentProxy> connectedAgents;

    public AuctionHouse(AuctionDisplay display, String name, String hostName,
                        int port, String bankHostName, int bankPort)
            throws IOException {

        connectedAgents = new HashMap<>();

        this.display = display;

        idRecord = new IDRecord(Utility.IDRecord.RecordType.AUCTION_HOUSE,
                name, 0.0, hostName, port);

        //cs = new CommunicationService(bankHostName, bankPort);
        BankProxy bankProxy = new BankProxy(cs);
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
        display.updateAuctionItemDisplay(auctions);

        PublicAuctionProtocol auctionProtocol = new AuctionHouseProtocol(this);
        NotificationServer ns = new NotificationServer(port, auctionProtocol);
        Thread notificationServer = new Thread(ns);
        notificationServer.start();
    }

    public void joinAuctionHouse(IDRecord agentInfo) throws IOException{
        CommunicationService cs = new CommunicationService
                (agentInfo.getHostname(), agentInfo.getPortNumber());

        AgentProxy ap = new AgentProxy(cs);

        connectedAgents.put(agentInfo.getNumericalID(), ap);
    }

    public List<AuctionItem> getAuctions(){
        return auctions;
    }

    public IDRecord getIdRecord(){
        return idRecord;
    }

    public Message.MessageIdentifier makeBid(AuctionItem itemOfInterest){

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
