import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainWithImgscalr {

    public static void main(String[] args) {
        String srcFolder = "/Users/tsyle/Desktop/src";
        String dstFolder = "/Users/tsyle/Desktop/dst";

        // Получаем количество доступных ядер процессора
        int processorCores = Runtime.getRuntime().availableProcessors();
        System.out.println("Используется " + processorCores + " потоков для обработки изображений");

        // Создаем директорию назначения, если она не существует
        File dstDir = new File(dstFolder);
        if (!dstDir.exists()) {
            dstDir.mkdirs();
        }

        File srcDir = new File(srcFolder);
        File[] files = srcDir.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("В исходной директории не найдено файлов");
            return;
        }

        long start = System.currentTimeMillis();

        // Разделяем работу между потоками
        List<File[]> workload = divideWorkload(files, processorCores);

        // Создаем пул потоков
        ExecutorService executor = Executors.newFixedThreadPool(processorCores);

        // Отправляем задачи на выполнение
        for (File[] batch : workload) {
            executor.execute(new ImgscalrResizer(batch, dstFolder, start));
        }

        // Завершаем пул потоков и ждем выполнения задач
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            System.out.println("Задачи прерваны");
            Thread.currentThread().interrupt();
        }

        System.out.println("Общая продолжительность: " + (System.currentTimeMillis() - start) + " мс");
    }

    private static List<File[]> divideWorkload(File[] files, int threadCount) {
        List<File[]> result = new ArrayList<>();

        // Вычисляем количество файлов на поток
        int filesPerThread = files.length / threadCount;

        // Обеспечиваем минимум один файл на поток
        if (filesPerThread == 0) {
            filesPerThread = 1;
            threadCount = Math.min(threadCount, files.length);
        }

        // Распределяем файлы равномерно
        for (int i = 0; i < threadCount; i++) {
            int startIndex = i * filesPerThread;
            int endIndex = (i == threadCount - 1) ? files.length : (i + 1) * filesPerThread;

            File[] threadFiles = new File[endIndex - startIndex];
            System.arraycopy(files, startIndex, threadFiles, 0, threadFiles.length);

            result.add(threadFiles);
        }

        return result;
    }
}
