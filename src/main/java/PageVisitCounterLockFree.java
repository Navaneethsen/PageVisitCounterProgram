import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * The type Page visit counter lock free.
 *
 * @author Navaneeth Sen
 * @version 1.0
 * @date 2022 /03/01
 */
public class PageVisitCounterLockFree
{
    // I am using a ConcurrentHashMap, because I think this is a highly concurrent module and
    // we are tending to have lots of key/value reads between multiple threads
    // here I have also decided to use a LongAdder as this specific scenario where
    // multiple threads update a common sum.
    // this implementation doesn't require any explicit locks
    private final Map<String, LongAdder> pageViewMap = new ConcurrentHashMap<>();

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

        // if not present, add the default value as 1
        // if key is present, just increment the value
        pageViewMap.computeIfAbsent(page, k -> new LongAdder()).increment();

    }

    /**
     * Gets page visits.
     *
     * @param page the page
     * @return the page visits
     */
    public long getPageVisits(String page)
    {
        // return the long value if not null else return 0
        return pageViewMap.get(page) == null ? 0 : pageViewMap.get(page).longValue();
    }
}
