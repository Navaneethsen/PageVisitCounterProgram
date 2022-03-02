import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The type Page visit counter read and write are safe.
 *
 * @author Navaneeth Sen
 * @version 1.0
 * @date 2022 /03/01
 */
public class PageVisitCounterReadWriteSafe implements PageVisitCounter
{
    // I am using a ConcurrentHashMap, because I think this is a highly concurrent module and
    // we are tending to have lots of key/value reads between multiple threads
    private final Map<String, Long> pageViewMap = new ConcurrentHashMap<>();

    // the locks to protect concurrent reads and concurrent writes in the map
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    /**
     * On page visit.
     *
     * @param page the page
     */
    public void onPageVisit(String page)
    {
        if (page == null)
        {
            // throw a meaningful exception
            return;
        }

        // critical section starts
        // acquire lock
        writeLock.lock();
        try
        {
            // increment the existing value
            pageViewMap.computeIfPresent(page, (key, val) -> val + 1);
            // if not present, add the default value as 1
            pageViewMap.putIfAbsent(page, 1L);
        }
        // critical section ends
        finally
        {
            // release lock
            writeLock.unlock();
        }
    }


    /**
     * Gets page visits.
     *
     * @param page the page
     * @return the page visits
     */
    public long getPageVisits(String page)
    {

        // use this to lock for prtecting the reads
        // multiple thread can acuire this lock at the same time as long as a write lock is not acquired
        readLock.lock();
        try
        {
            return pageViewMap.getOrDefault(page, 0L);
        }
        finally
        {
            // release the read lock
            readLock.unlock();
        }
    }
}
