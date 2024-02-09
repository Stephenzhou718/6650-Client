package part2;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CsvFileWriter {

  private String[] headers;
  private Path pathToFile;

  public CsvFileWriter(String path) {
    this.headers = new String[]{"StartTime", "RequestType", "Latency", "ResponseCode"};

    // check file existence
    pathToFile = Paths.get(path);
    if (Files.exists(pathToFile)) {
      try {
        Files.delete(pathToFile);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public synchronized void writeRecords(List<RequestPerformanceRecord> records) {
    try {
      boolean needHeader = !Files.exists(pathToFile);
      BufferedWriter writer = Files.newBufferedWriter(pathToFile, needHeader ? StandardOpenOption.CREATE : StandardOpenOption.APPEND);
      CSVPrinter csvPrinter = new CSVPrinter(writer, needHeader ? CSVFormat.DEFAULT.withHeader(headers) : CSVFormat.DEFAULT);
      for (RequestPerformanceRecord record : records) {
        csvPrinter.printRecord(record.getStartTime(), record.getMethod().getValue(), record.getLatency(), record.getResponseCode());
      }
      csvPrinter.flush();
      System.out.println("Thread-" + Thread.currentThread().getId() + " finish flush!");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
