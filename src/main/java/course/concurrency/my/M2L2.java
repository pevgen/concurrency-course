package course.concurrency.my;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class M2L2 {

    public static long longTask() {
        try {
            Thread.sleep(1000); // + try-catch
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ThreadLocalRandom.current().nextInt();
    }

    public static void main(String[] args) {

        // 1. отправляем в экзекьютор 10 задач, после каждого добавления печатаем количество потоков в пуле
        //    corePoolSize = maximumPoolSize = 10
        // ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10));
        // 1 2 3 4 5 6 7 8 9 10
        // Изначальное количество потоков не равно corePoolSize, оно растёт по мере необходимости

        // 2. Посмотрим, что происходит, когда corePoolSize заполнен. Изменим corePoolSize на 5
        // ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10));
        // 1 2 3 4 5 5 5 5 5 5
        // Потоки в пуле доросли до corePoolSize, но не стали увеличиваться до maximumPoolSize

        // 3. Увеличим maxPoolSize до 50
        // ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 50, 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(5));
        // 1 2 3 4 5 5 5 5 5 5
        // Первые 5 задач разбирают потоки в пуле, остальные 5 ждут в очереди. В пуле всё так же corePoolSize потоков,
        // изменение maxPoolSize ни к чему не приводит

        // 4. Поэкспериментируем теперь с размером очереди. Уменьшим очередь до 2 элементов
        // ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(2));
        // 1 2 3 4 5 5 5 6 7 8
        // Первые 5 задач отправились на выполнение, следующие 2 добавились в очередь, а дальше наконец начали добавляться потоки в пул.
        // Получается, что потоки растут до corePoolSize и не увеличиваются до maxPoolsize, пока в очереди есть место

        // 5. Проверим нашу теорию, теперь поставим такие параметры:
        // corePoolSize = 5
        // maxPoolSize = 6
        //размер очереди = 1
        // ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 6, 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1));
        // 1 2 3 4 5 5 6 RejectedExecutionException
        // 5 потоков добавились в пул, 1 задача отправилась в очередь, затем пришлось нарастить количество потоков до 6
        // Для следующих задач места в пуле и в очереди нет, поэтому сработал дефолтный RejectedExecutionHandler

        // 6. Вернёмся к более реалистичной ситуации, когда очередь в экзекьюторе не ограничивается:
        //corePoolSize = 5
        //maxPoolSize = 10
         ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        // 1 2 3 4 5 5 5 5 5 5
        //


        for (int i = 0; i < 10; i++) {
            executor.submit(() -> longTask());
            System.out.print(executor.getPoolSize() + " ");
        }

        executor.shutdown();
    }
}
