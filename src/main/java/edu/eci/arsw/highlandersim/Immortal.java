package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Immortal extends Thread {

    private Semaphore mutex = new Semaphore(1);

    private ImmortalUpdateReportCallback updateCallback = null;

    private AtomicInteger health;

    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean alive = true;

    private boolean pausa = false;

    private Lock aLock = new ReentrantLock();

    private Lock bLock = new ReentrantLock();

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback = ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = new AtomicInteger(health);
        this.defaultDamageValue = defaultDamageValue;
    }

    public synchronized void pausa() throws InterruptedException {
        this.pausa = true;

    }

    public synchronized void reanudar() {
        this.pausa = false;
        synchronized (this) {
            notifyAll();

        }
    }

    public void run() {

        while (alive) {
            synchronized (this) {
                if (pausa) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            Immortal im;

            aLock.lock();
            if (immortalsPopulation.size() == 1) break;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);
            aLock.unlock();

            try {
                this.fight(im);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void fight(Immortal i2) throws InterruptedException {

        if (i2.getHealth() > 0) {
            this.changeHealth(defaultDamageValue);
            i2.changeHealth(-defaultDamageValue);
            updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
        } else {
            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
        }

    }

    public synchronized void changeHealth(int v) {
        health.addAndGet(v);
        if (health.get() <= 0) {
            dead();
        }
    }

    public synchronized int getHealth() {
        return health.get();
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

    public void dead() {
        this.alive = false;
        synchronized (immortalsPopulation) {
            immortalsPopulation.remove(this);
        }
    }

    public void stopp(){
        this.alive = false;
    }

}
