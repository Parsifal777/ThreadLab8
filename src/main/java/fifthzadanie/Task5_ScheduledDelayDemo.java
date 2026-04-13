package fifthzadanie;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

public class Task5_ScheduledDelayDemo {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        System.out.println("Демонстрация schedule() с задержкой");

        long delaySeconds = 5;
        LocalTime scheduledTime = LocalTime.now().plus(delaySeconds, ChronoUnit.SECONDS);

        System.out.printf("Текущее время: %s%n", LocalTime.now().format(TIME_FORMATTER));
        System.out.printf("Задача запланирована через %d секунд (в %s)%n%n",
                delaySeconds, scheduledTime.format(TIME_FORMATTER));

        // Сохраняем время отправки для измерения реальной задержки
        final long submitTime = System.nanoTime();

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            long executionTime = System.nanoTime();
            long actualDelayMs = (executionTime - submitTime) / 1_000_000;

            System.out.printf("[%s] ЗАДАЧА ВЫПОЛНЕНА!%n",
                    LocalTime.now().format(TIME_FORMATTER));
            System.out.printf("Фактическая задержка: %d мс (запрошено: %d мс)%n",
                    actualDelayMs, delaySeconds * 1000);
            System.out.printf("Погрешность: %.2f мс%n",
                    Math.abs(actualDelayMs - delaySeconds * 1000));

            // Проверяем, что задача выполнена вовремя (допуск 100 мс)
            long difference = Math.abs(actualDelayMs - delaySeconds * 1000);
            if (difference <= 100) {
                System.out.println("✓ Задача выполнена вовремя (погрешность в пределах 100 мс)");
            } else {
                System.out.println("✗ Задача выполнена с существенным отклонением");
            }

        }, delaySeconds, TimeUnit.SECONDS);

        // Ожидаем завершения задачи
        try {
            future.get(); // ждем выполнения
        } catch (InterruptedException e) {
            System.err.println("Ожидание прервано");
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Ошибка выполнения задачи: " + e.getCause().getMessage());
        }

        scheduler.shutdown();
        System.out.println("\nПланировщик остановлен.");
    }
}
