import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;

import org.w3c.dom.TypeInfo;

public class Threads {

    public static void main(String[] args)
    {
        long start = System.currentTimeMillis();

        final int np = 500000; // number of presents
        // final int np = 20; // uncomment this along with print statements to see a reasonable output
        final int ns = 4; // number of servants
        

        Chain chain = new Chain(np);
        
        Thread guests[] = new Thread[ns];
        for (int i = 0; i < ns; i++)
            guests[i] = new Thread(chain, Integer.toString(i+1));
        for (int i = 0; i < ns; i++) {
            guests[i].start();
        }
        try {
            for (int i = 0; i < guests.length; i++) {
                guests[i].join();
            }
        } catch (Exception e) {}

        long stop = System.currentTimeMillis();

        System.out.println("Writing thank you cards for " + np + " presents took " + (stop - start) + "ms.");
    
    }
}

class Chain implements Runnable {

    LockFreeList lfl;
    ArrayList<Integer> presents;
    SafeCounterWithoutLock counter;
    SafeCounterWithoutLock counter2;
    int np;

    public Chain(int np) {
        this.np = np;
        lfl = new LockFreeList();

        // Since the only defining feature of these presents is their tag number, they can remain as ints for now
        presents = new ArrayList<Integer>(np);

        for(int i = 1; i<=np; i++) 
            presents.add(i);

        Collections.shuffle(presents);

        counter = new SafeCounterWithoutLock();
        counter2 = new SafeCounterWithoutLock();
    }

    public void run() {
        System.out.println(String.format("Servant %s is adding presents. (enqueueing)", Thread.currentThread().getName()));

        try {
            int i;
            while((i = counter.getAndIncrement()) < np){
                // System.out.println(String.format("Servant %2s: %d", Thread.currentThread().getName(), i));
                lfl.add(i);
            }
        } catch (Exception e) {
            // whatever
        }

        try {
            System.out.println(String.format("Servant %s has begun writing thank you notes. (dequeueing)", Thread.currentThread().getName()));
            int i;
            while((i = counter2.getAndIncrement()) < np){
                // System.out.println(String.format("Servant %2s: %d", Thread.currentThread().getName(), i));
                lfl.remove(i);
            }
        }
        catch (NoSuchElementException e) {
            System.out.println("All done!");
        }

    }

}