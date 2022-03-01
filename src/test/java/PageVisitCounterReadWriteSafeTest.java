import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Page visit counter read write safe test.
 *
 * @author Navaneeth Sen
 * @version 1.0
 * @date 2022 /03/01
 */
public class PageVisitCounterReadWriteSafeTest
{

    /**
     * The Page name list.
     */
    String[] pageNameList;
    /**
     * The Number of writer threads.
     */
    int numberOfWriterThreads;
    /**
     * The Writers.
     */
    List<Thread> writers;
    /**
     * The Number of reader threads.
     */
    int numberOfReaderThreads;
    /**
     * The Readers.
     */
    List<Thread> readers;
    /**
     * The Multiplier.
     */
    final int MULTIPLIER = 1000;

    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception
    {
        // a list of dummy pages
        pageNameList = new String[] {"A", "B", "C", "D", "E", "F"};

        // the number of writer threads to have parallel write scenario
        // I am going to have 60000 threads here
        numberOfWriterThreads = pageNameList.length * MULTIPLIER;

        // a list to hold the writer thread references for later use
        writers = new ArrayList<>();

        // create the reader threads
        // each reader thread will call the getPageVisitsUnsafe as many times as its index on the pageNameList
        numberOfReaderThreads = MULTIPLIER;

        // a list to hold the reader thread references for later use
        readers = new ArrayList<>();
    }

    /**
     * On page visit single write thread for each page followed by read.
     */
    @Test
    public void onPageVisitSingleWriteThreadForEachPageFollowedByRead()
    {

        // create an instance of the PageVisitCounterWriteSafeReadUnsafe
        PageVisitCounterReadWriteSafe pageVisitCounterReadWriteSafe = new PageVisitCounterReadWriteSafe();

        // set the number of writeThreads to be equal to the number of pages we have
        // in our case, we have 6 pages, so we use a thread for each page's updates
        numberOfWriterThreads = pageNameList.length;

        // the number of threads to read the result from the pageViewCounter
        numberOfReaderThreads = 1;

        for (int writerIndex = 0; writerIndex < numberOfWriterThreads; writerIndex++)
        {
            // get the index for the list, so that we can call the onPageVisit for it
            int index = writerIndex % pageNameList.length;
            Thread writer = new Thread(() -> {
                // number of times to call will be == the index of the pageName in the pageNameList
                // For eg.
                // "A" will be called 1 time
                // "B" will be called 2 times
                // "C" will be called 3 times... etc.
                int count = index;
                while (count > -1)
                {
                    pageVisitCounterReadWriteSafe.onPageVisit(pageNameList[index]);

                    // sleep for 100 ms for activating the priority inversion between threads
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        System.out.println("The Thread was Interrupted!" + e);
                    }

                    // decrement the count
                    count = count - 1;
                }
            });

            // we can make this a daemon thread, if we fear this might take a very long time
            // and can block the main program from finishing
            // it also makes sure the threads are cleaned up when the main thread exits
            writer.setDaemon(true);
            // add the thread to the list
            writers.add(writer);
        }

        // Start all Writer Threads
        for (Thread writer : writers) {
            writer.start();
        }

        // Wait for all Writer Threads to finish
        // can also set the time limit based on the requirement
        for (Thread writer : writers) {
            try
            {
                writer.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        // the final read after all thread have finished writing
        // each page visit number should be equal to the index of the page
        // For eg.
        // number of writer Threads / page = 1
        // "A" at index 0 will be called 1 times
        // "B" at index 1 will be called 2 times
        // "C" at index 2 will be called 3 times... etc.
        for (int i = 0; i < pageNameList.length; i++)
        {
            Assert.assertEquals(pageVisitCounterReadWriteSafe.getPageVisits(pageNameList[i]), i+1);
        }
    }

    /**
     * On page visit multiple thread write concurrent with multiple thread read.
     */
    @Test
    public void onPageVisitMultipleThreadWriteConcurrentWithMultipleThreadRead()
    {
        // create an instance of the PageVisitCounterWriteSafeReadUnsafe
        PageVisitCounterReadWriteSafe pageVisitCounterReadWriteSafe = new PageVisitCounterReadWriteSafe();

        for (int writerIndex = 0; writerIndex < numberOfWriterThreads; writerIndex++)
        {
            // get the index for the list, so that we can call the onPageVisit for it
            int index = writerIndex % pageNameList.length;
            Thread writer = new Thread(() -> {
                // number of times to call will be == the index of the pageName in the pageNameList
                // For eg.
                // "A" will be called 1 time
                // "B" will be called 2 times
                // "C" will be called 3 times... etc.
                int count = index;
                while (count > -1)
                {
                    pageVisitCounterReadWriteSafe.onPageVisit(pageNameList[index]);

                    // sleep for 100 ms for activating the priority inversion between threads
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        System.out.println("The Thread was Interrupted!" + e);
                    }

                    // decrement the count
                    count = count - 1;
                }
            });

            // we can make this a daemon thread, if we fear this might take a very long time
            // and can block the main program from finishing
            // it also makes sure the threads are cleaned up when the main thread exits
            writer.setDaemon(true);
            // add the thread to the list
            writers.add(writer);
        }

        // Reader threads just crunching the writes
        for (int readerIndex = 0; readerIndex < numberOfReaderThreads; readerIndex++)
        {
            // get the index for the list, so that we can call the onPageVisit for it
            int index = readerIndex % pageNameList.length;
            Thread reader = new Thread(() -> {
                // number of times to call will be == the index of the pageName in the pageNameList

                int count = 0;
                while (count < 5)
                {
                    pageVisitCounterReadWriteSafe.getPageVisits(pageNameList[index]);
                    // sleep for 100 ms for activating the priority inversion between threads
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        System.out.println("The Thread was Interrupted!" + e);
                    }

                    // decrement the count
                    count = count + 1;
                }
            });
            // we can make this a daemon thread, if we fear this might take a very long time
            // and can block the main program from finishing
            // it also makes sure the threads are cleaned up when the main thread exits
            reader.setDaemon(true);
            // add the thread to the list
            readers.add(reader);
        }

        // Start all Writer Threads
        for (Thread writer : writers) {
            writer.start();
        }

        // Start all Reader Threads
        for (Thread reader : readers) {
            reader.start();
        }

        // Wait for all Writer Threads to finish
        // can also set the time limit based on the requirement
        for (Thread writer : writers) {
            try
            {
                writer.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        // Wait for all Reader Threads to finish
        // can also set the time limit based on the requirement
        for (Thread reader : readers) {
            try
            {
                reader.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        // the final read after all thread have finished reading and writing
        // each page visit number should be equal to the index of the page * number of writer threads

        // For eg.
        // number of writer Threads = 1000
        // "A" at index 0 will be called 1000 times
        // "B" at index 1 will be called 2000 times
        // "C" at index 2 will be called 3000 times... etc.
        for (int i = 0; i < pageNameList.length; i++)
        {
            Assert.assertEquals(pageVisitCounterReadWriteSafe.getPageVisits(pageNameList[i]), (i+1) * MULTIPLIER);
        }
    }
}