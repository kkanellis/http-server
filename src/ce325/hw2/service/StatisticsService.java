package ce325.hw2.service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by georgetg on 25/3/2017.
 * Singleton class that does stats counting, using atomic variables
 */
public class StatisticsService {
    private static StatisticsService ourInstance = new StatisticsService();

    public static StatisticsService getInstance() {
        return ourInstance;
    }

    private long mStarted;
    private AtomicLong mConnections;
    private AtomicInteger mMeanCounter;
    private AtomicLong mServiceTime;


    private StatisticsService() {
        mServiceTime.set(0);
        mConnections.set(0);
        mMeanCounter.set(0);
    }

    public void start() {
        mStarted = System.currentTimeMillis();
    }

    public void addConnection() {
        mConnections.incrementAndGet();
    }

    public long getConnections() {
        return mConnections.get();
    }

    public void addServiceTime(int time) {
        mServiceTime.getAndAdd(time);
    }

}
