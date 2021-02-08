package edu.eci.arsw.threads;
import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HostBlackListThread extends Thread{
    private int firstServer;
    private int lastServer;
    private String ipAddress;
    private LinkedList<Integer> blackListOcurrences = new LinkedList<>();
    HostBlacklistsDataSourceFacade skds;
    private int ocurrencesCount=0;
    private int checkedListsCount=0;
    private static Semaphore mutex = new Semaphore(1);

    public HostBlackListThread (int firstServer,int lastServer,String ipAddress,HostBlacklistsDataSourceFacade skds){
        this.firstServer=firstServer;
        this.lastServer=lastServer;
        this.ipAddress=ipAddress;
        this.skds=skds;
    }
    public void run(){
        for(int i = firstServer; i<=lastServer; i++){
            try {
                checkedListsCount++;
                if (skds.isInBlackListServer(i,this.ipAddress)){
                    mutex.acquire();
                    blackListOcurrences.add(i);
                    ocurrencesCount++;
                    mutex.release();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(HostBlackListThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int getOcurrencesCount() {return ocurrencesCount;}
    public int getCheckedListsCount() {return checkedListsCount;}
    public LinkedList<Integer> getBlackListOcurrences() {return blackListOcurrences;}
}