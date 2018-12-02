package AuctionHouse;

import Utility.AuctionItem;
import Utility.Bid;


public class BidTimer implements Runnable{

    private final long TIME;
    private AgentProxy ap;
    private AuctionItem ai;
    private boolean stillValid;

    public BidTimer(long time, AgentProxy ap, AuctionItem ai){
        this.TIME = time;
        this.ap = ap;
        this.ai = ai;
        stillValid = true;
    }


    @Override
    public void run() {
        try{
            Thread.sleep(TIME);
            if(stillValid){
                ap.notifyWinner(ai);
                ai.getBid().setBidState(Bid.BidState.SOLD);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }


    }

    public void cancelTimer(){
        stillValid = false;
    }


}
