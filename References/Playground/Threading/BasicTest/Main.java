import java.math.BigInteger;

class ThreadTest implements Runnable {
    private static long index = 0;
    private String threadName;
    private boolean cont = true;
    public long result = 0;
    private Main m;

    public ThreadTest(Main m) {
        this.m = m;

        this.threadName = "Thread #" + index++;

        System.out.println("Creating " + this.threadName);

        Thread t = new Thread(this, this.threadName);
        t.start();
    }

    public void run() {
        for(int i = 0; this.cont; ++i) {
            long s = 1;
            for(int j = i; j > 0; --j) {
                s *= j;
            }

            this.m.updateBiggest(s, this.threadName);
        }
    }

    public void stop() {
        this.cont = false;
    }
}


public class Main {
    private long largestSoFar = 0;
    private String fromLargest = "";

    public static void main(String[] args) {
        Main m = new Main();
        m.run();
    }

    public void run() {
        for(int i = 0; i < 15; ++i) {
            new ThreadTest(this);
        }

        try {
            Thread.sleep(10000);
        }
        catch(Exception e){}

        System.out.println(this.fromLargest + ": " + this.largestSoFar);
        System.exit(0);
    }

    public void updateBiggest(long nl, String name) {
        if(nl > this.largestSoFar) {
            this.largestSoFar = nl;
            this.fromLargest = name;
        }
    }
}
