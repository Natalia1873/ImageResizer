import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

class ImgscalrResizer implements Runnable {
    private final File[] files;
    private final String dstFolder;
    private final long startTime;
    private final int newWidth = 300;

    public ImgscalrResizer(File[] files, String dstFolder, long startTime) {
        this.files = files;
        this.dstFolder = dstFolder;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        try {
            for (File file : files) {
                BufferedImage image = ImageIO.read(file);
                if (image == null) {
                    continue;
                }

                // Вычисляем новую высоту для сохранения пропорций
                int newHeight = (int) Math.round(
                        image.getHeight() / (image.getWidth() / (double) newWidth)
                );

                // Используем Imgscalr для изменения размера изображения с лучшим качеством
                BufferedImage newImage = Scalr.resize(image, Scalr.Method.QUALITY,
                        Scalr.Mode.FIT_EXACT,
                        newWidth, newHeight);

                File newFile = new File(dstFolder + "/" + file.getName());
                ImageIO.write(newImage, "jpg", newFile);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("Поток завершился за: " + (System.currentTimeMillis() - startTime) + " мс");
    }
}


