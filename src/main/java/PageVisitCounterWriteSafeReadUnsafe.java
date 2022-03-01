import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The type Page visit counter with all writes are thread safe and read might not give the actual values.
 *
 * @author Navaneeth Sen
 * @version 1.0
 * @date 2022 /03/01
 */
public class PageVisitCounterWriteSafeReadUnsafe
{
    // I am using a ConcurrentHashMap, because I think this is a highly concurrent module and
    // we are tending to have lots of key/value reads between multiple threads
    private final Map<String, Long> pageViewMap = new ConcurrentHashMap<>();

    // I am using a normal reentrant lock here
    // Also in this case we are not interested in the read values to be accurate
    // the reads eventually will have the correct values
    private final Lock reentrantLock = new ReentrantLock();

    /**
     * On page visit.
     *
     * @param page the page
     */
    public void onPageVisit(String page) {
        if(page == null)
        {
            // throw a meaningful exception
            return;
        }

        // critical
        reentrantLock.lock();
        try
        {
            // increment the existing value
            pageViewMap.computeIfPresent(page, (key, val) -> val + 1);
            // if not present, add the default value as 1
            pageViewMap.putIfAbsent(page, 1L);
        }
        finally
        {
            reentrantLock.unlock();
        }
    }

    /**
     * Gets page visits.
     *
     * @param page the page
     * @return the page visits
     */
    public long getPageVisits(String page) {

        // use this to lock for prtecting the reads
        // multiple thread can acuire this lock at the same time as long as a write lock is not acquired
        reentrantLock.lock();
        try
        {
            return pageViewMap.getOrDefault(page, 0L);
        }
        finally
        {
            // release the read lock
            reentrantLock.unlock();
        }
    }

    /**
     * Gets page visits unsafe.
     *
     * @param page the page
     * @return the page visits unsafe
     */
    public long getPageVisitsUnsafe(String page)
    {
        return pageViewMap.getOrDefault(page, 0L);
    }
}
