package Test;

import java.util.Arrays;
import java.util.LinkedList;

public class Test {

    private final String origin;
    private final String destination;
    private final LinkedList<String> expectedPlaces;
    private final int distance;

    public Test(String origin, String destination, int distance, String... expectedPlaces) {
        this.origin = origin;
        this.destination = destination;
        this.expectedPlaces = new LinkedList<>(Arrays.asList(expectedPlaces));
        this.distance = distance;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LinkedList<String> getExpectedPlaces() {
        return expectedPlaces;
    }

    public int getDistance() {
        return distance;
    }
}
