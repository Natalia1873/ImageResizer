import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

class ImageResizer implements Runnable {
    private final File[] files;
    private final String dstFolder;
    private final long startTime;
    private final int newWidth = 300;

    public ImageResizer(File[] files, String dstFolder, long startTime) {
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

                int newHeight = (int) Math.round(
                        image.getHeight() / (image.getWidth() / (double) newWidth)
                );

                BufferedImage newImage = new BufferedImage(
                        newWidth, newHeight, BufferedImage.TYPE_INT_RGB
                );

                int widthStep = image.getWidth() / newWidth;
                int heightStep = image.getHeight() / newHeight;

                for (int x = 0; x < newWidth; x++) {
                    for (int y = 0; y < newHeight; y++) {
                        int rgb = image.getRGB(x * widthStep, y * heightStep);
                        newImage.setRGB(x, y, rgb);
                    }
                }

                File newFile = new File(dstFolder + "/" + file.getName());
                ImageIO.write(newImage, "jpg", newFile);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("Поток завершился за: " + (System.currentTimeMillis() - startTime) + " мс");
    }
}


