package part1;

import io.swagger.client.ApiException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import part2.CsvFileWriter;
import part2.ThroughputInfoDisplay;

public class MutiThreadsSkiersClient {

  private static final String csvFilePath = "request-performance.csv";

  public static void main(String[] args) throws ApiException, InterruptedException {
    // init
    int numOfTotalRequests = 200000;
    int numOfRequestsPerThread = 1000;
    int numOfTasks = numOfTotalRequests / numOfRequestsPerThread;
    int numOfMaxThread = 32;

    // counter for success and fail
    AtomicInteger successCnt = new AtomicInteger(0);
    AtomicInteger failCnt = new AtomicInteger(0);

    // create thread pool
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, numOfMaxThread, 60, TimeUnit.SECONDS,  new LinkedBlockingDeque<>(numOfTasks - numOfMaxThread));

    Executors.newCachedThreadPool();

    // csv file writer
    CsvFileWriter csvFileWriter = new CsvFileWriter(csvFilePath);

    // random data & submit task
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < numOfTasks; i++) {
      threadPoolExecutor.submit(new PostRequstTask(numOfRequestsPerThread, successCnt, failCnt, csvFileWriter));
    }

    // wait task to be completed, clean thread pool
    threadPoolExecutor.shutdown();
    threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    long duration = System.currentTimeMillis() - startTime;
    duration /= 1000;

    // print info (part one)
    System.out.println("============ Part One ============");
    System.out.println("success cnt: " + successCnt.get() + ", fail cnt: " + failCnt.get());
    System.out.println("total time elasped: " + duration + " seconds");
    System.out.println("total throughput: " + (numOfTotalRequests / duration));

    // print info (part two)
    ThroughputInfoDisplay throughputInfoDisplay = new ThroughputInfoDisplay();
    throughputInfoDisplay.showData(csvFilePath);
  }
}
