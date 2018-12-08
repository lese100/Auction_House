package Agent;

import Utility.IDRecord;

/**
 * Stores all the important information the agent will need to know about the
 * Auction houses it connects to.
 * created: 11/30/18 by lb
 * last modified: 12/07/18 by lb
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class AuctionHouseLink {
    private IDRecord id;
    private int secretKey;
    private AuctionHouseProxy proxy;

    /**
     * Creates a new AuctionHouseLink object with the important information
     * about the auction house.
     * @param id auction houses IDRecord
     * @param secretKey The secret Key the bank provided for interacting with
     *                  the auction house
     * @param proxy the AuctionHouseProxy used to communicate/ send messages to
     *              the Auction House.
     */
    public AuctionHouseLink(IDRecord id,int secretKey,AuctionHouseProxy proxy){
        this.id = id;
        this.secretKey = secretKey;
        this.proxy = proxy;
    }

    /**
     * gets the auction houses IDRecord
     * @return auction houses IDRecord
     */
    public IDRecord getId(){return id;}

    /**
     * gets the secret key associated with the auction house
     * @return secret key
     */
    public int getSecretKey(){return secretKey;}

    /**
     * gets the AuctionHouseProxy used the communicate with the auction house
     * @return The Auction Houses Proxy
     */
    public AuctionHouseProxy getProxy(){return proxy;}
}
