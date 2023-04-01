package Assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
public class Earth extends JComponent implements MouseListener{

    public static void main(String[] args) {

        JFrame frame = new JFrame("Map of Earth");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Earth earth = new Earth();
        earth.readDataMap("src/Assignment/earth.xyz");

        try {
            earth.seaLevel = Integer.parseInt(args[0]); //allow custom sea level
        } catch (ArrayIndexOutOfBoundsException ignore) {}

        frame.add(earth);
        frame.setSize(Math.round(360 * 4), (int) Math.round(180 * 4.2));
        frame.setVisible(true);
    }

    public double[][] arrayOfEarth;
    private final Map < String, Double > mapOfEarth = new TreeMap < > (); //double array was not working as a key, so instead I concatenated the numbers as strings.

    private int lines;
    private int seaLevel = 0;

    private boolean readDataArrayCheck = false;

    private MapCoordinate coordinate;
    List < MapCoordinate > mapCoordinates = new ArrayList < > ();

    public void readDataArray(String fileName) {
        try {
            Scanner input = new Scanner(new File(fileName));

            lines = 2336041; //2336041 for earth or 14588101 for earthHD. Lines have been counted beforehand, since counting them every time is unnecessary

            arrayOfEarth = new double[lines][3];

            for (int i = 0; i < lines; i++) {

                String[] line = (input.nextLine().split("\t"));

                int n = 0;
                for (String value: line) {
                    arrayOfEarth[i][n] = Double.parseDouble(value);
                    n++;
                } //System.out.println(Arrays.toString(arrayOfEarth[i]));
            }
            readDataArrayCheck = true;
        } catch (FileNotFoundException lol) {

            lol.printStackTrace(); //if want to see errors

            Scanner tryAgain = new Scanner(System.in);
            System.out.println("File not found. Please try a different filename: ");

            readDataArray(tryAgain.next());
        }

    }

    private void readArrayChecker() {
        if (!readDataArrayCheck) {
            Scanner tryAgain = new Scanner(System.in);
            System.out.println("The file has not been read! Please enter a filename: ");

            readDataArray(tryAgain.next());
        }
    }

    public void percentageAbove(double altitude) {
        readArrayChecker();

        double nAbove = 0;
        double percentageAbove;
        for (int i = 0; i < lines; i++) {
            if (altitude > arrayOfEarth[i][2]) {
                nAbove++;
            }
        }
        percentageAbove = (lines - nAbove) * 100 / lines;

        System.out.println(String.format("%.4f", percentageAbove) + "% of the Earth lies above " + altitude + " metres");
    }

    public void userInp() {
        readArrayChecker();

        while (true) {
            System.out.println("\nPlease enter 1 for percentageAbove, 2 for getAltitude, or \"quit\" to end program:");

            Scanner input = new Scanner(System.in);
            String inp = input.next();

            if (inp.compareTo("quit") == 0) {
                System.out.println("Program terminated.");
                break;
            } else if (Double.parseDouble(inp) == 1) {
                while (true) {
                    System.out.println("\nPlease enter an altitude, or \"quit\" to end program:");

                    inp = input.next();

                    try {
                        if (inp.compareTo("quit") == 0) {
                            System.out.println("Program terminated.");
                            break;
                        } else {

                            percentageAbove(Double.parseDouble(inp));
                        }
                    } catch (NumberFormatException stopRightThere) {
                        System.out.println("Invalid altitude.");
                    }
                }
            } else if (Double.parseDouble(inp) == 2) {
                while (true) {
                    System.out.println("\nPlease enter a longitude (0 to 360) and latitude (-90 to 90),\nor \"quit\" to go back:");

                    inp = input.next();

                    try {
                        if (inp.compareTo("quit") == 0) {
                            System.out.println("Program terminated.");
                            break;

                        } else {
                            String[] coordinates = inp.split(",");
                            double longitude = Double.parseDouble(coordinates[0]);
                            double latitude = Double.parseDouble(coordinates[1]);

                            System.out.println(getAltitude(longitude, latitude));
                        }
                    } catch (NumberFormatException stopRightThere) {
                        System.out.println("Invalid coordinates.");
                    } catch (ArrayIndexOutOfBoundsException hmmmm) {
                        System.out.println("Invalid syntax. Enter longitude and latitude, separated by a comma");
                    } catch (NullPointerException tooMany) {
                        System.out.println("Invalid syntax. Enter one set of coordinates, separated by a comma");
                    }
                }
            }
        }
    }

