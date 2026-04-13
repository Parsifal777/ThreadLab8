package firstzadanie;

import java.util.concurrent.*;

public class Task1_RejectedExecutionHandlerDemo {

    public static void main(String[] args) {
        // Создаем пул из 2 потоков с ограниченной очередью на 3 задачи
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,
                2,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(3) // очередь на 3 задачи
        );

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        System.out.println("Демонстрация CallerRunsPolicy");
        System.out.println("Пул потоков: 2, Очередь: 3");

        // Пытаемся отправить 10 задач
        for (int i = 1; i <= 10; i++) {
            final int taskId = i;
            System.out.printf("Отправка задачи #%d... ", taskId);

            try {
                executor.submit(() -> {
                    String threadName = Thread.currentThread().getName();
                    System.out.printf("[%s] Выполняется задача #%d%n", threadName, taskId);
                    try {
                        Thread.sleep(1000); // имитация работы
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                System.out.println("принято");
            } catch (RejectedExecutionException e) {
                System.out.println("ОТКЛОНЕНО (это не должно произойти с CallerRunsPolicy)");
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
