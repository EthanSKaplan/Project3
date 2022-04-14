import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.Arrays;

import org.w3c.dom.TypeInfo;

public class CPU {

    public static void main(String[] args)
    {
        long start = System.currentTimeMillis();

        final int nt = 8; // number of threads
        final int freq = 60; // frequency = 1/minute * 60 minutes/hour
        final int numHours = 5;
        
        // generate shared memory space (int array)
        int len = nt*freq; // length of the shared memory space is enough for all 8 threads to each make 60 recordings
        int [] shared = new int[len];

        Rover rover = new Rover(freq, shared, numHours);
        
        // for h hours
        for (int h = 0; h < numHours; h++) {
            
            // create counter to index into shared memory space (the `shared` array)
            SafeCounterWithoutLock counter = new SafeCounterWithoutLock();

            // create 8 threads
            Thread sensors[] = new Thread[nt];
            for (int i = 0; i < nt; i++)
                sensors[i] = new Thread(rover, Integer.toString(i+1));

            // start 8 threads
            for (int i = 0; i < nt; i++){
                sensors[i].start(); // collect the temp readings
                try {
                    sensors[i].join(); // (wait for everyone to finish)
                } catch (InterruptedException e) {}
            }

            // reset the counter for the next hour of recordings
            rover.counter.reset(); 

            // generate and print the report
            Report report = new Report(shared, h, nt, 5);
            report.printReport();
        }

        long stop = System.currentTimeMillis();
        System.out.println("Recording the temperature for " + numHours + " hours took " + (stop - start) + "ms.");
    }
}

class Rover implements Runnable {
    int freq;
    int [] shared;
    int numHours;
    SafeCounterWithoutLock counter;

    public Rover(int freq, int [] shared, int numHours) {
        this.freq = freq;
        this.shared = shared;
        this.numHours = numHours;
        counter = new SafeCounterWithoutLock();
    }

    public void run() {

        for (int j = 0; j < freq; j++) {
            int temp = getRandomTemp();
            shared[counter.getAndIncrement()] = temp;            
        }
    }

    static final int MAX = 70;
    static final int MIN = -110;
    public int getRandomTemp() {
        return (int) ((Math.random() * (MAX - MIN)) + MIN);
    }   
}

class Report {

    int nt;
    int hour;
    int [] shared;
    int [] lowest;
    int [] highest;
    int first, last, from, to;
    LargestTemperatureRange ltr;
    int nr; // number of readings for lowest and highest temperatures

    public Report(int [] shared, int hour, int nt, int nr) {

        this.hour = hour;
        this.nt = nt;
        this.shared = shared;
        this.nr = nr;

        // get the 10-minute largest temperature range
        ltr = new LargestTemperatureRange(shared);
        
        // get the top 5 highest and top 5 lowest temperatures
        Arrays.sort(shared);
        int len = shared.length;

        this.lowest = new int[nr];
        for (int i = 0; i < nr; i++) {
            lowest[i] = shared[i];
        }

        this.highest = new int[nr];
        for (int i = 0; i < nr; i++) {
            highest[i] = shared[len-i-1];
        }

        // assign 10-minute boundaries for report
        if (ltr.max.index < ltr.min.index) {
            first = ltr.max.index;
            from = ltr.max.max;
            last = ltr.min.index;
            to = ltr.min.min;
        }
        else {
            first = ltr.min.index;
            from = ltr.min.min;
            last = ltr.max.index;
            to = ltr.max.max;
        }
    }

    public void printReport() {

        System.out.println("REPORT FOR HOUR " + hour + ":");
        String border = "============================";
        System.out.println(border);

        System.out.println("The top 5 highest temperatures were:");

        for (int i = 0; i < highest.length; i++)
            System.out.print(highest[i] + " ");
        System.out.println("\n");

        System.out.println("The top 5 lowest temperatures were:");

        for (int i = 0; i < lowest.length; i++)
            System.out.print(lowest[i] + " ");
        System.out.println("\n");
        
        System.out.println("The largest 10-minute temperature difference occured between minutes " + first/nt + " and " + last/nt);

        System.out.println("The temperature went from " + from + " to " + to + " for a difference of " + (from-to) + "F");

        System.out.println(border);
        System.out.println("\n");
    }
}