package Status;

public class ThreadTimer implements Runnable {

    private Main.StatusController sc;
    private final int NUM_MIL_SECONDS = 10000;

    public ThreadTimer(Main.StatusController sc){
        this.sc = sc;
    }

    @Override
    public void run(){
        try{
            Thread.sleep(NUM_MIL_SECONDS);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        sc.sendNotification();

    }
}
