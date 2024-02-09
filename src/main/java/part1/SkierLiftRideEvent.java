package part1;

import io.swagger.client.model.LiftRide;

public class SkierLiftRideEvent {

  private Integer skierID;

  private Integer resortID;

  private Integer liftID;

  private String seasonID;

  private String dayID;

  private Integer time;

  public SkierLiftRideEvent(Integer skierID, Integer resortID, Integer liftID, String seasonID,
      String dayID, Integer time) {
    this.skierID = skierID;
    this.resortID = resortID;
    this.liftID = liftID;
    this.seasonID = seasonID;
    this.dayID = dayID;
    this.time = time;
  }

  public Integer getSkierID() {
    return skierID;
  }

  public Integer getResortID() {
    return resortID;
  }

  public Integer getLiftID() {
    return liftID;
  }

  public String getSeasonID() {
    return seasonID;
  }

  public String getDayID() {
    return dayID;
  }

  public Integer getTime() {
    return time;
  }

  public LiftRide getLiftRide() {
    LiftRide liftRide = new LiftRide();
    liftRide.setTime(this.time);
    liftRide.setLiftID(liftID);
    return liftRide;
  }
}
