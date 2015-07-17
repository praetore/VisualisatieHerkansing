import controlP5.ControlEvent;
import org.gicentre.geomap.GeoMap;
import processing.core.PApplet;
import processing.core.PVector;

import java.awt.geom.Point2D;
import java.io.File;

/**
 * Created by darryl on 17-7-15.
 */
public class HerkansingOpdracht2 extends PApplet implements Opdracht{


    private GeoMap geoMap;
    private PVector vector;

    @Override
    public void setup() {
        size(800, 800);
        geoMap = new GeoMap(this);
        geoMap.readFile("nederland" + File.separator + "Gemeentegrenzen_2015_zonder_water");
        Point2D.Float latLongPoint = new Point2D.Float(Float.valueOf("4.444"), Float.valueOf("51.955"));
        Point2D.Float RDPoint = longLat2RD(latLongPoint);
        System.out.println(RDPoint);
        vector = geoMap.geoToScreen(RDPoint.x, RDPoint.y);
    }

    @Override
    public void draw() {
        fill(255);
        geoMap.draw();
        fill(color(201, 60, 60));
        ellipse(vector.x, vector.y, 20, 20);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void controlEvent(ControlEvent event) {

    }

    /**
     * http://www.gpsgek.nl/informatief/wgs84-rd-script.html
     * @param latLon (lon=x,lat=y)
     * @return rd
     */
    public static Point2D.Float longLat2RD(Point2D.Float latLon){
        float dF = (float) (0.36 * (latLon.getY() - 52.15517440));
        float dL = (float) (0.36 * (latLon.getX() - 5.38720621));

        float SomX= (float) ((190094.945 * dL) + (-11832.228 * dF * dL) +
                        (-144.221 * dF*dF * dL) + (-32.391 * dL*dL*dL) +
                        (-0.705 * dF) + (-2.340 * pow(dF,3) * dL) + (-0.608 * dF * pow(dL,3)) +
                        (-0.008 * dL*dL) + (0.148 * dF*dF * pow(dL,3)));
        float SomY = (float) ((309056.544 * dF) + (3638.893 * dL*dL) +
                        (73.077 * dF*dF ) + (-157.984 * dF * dL*dL) +
                        (59.788 * pow(dF,3) ) + (0.433 * dL) + (-6.439 * dF*dF * dL*dL) +
                        (-0.032 * dF * dL) + (0.092 * dL*dL*dL*dL) + (-0.054 * dF * pow(dL,4)));

        return new Point2D.Float(155000 + SomX, 463000 + SomY);
    }
}
