package edu.eci.arsw.threads;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HostBlackListThread extends Thread {
    private int firstServer;
    private int lastServer;
    private String ipAddress;
    private Collection<Integer> blackListOcurrences;
    HostBlacklistsDataSourceFacade skds;
    private int ocurrencesCount = 0;
    private int checkedListsCount = 0;
    private static Semaphore mutex = new Semaphore(1);

    public HostBlackListThread(int firstServer, int lastServer, String ipAddress, HostBlacklistsDataSourceFacade skds, Collection blackListOcurrences) {
        this.firstServer = firstServer;
        this.lastServer = lastServer;
        this.ipAddress = ipAddress;
        this.skds = skds;
        this.blackListOcurrences = blackListOcurrences;
    }

    public void run() {
        for (int i = firstServer; i <= lastServer; i++) {
            checkedListsCount++;
            if (skds.isInBlackListServer(i, this.ipAddress)) {
                blackListOcurrences.add(i);
                ocurrencesCount++;
            }
        }
    }

    public int getOcurrencesCount() {
        return ocurrencesCount;
    }

    public int getCheckedListsCount() {
        return checkedListsCount;
    }

    public Collection<Integer> getBlackListOcurrences() {
        return blackListOcurrences;
    }
}