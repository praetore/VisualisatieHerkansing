import controlP5.ControlEvent;

/**
 * Created by darryl on 17-7-15.
 */
public interface Opdracht {
    // Setup the chart and customises its appearance.
    public void setup();

    // Loads data into the chart
    public void loadData();

    // Adds listeners
    public void controlEvent(ControlEvent event);

    // Draws the scatterplot.
    public void draw();
}
