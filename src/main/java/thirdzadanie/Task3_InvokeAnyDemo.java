package thirdzadanie;

import java.util.*;
import java.util.concurrent.*;

public class Task3_InvokeAnyDemo {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Создаем коллекцию задач для вычисления факториала
        List<Callable<Long>> factorialTasks = new ArrayList<>();

        // Добавляем задачи с разными числами и разным временем выполнения
        factorialTasks.add(new FactorialTask(10, 2000)); // 2 сек
        factorialTasks.add(new FactorialTask(5, 500));   // 0.5 сек (самая быстрая)
        factorialTasks.add(new FactorialTask(15, 3000)); // 3 сек
        factorialTasks.add(new FactorialTask(8, 1500));  // 1.5 сек

        System.out.println("Запуск invokeAny()");
        System.out.println("Ожидаем первый успешно завершившийся результат...");

        try {
            long startTime = System.currentTimeMillis();
            Long firstResult = executor.invokeAny(factorialTasks);
            long endTime = System.currentTimeMillis();

            System.out.println("Первый полученный результат: " + firstResult);
            System.out.println("Время ожидания: " + (endTime - startTime) + " мс");

        } catch (InterruptedException e) {
            System.err.println("Выполнение прервано: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Все задачи завершились с ошибкой: " + e.getCause().getMessage());
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
            System.out.printf("[%s] Вычисление факториала %d...%n", threadName, number);
            Thread.sleep(delayMs);

            long factorial = 1;
            for (int i = 2; i <= number; i++) {
                factorial *= i;
            }

            System.out.printf("[%s] Факториал %d = %d%n", threadName, number, factorial);
            return factorial;
        }
    }
}