    public void readDataMap(String fileName) {
        if (!readDataArrayCheck) readDataArray(fileName);

        for (double[] row: arrayOfEarth) {

            String coordinates = row[0] + "\t" + row[1];
            mapOfEarth.put(coordinates, row[2]);
        }
    }

    public void generateMap(double resolution) { // to generate random map for testing
        for (int lng = 0; lng < 360 * resolution; lng++) {
            for (double lat = (-90 * resolution); lat <= 90 / resolution; lat++) { //   contrary to the assignment doc, there are 181 latitudes, so the number of coordinates will be 360*181/resolution
                String coordinates = lng * resolution + "\t" + lat * resolution;
                mapOfEarth.put(coordinates, (Math.random() * (16948)) - 10421);
            }
        }
    }

    public double getAltitude(double longitude, double latitude) {
        String coordinates = longitude + "\t" + latitude;

        return mapOfEarth.get(coordinates);
    }

    public Earth() {
        this.addMouseListener(this);
    }

    public void writeToFile(){
        try {
            File file = new File("src/Assignment/file.txt");
            file.createNewFile();

            BufferedWriter b = new BufferedWriter(new FileWriter(file));

            b.write("Coordinates in list:");
            b.write(System.getProperty("line.separator"));

            for (MapCoordinate coordinate: mapCoordinates) {

                b.write(System.getProperty("line.separator"));
                b.write(("Longitude, Latitude, Altitude: " + coordinate.toString()));

            }
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(Color.BLUE);

        for (Map.Entry < String, Double > word: mapOfEarth.entrySet()) {
            String[] coordinates = word.getKey().split("\t");

            double longitude = Double.parseDouble(coordinates[0]) + 180; //transformation applied to move entire map to left by 180 degrees
            double latitude = Double.parseDouble(coordinates[1]);
            double altitude = word.getValue() - seaLevel;

            if (longitude > 360) {
                longitude = longitude - 360; //transformation applied to move longitudes>180 to 0 - 'turning' the globe.

            }
            if (altitude < 0) {
                g.setColor(Color.getHSBColor((float) .6, (float) .8, (float)(.85 - (.8 * (0 - altitude) / 10421))));

            } else {
                if (latitude < -60) {
                    g.setColor(Color.getHSBColor((float) 0, (float) .03, (float)(.70 + (.3 * (altitude) / 6527))));
                } else {
                    switch ((int) altitude / 250) {

                        case 0:
                            g.setColor(Color.getHSBColor((float) 0.3, (float) .9, (float)(.40 + (.3 * (altitude) / 500))));
                            break;
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            g.setColor(Color.getHSBColor((float) 0.1, (float) .5, (float)(.40 + (.4 * (altitude) / 2000))));
                            break;
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                        case 24:
                        case 25:
                        case 26:
                            g.setColor(Color.getHSBColor((float) 0, (float) .03, (float)(.70 + (.3 * (altitude) / 6527))));
                            break;
                    }
                }
            }
            g.fillRect((int) Math.round(longitude * 4), -(int) Math.round((latitude - 90) * 4), 1, 1);
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) { //push coordinate to list

            double longitude = mouseEvent.getX() / 4 + 180;
            if (longitude >= 360) longitude = longitude - 360;

            double latitude = 90 - mouseEvent.getY() / 4;
            double altitude = getAltitude(longitude, latitude);

            System.out.println("Coordinates: " + longitude + ", " + latitude + "\tAltitude: " + altitude);

            MapCoordinate coordinate2 = new MapCoordinate(longitude, latitude, altitude);

            try {
                System.out.println("\nDistance: " + coordinate.haversineDist(coordinate2) + "\n");

            } catch (NullPointerException ignore) {}



            coordinate = new MapCoordinate(longitude, latitude, altitude);
            mapCoordinates.add(coordinate);
            Collections.sort(mapCoordinates);

            writeToFile();

        } else if (mouseEvent.getButton() == MouseEvent.BUTTON2) {//Extra option to be able to see list on middle click
            try {
                System.out.println("\nCoordinates in list:");

                for (MapCoordinate entry: mapCoordinates) {
                    System.out.println(entry.toString());
                }
            } catch (NullPointerException ignore) {}
        } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) { //pop last coordinate from list
            try {
                MapCoordinate temp = mapCoordinates.get(mapCoordinates.size() - 1);
                mapCoordinates.remove(mapCoordinates.size() - 1);
                System.out.println("Deleted -\t" + "Coordinates: " + temp.toString());

                writeToFile();
            } catch (IndexOutOfBoundsException ignore) {}
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

}