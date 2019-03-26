package Utility;

import java.io.Serializable;

import AuctionHouse.AgentProxy;
import AuctionHouse.AuctionHouse;
import AuctionHouse.BidTimer;

/**
 * Provides a structure for managing and observing an Auction Item which
 * will be contained in an Auction House. Each AuctionItem holds its
 * own name (generated randomly by a concatenation of Strings chosen from
 * a text file of adjectives and a text file of object nouns), Auction
 * House ID, item ID (generated at the Auction House class level when the
 * auction house is being setup), and the Bid associated with this item —
 * see Bid class for more details.
 *
 * created: 11/25/18 by thf
 * last modified: 12/02/18 by thf (adding transient modifier)
 * previously modified: 11/25/18 by thf
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class AuctionItem implements Serializable {

    private int houseID;
    private int itemID;
    private String itemName;
    private Bid bid;
    //transient variables are ignored during serialization. BidTimer is
    //irrelevant when passing an AuctionItem between Public Auction
    //components, and need not be serialized.
    private transient BidTimer bidTimer;
    private int timeLeftOnBid;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * AuctionItem constructor. Requires all fields be established upon
     * creation as all fields are immutable.
     * @param houseID identifier for the specific Auction House this item
     *                belongs to.
     * @param itemID identifier for this specific AuctionItem.
     * @param itemName name of the item — usually some concatenation of
     *                 some random Strings.
     * @param bid a bid object, used to keep track of this item's bid status,
     *            minBid, and currentBid.
     *
     */
    public AuctionItem(int houseID, int itemID, String itemName, Bid bid) {
        this.houseID = houseID;
        this.itemID = itemID;
        this.itemName = itemName;
        this.bid = bid;
        bidTimer = null;
    }

    // ****************************** //
    //   Getter(s) & Setter(s)        //
    // ****************************** //

    /**
     * Returns the houseID associated with this AuctionItem.
     * @return houseID of the AuctionHouse this item is associated with.
     */
    public int getHouseID() {
        return houseID;
    }

    /**
     * Returns the unique identifier for this AuctionItem.
     * @return itemID of this AuctionItem.
     */
    public int getItemID() {
        return itemID;
    }

    /**
     * Returns the (usually randomly generated) String name of this AuctionItem.
     * @return itemName, the String name associated with this AuctionItem.
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Returns the bid object for this AuctionItem, which provides all details
     * for the minBid, currentBid, and state of the bid (OPEN, BIDDING, SOLD).
     * @return bid object associated with this AuctionItem.
     */
    public Bid getBid() {
        return bid;
    }

    /**
     * Used for TableView JavaFX component to display bidState on GUI
     * @return current bidState
     */
    public Bid.BidState getBidsBidState(){
        return bid.getBidState();
    }

    /**
     * Used for TableView JavaFX component to display minBid on GUI
     * @return current minBid
     */
    public double getBidsMinBid(){
        return bid.getMinBid();
    }

    /**
     * Used for TableView JavaFX component to display currentBid on GUI
     * @return current currentBid
     */
    public double getBidsCurrentBid(){
        return bid.getCurrentBid();
    }

    /**
     * Allows you to replace the old bid with the new updated one, so
     * for when you are placing a bid or the Auction house is accepting
     * the new proposed bid.
     * @param newBid bid object that is replacing the old one
     */
    public void setBid(Bid newBid){bid = newBid;}

    /**
     * Sets the time left on current bid. If 0, the bid either hasn't
     * begun or is over.
     * @param timeLeftOnBid int amount of sec left on bid
     */
    public void setTimeLeftOnBid(int timeLeftOnBid){
        this.timeLeftOnBid = timeLeftOnBid;
    }

    /**
     * Used by AuctionGUI to display time left on bid.
     * @return int the amount of time left on the bid
     */
    public int getTimeLeftOnBid(){
        return timeLeftOnBid;
    }

    public void startTimer(long time, AgentProxy ap, AuctionHouse ah){
        if(bidTimer != null){
            bidTimer.cancelTimer();
        }

        bidTimer = new BidTimer(time, ap, this, ah);
        Thread threadTimer = new Thread(bidTimer);
        threadTimer.start();
    }

    /**
     * Provides one of two necessary functions for establishing a comparison
     * process for AuctionItems. See the .equals method override.
     * @return int
     */
    @Override
    public int hashCode(){
        int hash = 5;
        hash = 31 * hash + itemID;
        return hash;
    }

    /**
     * Provides a method for comparing two AuctionItems equality.
     * If two AuctionItems share a name, houseID, and itemID, they are
     * considered equal.
     * @param obj Object to compared to this AuctionItem
     * @return true or false
     */
    @Override
    public boolean equals(Object obj){

        //if the object is compared with itself
        if(obj == this){
            return true;
        }

        //check if obj is an instance of AuctionItem
        if(!(obj instanceof AuctionItem)){
            return false;
        }

        //for each AuctionItem, compare it's House ID, Item ID, and name. If
        //all match, we say the AuctionItem's are equal
        AuctionItem otherAI = (AuctionItem) obj;
        AuctionItem thisAI = this;

        if((otherAI.getItemName().equals(thisAI.getItemName())) &&
                (otherAI.getItemID() == thisAI.getItemID()) &&
                (otherAI.getHouseID() == thisAI.getHouseID())){
            return true;
        }

        return false;
    }
}
