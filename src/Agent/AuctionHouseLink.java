package Agent;

import Utility.IDRecord;

public class AuctionHouseLink {
    private IDRecord id;
    private int secretKey;
    private AuctionHouseProxy proxy;
    public AuctionHouseLink(IDRecord id,int secretKey,AuctionHouseProxy proxy){
        this.id = id;
        this.secretKey = secretKey;
        this.proxy = proxy;
    }
    public IDRecord getId(){return id;}
    public int getSecretKey(){return secretKey;}
    public AuctionHouseProxy getProxy(){return proxy;}
}
