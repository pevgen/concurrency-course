package course.concurrency.m3_shared.auction;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    // инициализируем, чтобы не проверять кадый раз на null
    // volatile - чтобы в getLatestBid было видно актуальное значение
    // (можно без volatile,  но с synchronized в getLatestBid )
    private volatile Bid latestBid = new Bid(-1L, -1L, -1L);

    private final Object lock = new Object();

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            synchronized (lock) {
                if (bid.getPrice() > latestBid.getPrice()) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
