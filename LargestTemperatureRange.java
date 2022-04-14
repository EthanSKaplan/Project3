/*
To get the 10-minute range, observe that 10 minutes is 8*10 readings. 
So the question is, what sub-sequence of length 80 in the array had 
the largest temperature difference?

In an array of 480, there are 401 places to start a sub sequence of 80 
(iterate through the array and take the 79 ints after it). So you have 401 
hypothetical subarrays. On any given subarray, the way to get the largest 
temp difference is iterate through the list and get the `diff = max - min`. 
We can compare new diffs to old diffs as we iterate through sub-sequences 
so at the end we're left with the largest one.

But after you get diff[0] from ss[0] (the first sub-sequence), to calculate 
ss[1] you have to iterate over 79 of the same numbers. Is there any way 
to not have to redo that work?

One way is to save the index of max and min from ss[0]. If you do that, then 
you can simply check if they're also in ss[1]. Now, ss[1][-1] doesn't have to 
be compared with all of ss[0], just max[0] and min[0] to see if it beats either 
of those. If it does, calculate a new diff. If it doesn't, move on.

But what if max[0] and min[0] aren't in ss[1]? Now those are lost to ss[1] 
which means we have to start over.

Since we can't use them for comparison anymore (they necessarily won't be in 
any future ss's), we can discard them and replace them with min[1] and max[1]. 
Then we just continue.

With this approach, we should cut down the work by a factor of 80, because once 
a very large or very small number enters a ss, it'll take the length of that ss 
to leave it. And only then will we have to calculate new values for max and min 
using the brute force iterative method.
*/

public class LargestTemperatureRange {

    static int nt = 8; // number of threads
    static int m = 60; // minutes in an hour
    int t = 10; // 10 minute intervals
    int[] arr = new int[nt*m]; // array to hold temp readings
    Min min;
    Max max;
    LargestDiff ld;

    public static void main(String[] args) {

        /* Example main function in case you'd like to run this directly */
        int n = nt*m;
        int[] myArr = new int[n];

        for (int i = 0; i < myArr.length; i++) {
            myArr[i] = getRandomTemp();
        }
        
        for (int i = 0; i < myArr.length; i++) {
            System.out.println(i + " : " + myArr[i]);
        }

        LargestTemperatureRange r = new LargestTemperatureRange(myArr);
        System.out.println(String.format("max %4d at index %3d", r.max.max, r.max.index));
        System.out.println(String.format("min %4d at index %3d", r.min.min, r.min.index));
        System.out.println(String.format("diff = %d", r.ld.diff));
    }

    public static int getRandomTemp() {
        final int MAX = 70;
        final int MIN = -110;
        return (int) ((Math.random() * (MAX - MIN)) + MIN);
    }

    public LargestTemperatureRange(int [] arr) {
        min = new Min(arr[0], 0);
        max = new Max(arr[0], 0);
        ld = new LargestDiff(min, max);

        for(int i = 0; i < arr.length - nt*t; i++) {
            
            // see if max and min are in the new subsequence
            // if they aren't, do the work

            if (!(max.index >= i && max.index <= i)) // current max is not in range
                max = Max.getMax(arr, i, i+t-1);

            if (!(min.index >= i && min.index <= i)) // current min is not in range
                min = Min.getMin(arr, i, i+t-1);
        
            // got max and min, now record diff
            if (ld.test(min, max))
                ld = new LargestDiff(min, max);
        }
    }


    static class Max {
        int max;
        int index;

        public Max(int max, int index) {
            this.max = max;
            this.index = index;
        }

        public static Max getMax(int [] arr, int start, int end) {
            if (arr == null || start < 0 || end >= arr.length) // guard condition
                return null;

            // iterate through array, get the largest value
            int tmp_max = arr[0];
            int tmp_index = 0;
            for (int i = 1; i < arr.length; i++) {
                if (arr[i] > tmp_max) {
                    tmp_max = arr[i];
                    tmp_index = i;
                }
            }

            return new Max(tmp_max, tmp_index);
        }
    }
    
    static class Min {
        int min;
        int index;

        public Min(int min, int index) {
            this.min = min;
            this.index = index;
        }

        public static Min getMin(int [] arr, int start, int end) {
            if (arr == null || start < 0 || end >= arr.length) // guard condition
                return null;

            // iterate through array, get the smallest value
            int tmp_min = arr[0];
            int tmp_index = 0;
            for (int i = 1; i < arr.length; i++) {
                if (arr[i] < tmp_min) {
                    tmp_min = arr[i];
                    tmp_index = i;
                }
            }

            return new Min(tmp_min, tmp_index);
        }
    }
    
    class LargestDiff {
        Min min;
        Max max; // we record the whole object and not just the value so we can keep track of the indices
        int diff;

        public LargestDiff(Min min, Max max) {
            this.min = min;
            this.max = max;
            this.diff = max.max - min.min;
            if (this.diff < 0)
                this.diff *= -1; // always keep diff positive (absolute value)
        }

        public boolean test(Min min, Max max) {
            int contender = max.max - min.min;
            if (contender < 0)
                contender *= -1; // keep contender positive to compare absolute values
            if (this.diff < contender){
                this.diff = contender;
                return true;
            }
            else
                return false;
        }
    }
}