package course.concurrency.m3_shared;

public class Counter {

    private static final Object lock = new Object();

    private static final int MAX_COUNTER = 3;
    private static volatile int counter;

    private static int number = 1;

    public static void first() {

        while (true) {
            synchronized (lock) {
                System.out.println("1: syn");
                try {
                    if (number == 1) {
                        System.out.println("1");
                        number = 2;
                        // this thread release monitor not right here (not right after notifyAll()) !
                        // Another thread can only access the monitor ONLY when
                        // this thread releases the monitor and left the synchronized block
                        lock.notifyAll();
                        System.out.println("1: notify All");
                        Thread.sleep(1000);
                        System.out.println("1: notify All + 1000 mc");
                    }
                    System.out.println("1: before wait");
                    lock.wait();  // release monitor, go out of synchronized block
                    System.out.println("1: after wait");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void second() {
        while (true) {
            synchronized (lock) {
                System.out.println("2: syn");
                try {
                    if (number == 2) {
                        System.out.println("2");
                        number = 3;
                        // this thread release monitor not right here (not right after notifyAll()) !
                        // Another thread can only access the monitor ONLY when
                        // this thread releases the monitor and left the synchronized block
                        lock.notifyAll();
                        System.out.println("2: notify All");
                        Thread.sleep(1000);
                        System.out.println("2: notify All + 1000 mc");
                    }
                    System.out.println("2: before wait");
                    lock.wait();  // release monitor, go out of synchronized block
                    System.out.println("2: after wait");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void third() {
        while (true) {
            synchronized (lock) {
                System.out.println("3: syn");
                try {
                    if (number == 3) {
                        System.out.println("3");
                        number = 1;
                        // this thread release monitor not right here (not right after notifyAll()) !
                        // Another thread can only access the monitor ONLY when
                        // this thread releases the monitor and left the synchronized block
                        lock.notifyAll();
                        System.out.println("3: notify All");
                        Thread.sleep(1000);
                        System.out.println("3: notify All + 1000 mc");
                    }
                    System.out.println("3: before wait");
                    lock.wait();  // release monitor, go out of synchronized block
                    System.out.println("3: after wait");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> first());
        Thread t2 = new Thread(() -> second());
        Thread t3 = new Thread(() -> third());

        t2.start();
        t3.start();
        // change the order of starts thread (1-st at the end)
        // to show that monitor is released
        Thread.sleep(1000);
        // by this time t2 and t3 execute lock.wait()'s and released monitor, so t1 can acquire monitor
        t1.start();
    }
}
