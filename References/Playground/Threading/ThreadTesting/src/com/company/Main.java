package com.company;

class ThreadTest implements Runnable {
    private String threadName;
    private boolean cont = true;
    public long result = 0;

    ThreadTest(String name) {
        this.threadName = name;
        System.out.println("Creating " + this.threadName);
        System.out.println("Starting " + this.threadName);
        Thread t = new Thread(this, this.threadName);
        t.start();
    }

    public void run() {
        System.out.println("Running " + this.threadName);

        for(this.result = 0; this.cont; ++this.result) {
            System.out.println(this.result);

            for(int i = 0; i < 100; ++i) {
                System.out.printf("%d: %d\n", this.result, i);
            }
        }

        System.out.println("Thread " + this.threadName + " exiting");
    }

    public void stop() {
        this.cont = false;
    }
}


public class Main {
    public static void main(String[] args) {
        Main m = new Main();
        m.run();
    }

    public void run() {
        ThreadTest t1 = new ThreadTest("t1");

        try {
            Thread.sleep(50);  // sleep 1 second
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        t1.stop();
        System.out.println("Captured result: " + t1.result);
    }
}
