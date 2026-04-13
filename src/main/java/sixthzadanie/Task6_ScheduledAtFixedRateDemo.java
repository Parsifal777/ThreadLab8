package sixthzadanie;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Task6_ScheduledAtFixedRateDemo {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        AtomicInteger counter = new AtomicInteger(1);

        System.out.println("scheduleAtFixedRate: задача каждые 2 секунды");
        System.out.printf("Начало в: %s%n%n", LocalTime.now().format(TIME_FORMATTER));

        // Задача с коротким временем выполнения (нормальный режим)
        Runnable shortTask = () -> {
            int taskNum = counter.getAndIncrement();
            String startTime = LocalTime.now().format(TIME_FORMATTER);

            System.out.printf("[%s] Задача #%d начата%n", startTime, taskNum);

            // После 5 выполнений демонстрируем "долгую" задачу
            if (taskNum == 5) {
                System.out.printf("[%s] Задача #%d выполняется ДОЛЬШЕ интервала (4 сек)...%n",
                        LocalTime.now().format(TIME_FORMATTER), taskNum);
                try {
                    Thread.sleep(4000); // 4 секунды > интервала 2 сек
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                try {
                    Thread.sleep(500); // обычное быстрое выполнение
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            System.out.printf("[%s] Задача #%d завершена%n",
                    LocalTime.now().format(TIME_FORMATTER), taskNum);
        };

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                shortTask,
                0,
                2,
                TimeUnit.SECONDS
        );

        // Даем поработать 12 секунд
        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        future.cancel(false);
        scheduler.shutdown();

        try {
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
