# PageViewCounter Program

## Task

* You have a single node system hosting a web site
* Every time a page is visited, the onPageVisit(page) method is called
* Your task is to count the number of visits for each page
* No need to persist data

## Specifications

Please choose to implement a pageview counter by:
* using any inbuilt java data structure
* allow multiple threads to read and write at the same time

Please use the below skeleton for the implementation of the logic

```
 public static class PageVisitCounter 
 {
      public void onPageVisit(String page) 
      {
          //implement
      }
      
      public long getPageVisits(String page)
      {
          //implement
      }
 }
```

## Developer Notes
I have tried to implement the PageViewCounter in two ways:
* One with both the Write and Read are protected using ReentrantReadWriteLock.
* Another one with the Write protected and non-protected read using ReentrantLock.

The code is extensively commented to give an idea on my thinking while developing this code.

To compile the project:
* checkout the project first using `git clone`
* run `mvn clean install` in the parent directory
* you will see the below lines which shows all the tests passed
```
-------------------------------------------------------
T E S T S
-------------------------------------------------------
Running PageVisitCounterWriteSafeReadUnsafeTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.779 sec
Running PageVisitCounterReadWriteSafeTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.657 sec

Results :

Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```

I have used Java 11 SDK to compile and run the program.


## Things to Improve
* ~~Optimize the code using lambdas available with the ConcurrentHashMap functions.~~

## License

This repository is released under the [MIT license](https://opensource.org/licenses/MIT). In short, this means you are free to use this software in any personal, open-source or commercial projects. Attribution is optional but appreciated.

