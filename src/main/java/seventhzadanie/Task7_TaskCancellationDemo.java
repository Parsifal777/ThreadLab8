package seventhzadanie;

import java.util.concurrent.*;

public class Task7_TaskCancellationDemo {

    public static void main(String[] args) {
        System.out.println("Демонстрация отмены задачи\n");

        System.out.println("Часть 1: Отмена через Future.cancel()");
        demonstrateFutureCancel();

        System.out.println("\nЧасть 2: Отмена через shutdownNow()");
        demonstrateShutdownNow();
    }

    private static void demonstrateFutureCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<String> longTask = () -> {
            System.out.println("Запущена долгая задача (бесконечный цикл с проверкой прерывания)");
            int iteration = 0;
            while (!Thread.currentThread().isInterrupted()) {
                iteration++;
                // Имитация работы с проверкой прерывания
                if (iteration % 100_000_000 == 0) {
                    System.out.printf("Задача всё ещё работает... (итерация %,d)%n", iteration);
                }

                // Явная проверка флага прерывания
                if (Thread.interrupted()) {
                    System.out.println("Обнаружено прерывание внутри задачи!");
                    throw new InterruptedException("Задача прервана");
                }
            }
            return "Завершено";
        };

        Future<String> future = executor.submit(longTask);

        // Ждем 2 секунды и отменяем задачу
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nВызов future.cancel(true)...");
        boolean cancelled = future.cancel(true);
        System.out.println("Результат cancel(): " + cancelled);

        // Проверка статуса отмены
        System.out.println("future.isCancelled(): " + future.isCancelled());
        System.out.println("future.isDone(): " + future.isDone());

        try {
            String result = future.get();
            System.out.println("Результат: " + result);
        } catch (CancellationException e) {
            System.out.println("Ожидаемое исключение CancellationException: задача была отменена!");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Другое исключение: " + e.getMessage());
        }

        executor.shutdown();
    }

    private static void demonstrateShutdownNow() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Запускаем несколько долгих задач
        Future<?> future1 = executor.submit(() -> {
            try {
                System.out.println("Задача 1 запущена");
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000);
                    System.out.println("Задача 1 работает...");
                }
            } catch (InterruptedException e) {
                System.out.println("Задача 1 получила InterruptedException");
                Thread.currentThread().interrupt();
            }
            return null;
        });

        Future<?> future2 = executor.submit(() -> {
            try {
                System.out.println("Задача 2 запущена");
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000);
                    System.out.println("Задача 2 работает...");
                }
            } catch (InterruptedException e) {
                System.out.println("Задача 2 получила InterruptedException");
                Thread.currentThread().interrupt();
            }
            return null;
        });

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nВызов shutdownNow()...");
        executor.shutdownNow();

        System.out.println("\nСтатус задач после shutdownNow():");
        System.out.println("future1.isCancelled(): " + future1.isCancelled());
        System.out.println("future1.isDone(): " + future1.isDone());
        System.out.println("future2.isCancelled(): " + future2.isCancelled());
        System.out.println("future2.isDone(): " + future2.isDone());

        try {
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
