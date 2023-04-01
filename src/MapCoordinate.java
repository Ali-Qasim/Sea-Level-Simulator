package Assignment;

public class MapCoordinate implements Comparable<MapCoordinate> {

    public final double LONGITUDE;
    public final double LATITUDE;
    public final double ALTITUDE;
    public final static double R = 6371;


    public MapCoordinate(double longitude, double latitude, double altitude){
        LONGITUDE = longitude; LATITUDE = latitude; ALTITUDE = altitude;
    }

    public double haversineDist(MapCoordinate mc){
        double lat1 = Math.toRadians(LATITUDE);
        double lat2 = Math.toRadians(mc.LATITUDE);
        double long1 = Math.toRadians(LONGITUDE);
        double long2 = Math.toRadians(mc.LONGITUDE);

        double a = Math.pow((Math.sin((lat2 - lat1) / 2)), 2) + (Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin((long2 - long1) / 2) , 2));

        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt((1-a))); // haversine distance
    }

    @Override
    public int compareTo(MapCoordinate m) {
        if(Double.compare(ALTITUDE, m.ALTITUDE) == 0){
            if(Double.compare(LATITUDE, m.LATITUDE) == 0){
                if(Double.compare(LONGITUDE, m.LONGITUDE) == 0){
                    return 0;
                } else return Double.compare(LONGITUDE, m.LONGITUDE);
            } else return Double.compare(LATITUDE, m.LATITUDE);
        } else return Double.compare(ALTITUDE, m.ALTITUDE);
    }

    @Override
    public boolean equals(Object o) {
        return this.compareTo((MapCoordinate) o) == 0;
    }

    @Override
    public String toString() {
        return this.LONGITUDE + ", " + this.LATITUDE + ", " + this.ALTITUDE;
    }
}