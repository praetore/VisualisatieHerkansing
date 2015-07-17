import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.DropdownList;
import org.gicentre.utils.stat.XYChart;
import processing.core.PApplet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by darryl on 16-7-15.
 */
public class HerkansingsOpdracht1 extends PApplet {
    private final String HUMIDITY = "Luchtvochtigheid";
    private final String TEMPERATURE = "Temperatuur";
    private final String SUNSHINE = "Zonneschijnduur";
    private final String AIRPRESSURE = "Luchtdruk";
    private final String WINDSPEED = "Windsnelheid";
    private final String[] CONSTANTS = {
            HUMIDITY, TEMPERATURE, SUNSHINE, AIRPRESSURE, WINDSPEED
    };
    private XYChart scatterplot;
    private Map<String, float[]> records;
    private ControlP5 p5;
    private DropdownList xAxisDropDown;
    private DropdownList yAxisDropDown;
    private String currentX;
    private String currentY;

    // Loads data into the chart and customises its appearance.
    @Override
    public void setup() {
        size(800, 380, P2D);
        textFont(createFont("Arial", 11), 11);

        loadData();
        setupScatterPlot();
        setupDropdownLists();
    }

    // Draws the scatterplot.
    @Override
    public void draw() {
        background(255);
        scatterplot.draw(20, 20, width - 40, height - 40);
    }

    private void updateScatterPlot() {
        if (currentX != null && currentY != null) {
            scatterplot.setData(records.get(currentX), records.get(currentY));
            scatterplot.setXAxisLabel("\n" + currentX);
            scatterplot.setYAxisLabel(currentY + "\n");
        }
    }

    private void setupScatterPlot() {
        // Both x and y data set here.
        scatterplot = new XYChart(this);

        // Axis formatting and labels.
        scatterplot.showXAxis(true);
        scatterplot.showYAxis(true);
        scatterplot.setXFormat("###,###");

        // Symbol styles
        scatterplot.setPointColour(color(180, 50, 50, 100));
        scatterplot.setPointSize(5);
        scatterplot.setData(new float[]{}, new float[]{});
        scatterplot.setXAxisLabel("");
        scatterplot.setYAxisLabel("");
    }

    public void loadData() {
        // Load in data from a file
        // (first line of file contains column headings).
        String[] data = loadStrings("weerdata-juni-2015.csv");
        float[] temperature = new float[data.length - 1];
        float[] humidity = new float[data.length - 1];
        float[] airpressure = new float[data.length - 1];
        float[] windspeed = new float[data.length - 1];
        float[] sunshine = new float[data.length - 1];

        for (int i = 0; i < data.length - 1; i++) {
            String[] tokens = data[i + 1].split(",");
            if (tokens.length == 41) {
                temperature[i] = (float) (Double.parseDouble(tokens[11]) * 0.1);
                humidity[i] = Float.parseFloat(tokens[35]);
                airpressure[i] = (float) (Double.parseDouble(tokens[25]) * 0.1);
                windspeed[i] = (float) (Double.parseDouble(tokens[4]) * 0.1);
                sunshine[i] = (float) (Double.parseDouble(tokens[18]) * 0.1);
            }
        }

        records = new HashMap<>();
        records.put(HUMIDITY, humidity);
        records.put(TEMPERATURE, temperature);
        records.put(SUNSHINE, sunshine);
        records.put(AIRPRESSURE, airpressure);
        records.put(WINDSPEED, windspeed);
    }

    private void setupDropdownLists() {
        p5 = new ControlP5(this);
        int dropDownHeight = 10;
        xAxisDropDown = p5.addDropdownList("x-Axis")
                .setPosition(60, dropDownHeight)
                .addItems(CONSTANTS);
        yAxisDropDown = p5.addDropdownList("y-Axis")
                .setPosition(170, dropDownHeight)
                .addItems(CONSTANTS);
    }

    public void controlEvent(ControlEvent event) {
        // DropdownList selects key to be loaded
        if (event.isController()) {
            int idx = (int) event.getController().getValue();
            if (event.getController().getAddress().equals("/y-Axis")) {
                currentY = CONSTANTS[idx];
            } else if (event.getController().getAddress().equals("/x-Axis")) {
                currentX = CONSTANTS[idx];
            }
            updateScatterPlot();
        }
    }
}
