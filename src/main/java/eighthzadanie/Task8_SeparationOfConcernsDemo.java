package eighthzadanie;

import java.util.concurrent.*;

public class Task8_SeparationOfConcernsDemo {

    public static void main(String[] args) {
        System.out.println("Разделение: Постановщик задач vs Обработчик результатов\n");

        // Создаем сервис обработки
        ComputationService service = new ComputationService(3);

        // Постановщик задач (Producer)
        System.out.println("Постановщик отправляет задачи");
        for (int i = 1; i <= 5; i++) {
            service.submitComputation("Задача-" + i, i * 10);
        }

        // Обработчик результатов (Consumer) - работает в главном потоке
        System.out.println("\nОбработчик получает результаты");
        service.processResults();

        service.shutdown();
    }
}

class ComputationService {
    private final ExecutorService executor;
    private final BlockingQueue<Future<Integer>> resultQueue;

    public ComputationService(int threadCount) {
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.resultQueue = new LinkedBlockingQueue<>();
    }

    public void submitComputation(String taskName, int value) {
        Callable<Integer> task = () -> {
            System.out.printf("  [%s] Начало вычислений для %s (значение: %d)%n",
                    Thread.currentThread().getName(), taskName, value);

            Thread.sleep(1000 + (long)(Math.random() * 2000));

            int result = value * value;
            System.out.printf("  [%s] Завершено: %s -> %d%n",
                    Thread.currentThread().getName(), taskName, result);

            return result;
        };

        Future<Integer> future = executor.submit(task);
        resultQueue.offer(future);
        System.out.printf("Постановщик: задача '%s' отправлена, Future помещен в очередь%n", taskName);
    }

    public void processResults() {
        int processedCount = 0;

        while (!resultQueue.isEmpty() || processedCount == 0) {
            try {
                Future<Integer> future = resultQueue.poll(500, TimeUnit.MILLISECONDS);

                if (future == null) {
                    // Очередь пуста, но могут быть еще не завершенные задачи
                    if (processedCount > 0) {
                        break;
                    }
                    continue;
                }

                try {
                    // Получаем результат с таймаутом
                    Integer result = future.get(5, TimeUnit.SECONDS);
                    System.out.printf("Обработчик: получен результат = %d%n", result);
                    processedCount++;
                } catch (ExecutionException e) {
                    System.err.println("Обработчик: ошибка выполнения - " + e.getCause().getMessage());
                } catch (TimeoutException e) {
                    System.err.println("Обработчик: превышено время ожидания результата");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.printf("%nОбработчик: всего обработано %d результатов%n", processedCount);
    }

    public void shutdown() {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Демонстрация альтернативного подхода - слушатель результатов
    static class ResultListener {
        public void onResult(String taskId, int result) {
            System.out.printf("[Listener] Результат для %s: %d%n", taskId, result);
        }

        public void onError(String taskId, Throwable error) {
            System.err.printf("[Listener] Ошибка для %s: %s%n", taskId, error.getMessage());
        }
    }
}
