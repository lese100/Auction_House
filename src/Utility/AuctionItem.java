package Utility;

import java.io.Serializable;

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
 * last modified: 11/25/18 by thf
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class AuctionItem implements Serializable {

    private int houseID;
    private int itemID;
    private String itemName;
    private Bid bid;

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
}
