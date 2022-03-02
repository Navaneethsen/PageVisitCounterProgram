/**
 * The interface Page visit counter.
 *
 * @author Navaneeth Sen
 * @version 1.0
 * @date 2022 /03/02
 */
public interface PageVisitCounter
{
    /**
     * On page visit.
     *
     * @param page the page
     */
    void onPageVisit(String page);

    /**
     * Gets page visits.
     *
     * @param page the page
     * @return the page visits
     */
    long getPageVisits(String page);
}
