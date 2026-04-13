package fourthzadanie;

import java.util.*;
import java.util.concurrent.*;

public class Task4_InvokeAllDemo {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        List<Callable<Long>> tasks = new ArrayList<>();
        tasks.add(new FactorialTask(10, 1000));  // успешная
        tasks.add(new FactorialTask(5, 500));    // успешная
        tasks.add(new FactorialTask(-1, 200));   // вызовет исключение
        tasks.add(new FactorialTask(15, 2000));  // успешная
        tasks.add(new FactorialTask(0, 300));    // успешная

        System.out.println("Запуск invokeAll()");
        System.out.println("Ожидаем завершения ВСЕХ задач...\n");

        long startTime = System.currentTimeMillis();

        List<Future<Long>> futures;
        try {
            futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Выполнение прервано");
            Thread.currentThread().interrupt();
            executor.shutdownNow();
            return;
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\nВсе задачи завершены или превысили таймаут");
        System.out.println("Общее время: " + (endTime - startTime) + " мс\n");

        // Обработка результатов
        System.out.println("Результаты выполнения задач:");

        for (int i = 0; i < futures.size(); i++) {
            Future<Long> future = futures.get(i);
            System.out.printf("Задача #%d: ", i + 1);

            if (future.isCancelled()) {
                System.out.println("ОТМЕНЕНА (превышен таймаут)");
            } else {
                try {
                    Long result = future.get();
                    System.out.println("УСПЕХ, результат = " + result);
                } catch (ExecutionException e) {
                    System.out.println("ОШИБКА: " + e.getCause().getMessage());
                } catch (InterruptedException e) {
                    System.out.println("ПРЕРВАНО");
                    Thread.currentThread().interrupt();
                }
            }
        }

        executor.shutdown();
    }

    static class FactorialTask implements Callable<Long> {
        private final int number;
        private final long delayMs;

        FactorialTask(int number, long delayMs) {
            this.number = number;
            this.delayMs = delayMs;
        }

        @Override
        public Long call() throws Exception {
            String threadName = Thread.currentThread().getName();

            if (number < 0) {
                throw new IllegalArgumentException("Число не может быть отрицательным: " + number);
            }

            System.out.printf("[%s] Вычисление факториала %d...%n", threadName, number);
            Thread.sleep(delayMs);

            long factorial = 1;
            for (int i = 2; i <= number; i++) {
                factorial *= i;
            }

            System.out.printf("[%s] Завершено: %d! = %d%n", threadName, number, factorial);
            return factorial;
        }
    }
}
