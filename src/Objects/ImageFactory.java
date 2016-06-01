package Objects;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Bilel on 18-6-2015.
 * <p>
 * ImageFactory zal een aantal voorgekauwde plaatjes inladen voor:
 * <p>
 * - Dropable Objects
 * --> getImage(stage1)
 * --> getImage(toilet)
 * <p>
 * - Bezoeker Sets
 * --> createSkinProfileSet: Willekeurige Bezoeker set images teruggeven
 */
public class ImageFactory {

    public static String    stage1 = "/IMG/Stage.png",
                            toilet = "/IMG/Toilet.png",
                            pathSand = "/IMG/pad.jpg",
                            pathStones = "/IMG/pad1_40_40.jpg",
                            pathBlue = "/IMG/pad2_40_40.jpg",
                            pathCheckers = "/IMG/pad3_40_40.jpg",
                            pathStonesLarge = "/IMG/pad1.jpg",
                            pathBlueLarge = "/IMG/pad2.jpg",
                            pathCheckersLarge = "/IMG/pad3.jpg",
                            foodStand = "/IMG/BurgerStand.png",
                            char1back = "/IMG/character1back1.png",
                            char1left = "/IMG/character1left1.png",
                            char1right = "/IMG/character1right1.png",
                            char1front = "/IMG/character1front1.png",
                            char2left = "/IMG/character2left1.png",
                            char2right = "/IMG/character2right1.png",
                            char2front = "/IMG/character2front1.png",
                            char2back = "/IMG/character2back1.png",
                            char3left = "/IMG/character3left1.png",
                            char3right = "/IMG/character3right1.png",
                            char3front = "/IMG/character3front1.png",
                            char3back = "/IMG/character3back1.png",
                            char4left = "/IMG/character4left1.png",
                            char4right = "/IMG/character4right1.png",
                            char4front = "/IMG/character4front1.png",
                            char4back = "/IMG/character4back1.png",
                            backGrass = "/IMG/Backgrounds/Grass.jpg",
                            backRock = "/IMG/Backgrounds/Rock.jpg",
                            backSand = "/IMG/Backgrounds/Sand.jpg";


    public static BufferedImage getImage(String imageName) {
        try {
            return ImageIO.read(ImageFactory.class.getClass().getResource(imageName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Loads whole set of images instead of just one
    public static BufferedImage[] createSkinProfileSet() {
        BufferedImage[] buffImg = null;

        try {
            switch (new Random().nextInt(4) + 1) {

                case 1:
                    buffImg = new BufferedImage[]{
                            ImageIO.read(ImageFactory.class.getClass().getResource(char1left)),
                            ImageIO.read(ImageFactory.class.getClass().getResource(char1right)),
                            ImageIO.read(ImageFactory.class.getClass().getResource(char1front)),
                            ImageIO.read(ImageFactory.class.getClass().getResource(char1back))
                    };
                    break;
                case 2:
                    buffImg = new BufferedImage[]{
                            ImageIO.read(ImageFactory.class.getClass().getResource(char2left)),
                            ImageIO.read(ImageFactory.class.getClass().getResource(char2right)),
                            ImageIO.read(ImageFactory.class.getClass().getResource(char2front)),
                            ImageIO.read(ImageFactory.class.getClass().getResource(char2back))
                    };
                    break;
                case 3:
                    buffImg = new BufferedImage[]{
                            ImageIO.read(ImageFactory.class.getClass().getResource(char3left)),
                            ImageIO.read(ImageFactory.class.getClass().getResource(char3right)),
                            ImageIO.read(ImageFactory.class.getClass().getResource(char3front)),
                            ImageIO.read(ImageFactory.class.getClass().getResource(char3back))
                    };
                    break;
                case 4:
                    buffImg = new BufferedImage[]{
                            ImageIO.read(ImageFactory.class.getClass().getResource(char4left)),
                            ImageIO.read(ImageFactory.class.getClass().getResource(char4right)),
                            ImageIO.read(ImageFactory.class.getClass().getResource(char4front)),
                            ImageIO.read(ImageFactory.class.getClass().getResource(char4back))
                    };
                    break;
            }
        } catch (IOException i) {
            i.printStackTrace();
        }

        return buffImg;

    }
}
