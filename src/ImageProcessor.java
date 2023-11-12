import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessor {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Invalid arguments. Usage: yourprogram filename squareSize (S or M)");
            return;
        }

        String fileName = args[0];
        int squareSize;
        try {
            squareSize = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid square size. Please provide a number.");
            return;
        }
        String operationMode = args[2];

        BufferedImage image = loadImage(fileName);
        if (image != null) {
            if (operationMode.equals("S")) {
                processAndDisplayImage(image, squareSize);
            } else if (operationMode.equals("M")) {
                processAndDisplayImageMultithread(image, squareSize);
            } else {
                System.out.println("Invalid operation mode. Use S for single-threaded or M for multi-threaded.");
            }
        }
    }

    public static BufferedImage loadImage(String fileName) {
        try {
            File file = new File(fileName);
            BufferedImage image = ImageIO.read(file);

            if (image == null) {
                System.out.println("Could not read the image");
                return null;
            }

            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static BufferedImage scaleImage(BufferedImage image, int maxWidth, int maxHeight) {
        double scale = Math.min((double) maxWidth / image.getWidth(), (double) maxHeight / image.getHeight());
        int newWidth = (int) (image.getWidth() * scale);
        int newHeight = (int) (image.getHeight() * scale);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
        resizedImage.getGraphics().drawImage(image.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH), 0, 0, null);
        return resizedImage;
    }

    public static void processAndDisplayImage(BufferedImage image, int squareSize) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane();
        JLabel label = new JLabel(new ImageIcon(image));
        scrollPane.setViewportView(label);

        frame.add(scrollPane);
        frame.pack();
        frame.setVisible(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.8);
        int height = (int) (screenSize.height * 0.8);
        frame.setSize(width, height);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BufferedImage updatedImage = deepCopy(image);

        for (int y = 0; y < image.getHeight(); y += squareSize) {
            for (int x = 0; x < image.getWidth(); x += squareSize) {
                int avgColor = getAverageColor(image, x, y, squareSize);
                updateSquare(updatedImage, x, y, squareSize, avgColor);
                label.setIcon(new ImageIcon(updatedImage));
                frame.revalidate();
                frame.repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            File outputfile = new File("result.jpg");
            ImageIO.write(updatedImage, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        copy.getGraphics().drawImage(bi, 0, 0, null);
        return copy;
    }

    public static int getAverageColor(BufferedImage image, int x, int y, int squareSize) {
        int totalRed = 0, totalGreen = 0, totalBlue = 0, pixels = 0;

        for (int j = y; j < y + squareSize && j < image.getHeight(); j++) {
            for (int i = x; i < x + squareSize && i < image.getWidth(); i++) {
                int rgb = image.getRGB(i, j);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = (rgb) & 0xFF;

                totalRed += red;
                totalGreen += green;
                totalBlue += blue;
                pixels++;
            }
        }

        if (pixels == 0) pixels = 1;
        int avgRed = totalRed / pixels;
        int avgGreen = totalGreen / pixels;
        int avgBlue = totalBlue / pixels;

        return (avgRed << 16) | (avgGreen << 8) | avgBlue;
    }

    public static void updateSquare(BufferedImage image, int x, int y, int squareSize, int avgColor) {
        for (int j = y; j < y + squareSize && j < image.getHeight(); j++) {
            for (int i = x; i < x + squareSize && i < image.getWidth(); i++) {
                image.setRGB(i, j, avgColor);
            }
        }
    }

    public static void processAndDisplayImageMultithread(BufferedImage image, int squareSize) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane();
        JLabel label = new JLabel(new ImageIcon(image));
        scrollPane.setViewportView(label);

        frame.add(scrollPane);
        frame.pack();
        frame.setVisible(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.8);
        int height = (int) (screenSize.height * 0.8);
        frame.setSize(width, height);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BufferedImage updatedImage = deepCopy(image);
        int numThreads = Runtime.getRuntime().availableProcessors(); // Number of threads based on processor count
        int rowsPerThread = image.getHeight() / numThreads;

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            int startRow = i * rowsPerThread;
            int endRow = (i == numThreads - 1) ? image.getHeight() : (i + 1) * rowsPerThread;

            Thread thread = new Thread(() -> {
                for (int y = startRow; y < endRow; y += squareSize) {
                    for (int x = 0; x < image.getWidth(); x += squareSize) {
                        int avgColor = getAverageColor(image, x, y, squareSize);
                        updateSquare(updatedImage, x, y, squareSize, avgColor);
                        label.setIcon(new ImageIcon(updatedImage));
                        frame.revalidate();
                        frame.repaint();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            threads.add(thread);
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            File outputfile = new File("result.jpg");
            ImageIO.write(updatedImage, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
