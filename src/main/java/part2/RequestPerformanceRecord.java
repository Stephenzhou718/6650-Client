package part2;

import com.squareup.okhttp.internal.http.HttpMethod;
import java.net.Authenticator.RequestorType;

public class RequestPerformanceRecord {

  private long startTime;

  private RequestMethod method;

  /**
   * millisecs
   */
  private long latency;

  private int responseCode;

  public RequestPerformanceRecord(long startTime, RequestMethod method, long latency,
      int responseCode) {
    this.startTime = startTime;
    this.method = method;
    this.latency = latency;
    this.responseCode = responseCode;
  }

  public long getStartTime() {
    return startTime;
  }

  public RequestMethod getMethod() {
    return method;
  }

  public long getLatency() {
    return latency;
  }

  public int getResponseCode() {
    return responseCode;
  }
}
