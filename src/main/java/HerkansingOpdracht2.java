import com.sun.xml.internal.ws.util.StringUtils;
import org.gicentre.geomap.GeoMap;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Created by darryl on 17-7-15.
 */
public class HerkansingOpdracht2 extends PApplet {
    private final int POINTSIZE = 10;
    private GeoMap geoMap;
    private PFont font;
    private Map<Integer, Weerstation> weerstationMap;

    @Override
    public void setup() {
        size(800, 800);
        font = createFont("Arial Bold", 12);
        geoMap = new GeoMap(this);
        geoMap.readFile("nederland" + File.separator + "Gemeentegrenzen_2015_zonder_water");
        loadData();
    }

    @Override
    public void draw() {
        background(202, 226, 245);
        fill(255);
        geoMap.draw();
        drawStations();
        checkCollision();
    }

    public void loadData() {
        weerstationMap = new HashMap<>();
        String[] weerstations = loadStrings("stations.csv");
        for (int i = 0; i < weerstations.length - 1; i++) {
            String[] weerstation = weerstations[i + 1].split(",");
            int station_id = Integer.parseInt(weerstation[0]);
            Float longitude = Float.valueOf(weerstation[1]);
            Float latitude = Float.valueOf(weerstation[2]);
            String name = StringUtils.capitalize(weerstation[4].toLowerCase());
            Point2D.Float latLongPoint = new Point2D.Float(longitude, latitude);
            Point2D.Float RDPoint = longLat2RD(latLongPoint);
            PVector location = geoMap.geoToScreen(RDPoint.x, RDPoint.y);
            weerstationMap.put(station_id, new Weerstation(name, location));
        }

        String[] data = loadStrings("KNMI_20131231.csv");
        for (int i = 0; i < data.length - 1; i++) {
            String s = data[i];
            if (!s.startsWith("#")) {
                s = s.replace(" ", "");
                List<String> split = Arrays.asList(s.split(","));
                List<String> row = new ArrayList<>();
                for (String old : split) {
                    row.add(old);
                }
                while (row.size() < 7) {
                    row.add("0");
                }
                int station_id = Integer.parseInt(row.get(0));
                Weerstation weerstation = weerstationMap.get(station_id);
                weerstation.addAvgTemp((float) (Float.parseFloat(row.get(2)) * 0.1));
                weerstation.addMinTemp((float) (Float.parseFloat(row.get(3)) * 0.1));
                weerstation.addMaxTemp((float) (Float.parseFloat(row.get(4)) * 0.1));
                float time_rainfall = Float.parseFloat(row.get(5));
                float amount_rainfall = Float.parseFloat(row.get(6));
                if (amount_rainfall == -1) {
                    amount_rainfall = 0;
                }
                weerstation.addRainfall(time_rainfall * amount_rainfall);
            }
        }
    }

    private void drawStations() {
        for (Map.Entry<Integer, Weerstation> entry : weerstationMap.entrySet()) {
            Weerstation weerstation = entry.getValue();
            fill(color(201, 60, 60));
            PVector location = weerstation.getLocation();
            ellipse(location.x, location.y, POINTSIZE, POINTSIZE);
            fill(0);
            textFont(font);
            text(weerstation.getName(), location.x + 10, location.y + 5);
        }
    }

    private void checkCollision() {
        for (Weerstation weerstation : weerstationMap.values()) {
            PVector location = weerstation.getLocation();
            if (collide(location.x, location.y, POINTSIZE)) {
                System.out.println(weerstation);
                break;
            }
        }
    }

    private boolean collide(float x, float y, int diameter) {
        float disX = x - mouseX;
        float disY = y - mouseY;
        return sqrt(sq(disX) + sq(disY)) < diameter / 2;
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

    private class Weerstation {
        private String name;
        private PVector location;
        private List<Float> max_temps;
        private List<Float> min_temps;
        private List<Float> avg_temps;
        private List<Float> rainfall;

        public Weerstation(String name, PVector location) {
            this.name = name;
            this.location = location;
            max_temps = new ArrayList<>();
            min_temps = new ArrayList<>();
            avg_temps = new ArrayList<>();
            rainfall = new ArrayList<>();
        }

        public void addMinTemp(float temp) {
            min_temps.add(temp);
        }

        public void addMaxTemp(float temp) {
            max_temps.add(temp);
        }

        public void addRainfall(float temp) {
            rainfall.add(temp);
        }

        public void addAvgTemp(float temp) {
            avg_temps.add(temp);
        }

        public PVector getLocation() {
            return location;
        }

        public String getName() {
            return name;
        }

        public Float getMaxTemp() {
            Collections.sort(max_temps);
            Collections.reverse(max_temps);
            return max_temps.get(0);
        }

        public Float getMinTemp() {
            Collections.sort(min_temps);
            return min_temps.get(0);
        }

        public Float getAvgTemp() {
            float sum = 0;
            for (Float temp : avg_temps) {
                sum += temp;
            }
            return sum / avg_temps.size();
        }

        public float getRainFall() {
            float sum = 0;
            for (Float obs : rainfall) {
                sum += obs;
            }
            return sum / rainfall.size();
        }

        @Override
        public String toString() {
            return "Weerstation{" +
                    "name='" + name + '\'' +
                    ", location=" + location +
                    ", max_temp=" + getMaxTemp() +
                    ", min_temp=" + getMinTemp() +
                    ", avg_temp=" + getAvgTemp() +
                    ", rainfall=" + getRainFall() +
                    '}';
        }
    }
}
