import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class SafeCounterWithoutLock {
    private AtomicInteger counter = new AtomicInteger(0);
    
    public int getValue() {
        return counter.get();
    }

    public int getAndIncrement() {
        while(true) {
            int existingValue = getValue();
            int newValue = existingValue + 1;
            if(counter.compareAndSet(existingValue, newValue)) {
                return existingValue;
            }
        }
    }

    public void reset() {
        this.counter = new AtomicInteger(0);
    }
}