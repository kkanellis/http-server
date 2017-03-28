package ce325.hw2.service;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
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

    private final static int MAX_MEAN_SLOTS = 3;

    private long mStarted;
    private AtomicLong mConnections;
    private AtomicInteger mMeanTime;
    private AtomicIntegerArray mTimeArray;

    private StatisticsService() {
        mConnections = new AtomicLong(0);
        mMeanTime = new AtomicInteger(0);
        mTimeArray = new AtomicIntegerArray(MAX_MEAN_SLOTS);
    }

    public void start() {
        mStarted = System.currentTimeMillis();
    }

    public int getTimeDelta() {
        return (int)(mStarted - System.currentTimeMillis());
    }

    /**
     * Called when a thread starts serving an exchange
     * @param threadId the thread id, negative integer
     * @return the time slot if one is free, or -1 if not
     */
    public int onConnect(int threadId){
        if (threadId >= 0) {
            throw new IllegalArgumentException("threadId must be negative!");
        }
        // increment and get total connections
        long connections = mConnections.incrementAndGet();

        // Calculate the sum, and try to zero out the slots
        if ((connections % MAX_MEAN_SLOTS) == 0) {
            int sum = 0;
            int nonZero = 0;
            for (int i = 0; i < mTimeArray.length(); i++) {
                int time = mTimeArray.get(i);
                if (time > 0) {
                    // Zero the time, only if it wasn't modified
                    mTimeArray.compareAndSet(i, time, 0);
                    sum += time;
                    nonZero++;
                }
            }
            // calculate the mean time
            if (nonZero > 0) {
                mMeanTime.set(sum / nonZero);
            }
        }


        // try to get a slot for mean request time calculation
        for (int i=0; i < mTimeArray.length(); i++) {
            if (mTimeArray.compareAndSet(i, 0, threadId)) {
                // we managed to get a slot
                return i;
            }
        }
        // All slots are full
        // TODO: log this
        System.err.println("All time slots are full :(");
        return -1;
    }

    /**
     * Set the request time for this slot
     * @param slot the time slot for this exchange
     * @param time the time this exchange took
     */
    public void onDisconnect(int slot, int time) {
        mTimeArray.set(slot, time);
    }

    /**
     * Get the total connections
     * @return total connections
     */
    public long getConnections() {
        return mConnections.get();
    }

    /**
     * Get the mean request serve time
     * @return the mean time
     */
    public int getMeanTime() {
        return mMeanTime.get();
    }

}
