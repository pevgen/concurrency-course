package course.concurrency.m3_shared.auction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notifier {

    // для блокирующих операций, чем больше потоков, тем лучше.
    // но (!) cached не подойдёт, т.к. очень много вызовов и безразмерное увеличение пула потоков
    // может привести к переполнению памяти
    private final ExecutorService executorService = Executors.newFixedThreadPool(100);

    public void sendOutdatedMessage(Bid bid) {
        executorService.submit(() -> imitateSending());
    }

    private void imitateSending() {
        // don't remove this delay, deal with it properly
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
    }

    public void shutdown() {executorService.shutdown();}
}
