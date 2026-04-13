package secondzadanie;

import java.util.concurrent.*;

public class Task2_CallableWithResult {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Callable задача для суммирования числового ряда
        Callable<Long> sumTask = () -> {
            System.out.println("Начинаем вычисление суммы чисел от 1 до 1_000_000...");
            long sum = 0;
            for (int i = 1; i <= 1_000_000; i++) {
                sum += i;
                // Имитация длительной работы
                if (i % 100_000 == 0) {
                    Thread.sleep(10);
                }
            }
            return sum;
        };

        // Callable задача, которая выбрасывает исключение
        Callable<Integer> errorTask = () -> {
            System.out.println("Запуск задачи с ошибкой...");
            Thread.sleep(500);
            throw new IllegalArgumentException("Демонстрационное исключение в Callable!");
        };

        System.out.println("Отправка задач на выполнение");

        // Запуск успешной задачи
        Future<Long> futureSum = executor.submit(sumTask);

        // Запуск задачи с ошибкой
        Future<Integer> futureError = executor.submit(errorTask);

        // Получение результата успешной задачи
        try {
            System.out.println("Ожидание результата sumTask...");
            Long result = futureSum.get(3, TimeUnit.SECONDS);
            System.out.println("Результат sumTask: сумма = " + result);
        } catch (TimeoutException e) {
            System.err.println("Задача sumTask превысила время ожидания!");
        } catch (InterruptedException e) {
            System.err.println("Поток был прерван: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Ошибка выполнения sumTask: " + e.getCause().getMessage());
        }

        // Получение результата задачи с ошибкой
        try {
            System.out.println("\nОжидание результата errorTask...");
            Integer result = futureError.get();
            System.out.println("Результат errorTask: " + result);
        } catch (InterruptedException e) {
            System.err.println("Поток был прерван: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Ожидаемая ошибка выполнения errorTask: " + e.getCause().getMessage());
        }

        executor.shutdown();
    }
}
