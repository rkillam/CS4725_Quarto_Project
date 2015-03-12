class ThreadTest implements Runnable {
    public static long MAX_THREAD_BREDTH = 20;
    public static long index = 0;

    private String threadName;

    ThreadTest() {
        this.threadName = "Thread #" + index++;

        System.out.println("Creating " + this.threadName);

        Thread t = new Thread(this, this.threadName);
        t.start();
    }

    public void run() {
        System.out.println("Running " + this.threadName);

        try {
            for(int i = 0; i < MAX_THREAD_BREDTH; ++i) {
                new ThreadTest();
            }
        }
        catch(Exception e) {
            System.out.println(e);
            System.exit(-1);
        }

        System.out.println("Thread " + this.threadName + " exiting");
    }
}


public class Main {
    public static void main(String[] args) {
        Main m = new Main();
        m.run();
    }

    public void run() {
        for(int i = 0; i < ThreadTest.MAX_THREAD_BREDTH; ++i) {
            new ThreadTest();
        }

        try {
            Thread.sleep(1000);  // sleep 1 second
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("done at " + ThreadTest.index);

        System.exit(0);
    }
}
