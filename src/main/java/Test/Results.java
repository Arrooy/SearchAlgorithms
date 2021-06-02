package Test;

import java.util.LinkedList;

public class Results {

    private final String title;
    private final double elapsedTime;
    private final double bytesUsed;
    private LinkedList<Long> times;
    private LinkedList<Long> mems;

    public Results(String title, double elapsedTime, double bytesUsed, LinkedList<Long> times, LinkedList<Long> mems) {
        this.title = title;
        this.elapsedTime = elapsedTime;
        this.bytesUsed = bytesUsed;
        this.times = times;
        this.mems = mems;
    }
}
