package part1;

import io.swagger.client.model.LiftRide;
import java.util.Random;

public class SkierLiftRideEventGenerator {

  public static SkierLiftRideEvent generate() {
    Random rand = new Random();
    int skierID = rand.nextInt(100000) + 1;
    int resortID = rand.nextInt(10) + 1;
    int liftID = rand.nextInt(40) + 1;
    String seasonID = "2024";
    String dayID = "1";
    int time = rand.nextInt(360) + 1;

    // liftBody
    LiftRide body = new LiftRide();
    body.setTime(time);
    body.setLiftID(liftID);

    return new SkierLiftRideEvent(skierID, resortID, liftID, seasonID, dayID, time);
  }

}
