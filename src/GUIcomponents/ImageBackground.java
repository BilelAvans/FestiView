package GUIcomponents;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Bilel on 30-3-2016.
 */
public class ImageBackground extends JPanel {

    private BufferedImage image;

    public ImageBackground(BufferedImage image){
        this.image = image;

        setVisible(true);
        setPreferredSize(new Dimension(600, 600));
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(image, 0, 0, null);

    }

}
