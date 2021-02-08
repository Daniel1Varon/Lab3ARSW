/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import edu.eci.arsw.threads.HostBlackListThread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;

    private ArrayList<HostBlackListThread> hilos=new ArrayList<HostBlackListThread>();
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress,int n){

        LinkedList<Integer> blackListOcurrences=new LinkedList<>();

        int ocurrencesCount=0;

        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();

        int checkedListsCount=0;

        int inicio=0;
        int cota=(int) Math.ceil(skds.getRegisteredServersCount()/n)-1;
        int fin=cota;

        for (int i=0;i<n;i++){
            HostBlackListThread hilo1=new HostBlackListThread(inicio,fin,ipaddress,skds);
            hilos.add(hilo1);
            inicio=fin+1;
            if(fin<skds.getRegisteredServersCount()-cota && skds.getRegisteredServersCount()-cota<fin+cota) fin=skds.getRegisteredServersCount();
            else if(fin+cota<skds.getRegisteredServersCount()) fin=fin+cota;
            else fin=skds.getRegisteredServersCount();

        }
        for(HostBlackListThread e:hilos) {e.start();}
        for(HostBlackListThread e:hilos) {
            try {
                e.join();
            } catch (InterruptedException e1) {
                System.out.print("Error");
            }
        }
        for(HostBlackListThread e:hilos) {
            ocurrencesCount+=e.getOcurrencesCount();
            checkedListsCount+=e.getCheckedListsCount();
            blackListOcurrences.addAll(e.getBlackListOcurrences());
        }


        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount-1, skds.getRegisteredServersCount()});


        return blackListOcurrences;
    }


    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());



}
