package course.concurrency.m2_async.minPrice;

import java.util.Collection;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Double.NaN;

/**
 * Практика: возврат наименьшей цены
 * <p>
 * Задание на проверку
 * Обязательное
 * Исходный код доступен в классе PriceAggregator:
 * <p>
 * Метод getPrice узнаёт цену на товар в конкретном магазине. Может выполняться долго (делает HTTP запрос в магазин)
 * <p>
 * Unit тесты находятся в классе  PriceAggregatorTests.
 * <p>
 * Задача:
 * <p>
 * Написать метод PriceAggregator#getMinPrice, который возвращает минимальную цену на товар среди всех магазинов. Кто-то из них ответит быстро, а кто-то не очень. Минимальная цена выбирается из тех результатов, которые успели прийти. Если ни один магазин не вернул результат, возвращается NaN.
 * <p>
 * Метод getMinPrice  должен выполняться не более трёх секунд.
 */

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    // чтобы запустить запросы (состояние потока BLOCKED - пока не вернётся ответ)
    // КО ВСЕМ магазинам примерно в одно время
    // (иначе часть встанет в очередь и не успеют выполниться за ожидаемое в тесте время)
    private ExecutorService poolForAllRequest = Executors.newCachedThreadPool();

    public double getMinPrice(long itemId) {

        List<CompletableFuture<Double>> requests = shopIds.stream()
                .map(shopId ->
                        CompletableFuture
                                .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), poolForAllRequest)
                                .completeOnTimeout(Double.POSITIVE_INFINITY, 2900, TimeUnit.MILLISECONDS)
                                .exceptionally(t -> Double.POSITIVE_INFINITY))
                .toList();

        CompletableFuture<Void> allResults =
                CompletableFuture.allOf(requests.toArray(new CompletableFuture[0]));
        allResults.join();

        OptionalDouble resultOpt =
                requests.stream()
                        .mapToDouble(CompletableFuture::join)
                        .filter(Double::isFinite)
                        .min();

        // System.out.println(requests);

        return resultOpt.orElse(NaN);
    }
}
