package part1;

import com.google.gson.Gson;
import com.squareup.okhttp.internal.http.HttpMethod;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SkiersApi;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import part2.CsvFileWriter;
import part2.RequestMethod;
import part2.RequestPerformanceRecord;

public class PostRequstTask implements Runnable {

  private final int numberOfRequests;

  private AtomicInteger successCnt;

  private AtomicInteger failCnt;

  private List<SkierLiftRideEvent> failedEvents;

  private List<RequestPerformanceRecord> performanceRecords;

  private CsvFileWriter writer;

  public PostRequstTask(int numberOfRequests, AtomicInteger successCnt, AtomicInteger failCnt, CsvFileWriter writer) {
    this.numberOfRequests = numberOfRequests;
    this.successCnt = successCnt;
    this.failCnt = failCnt;
    this.failedEvents = new ArrayList<>();
    this.performanceRecords = new ArrayList<>();
    this.writer = writer;
  }

  @Override
  public void run() {
    // create SkierApi object
    ApiClient apiClient = new ApiClient();
//    apiClient.setBasePath("http://ec2-34-218-250-156.us-west-2.compute.amazonaws.com:8080/Lab2_Servlet_war/");
    apiClient.setBasePath("http://server-alb-257123290.us-west-2.elb.amazonaws.com:8080/Lab2_Servlet_war/");
//    apiClient.setBasePath("http://localhost:8080/Lab2_Servlet_war_exploded/");
    SkiersApi skiersApi = new SkiersApi(apiClient);

    // send request
    SkierLiftRideEvent event = null;
    for (int i = 0; i < numberOfRequests; i++) {
      try {
        // generate data & send
        event = SkierLiftRideEventGenerator.generate();
        sendRequest(skiersApi, event);
      } catch (Exception ex) {
        failedEvents.add(event);
      }
    }

    // retry failed event
    for (SkierLiftRideEvent failedEvent : failedEvents) {
      int retiredCnt = 1;
      boolean success = false;
      while (!success && retiredCnt <= 5) {
        try {
          System.out.println("retry event: " + new Gson().toJson(failedEvent));
          sendRequest(skiersApi, failedEvent);
          success = true;
        } catch (Exception ex) {
          System.out.println("Retry send " + retiredCnt + " times");
          retiredCnt++;
        }
      }

      if (!success) {
        this.failCnt.getAndIncrement();
      }
    }

    // write record to file
    writer.writeRecords(performanceRecords);
  }

  private void sendRequest(SkiersApi skiersApi, SkierLiftRideEvent event) throws ApiException {
    // request
    long startTime = System.currentTimeMillis();
    skiersApi.writeNewLiftRide(event.getLiftRide(), event.getSkierID(), event.getSeasonID(),
        event.getDayID(), event.getSkierID());
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    // count
    successCnt.getAndIncrement();

    // record performance
    performanceRecords.add(new RequestPerformanceRecord(startTime, RequestMethod.POST, duration, 200));
  }
}
