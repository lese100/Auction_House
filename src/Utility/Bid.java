package Utility;

import java.io.Serializable;

/**
 * Provides a structure for managing and observing the status of a bid on
 * an AuctionItem. A bid keeps track of the minBid — the minimum bid that must
 * be made to out-bit the current bid, the currentBid — the most recent
 * successful bid, and the bidState — the state of the current bid.
 *
 * Possible states of a bid:
 * OPEN    - No bid has been made
 * BIDDING - An agent has placed a bid, but the auction is still open
 *           on the related item.
 * SOLD    - The bid is over and a winner for the item has been found.
 *
 * created: 11/25/18 by thf
 * last modified: 11/25/18 by thf
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class Bid implements Serializable {


    private enum BidState {OPEN, BIDDING, SOLD}
    private BidState bidState;
    //the minimum bid that must be made to out-bit the current bid.
    private double minBid;
    //the most recent successful bid amount.
    private double currentBid;

    /**
     * Constructs a new Bid, always setting the state to open. A new bid
     * must know it's starting price (minBid) upon construction. Starts
     * currentBid = 0 as no agent has bid yet.
     * @param minBid the starting price of this bid
     */
    public Bid (double minBid){
        this.minBid = minBid;
        bidState = BidState.OPEN;
        currentBid = 0;
    }

    /**
     * Returns the bidState enum value associated with this bid.
     * @return bidState
     */
    public BidState getBidState(){
        return bidState;
    }

    /**
     * Returns the minBid value associated with this bid.
     * @return minBid — the minimum bid that must be made to out-bit the
     *                   current bid.
     */
    public double getMinBid(){
        return minBid;
    }

    /**
     * Returns the currentBid value associated with this bid.
     * @return currentBid — The most recent successful bid.
     */
    public double getCurrentBid(){
        return currentBid;
    }

    /**
     * Sets the bidState. Used when the bidding status is changed by agent
     * actions.
     * @param bidState new bidState of this bid
     */
    public void setBidState(BidState bidState){
        this.bidState = bidState;
    }

    /**
     * Sets the minBid. Used when the item associated with this bid has been
     * bid on.
     * @param minBid new lowest amount an agent can bid to become the
     *               currentBid.
     */
    public void setMinBid(double minBid){
        this.minBid = minBid;
    }

    /**
     * Sets the currentBid. Used when the item associated with this bid has
     * been bid on.
     * @param currentBid new potential winning amount of the bid.
     */
    public void setCurrentBid(double currentBid){
        this.currentBid = currentBid;
    }


}
