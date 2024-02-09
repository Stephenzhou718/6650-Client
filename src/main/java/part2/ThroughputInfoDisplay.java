package part2;

import java.awt.Dimension;
import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFrame;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class ThroughputInfoDisplay {
  private TreeMap<Long, Integer> requestsPerSecond = new TreeMap<>();
  private List<Long> latencies = new ArrayList<>();

  public static void main(String[] args) {
    new ThroughputInfoDisplay().showData("request-performance.csv");
  }

  public void showData(String path) {
    // read data
    readData(path);

    // display metrics
    displayMetrics();

    // show plot
    showPlot();
  }

  private void displayMetrics() {
    // calculate data
    Collections.sort(latencies);
    long sum = 0;
    for (long latency : latencies) {
      sum += latency;
    }
    double mean = sum / (double) latencies.size();
    double median = latencies.size() % 2 == 0 ?
        (latencies.get(latencies.size() / 2) + latencies.get(latencies.size() / 2 - 1)) / 2.0 :
        latencies.get(latencies.size() / 2);
    double p99 = latencies.get((int) Math.ceil(latencies.size() * 0.99) - 1);
    long min = latencies.get(0);
    long max = latencies.get(latencies.size() - 1);
    long wallTimeInSeconds = requestsPerSecond.lastKey() - requestsPerSecond.firstKey();
    double throughput = latencies.size() / (double) wallTimeInSeconds;

    // show data
    System.out.println("============ Part Two ============");
    System.out.println("Mean Response Time: " + mean + " ms");
    System.out.println("Median Response Time: " + median + " ms");
    System.out.println("Throughput: " + throughput + " requests/second");
    System.out.println("99th Percentile Response Time: " + p99 + " ms");
    System.out.println("Min Response Time: " + min + " ms");
    System.out.println("Max Response Time: " + max + " ms");
  }

  private void showPlot() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    long startTime = requestsPerSecond.firstKey();
    for (Map.Entry<Long, Integer> entry : requestsPerSecond.entrySet()) {
      dataset.addValue(entry.getValue(), "Requests", String.valueOf(entry.getKey() - startTime));
    }

    // contruct chart
    JFreeChart chart = ChartFactory.createLineChart(
        "Throughput Over Time",
        "Time (seconds)",
        "Request per Second",
        dataset,
        PlotOrientation.VERTICAL,
        false,
        true,
        false
    );

    // config image size
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(800, 600));
    JFrame frame = new JFrame();
    frame.setContentPane(chartPanel);
    frame.setTitle("Throughput Analysis");
    frame.pack();
    frame.setVisible(false);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Optionally save the chart as an image
    try {
      ChartUtils.saveChartAsPNG(new File("ThroughputChart.png"), chart, 800, 600);
      frame.dispose();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void readData(String path) {
    try {
      Reader reader = Files.newBufferedReader(Paths.get(path));
      CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim());

      for (CSVRecord csvRecord : csvParser) {
        long startTimeInMillSecond = Long.parseLong(csvRecord.get("StartTime"));
        long startTimeInSecond = startTimeInMillSecond / 1000;
        long latency = Long.parseLong(csvRecord.get("Latency"));

        // record request count per second
        requestsPerSecond.put(startTimeInSecond, requestsPerSecond.getOrDefault(startTimeInSecond, 0) + 1);

        // record latency data
        latencies.add(latency);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}






