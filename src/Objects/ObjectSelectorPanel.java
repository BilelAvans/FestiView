package Objects;

import Objects.Buildings.Paths.Path;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * Created by Bilel on 27-2-2016.
 */
public class ObjectSelectorPanel extends JPanel {

    public static   String              selected    =   "";
    public static   Path.PATH_THEMES    theme       = Path.PATH_THEMES.SAND;

    // Object knoppen
    private ImageIcon   stage           =   new ImageIcon(ImageFactory.getImage(ImageFactory.stage1).getScaledInstance(80, 70, BufferedImage.SCALE_SMOOTH)),
                        toilet          =   new ImageIcon(ImageFactory.getImage(ImageFactory.toilet).getScaledInstance(80, 70, BufferedImage.SCALE_SMOOTH)),
                        foodStand       =   new ImageIcon(ImageFactory.getImage(ImageFactory.foodStand).getScaledInstance(80, 70, BufferedImage.SCALE_SMOOTH)),
                        pathSand            =   new ImageIcon(ImageFactory.getImage(ImageFactory.pathSand).getScaledInstance(80, 70, BufferedImage.SCALE_SMOOTH)),
                        pathStones            =   new ImageIcon(ImageFactory.getImage(ImageFactory.pathStonesLarge).getScaledInstance(80, 70, BufferedImage.SCALE_SMOOTH)),
                        pathBlue            =   new ImageIcon(ImageFactory.getImage(ImageFactory.pathBlueLarge).getScaledInstance(80, 70, BufferedImage.SCALE_SMOOTH)),
                        pathCheckers            =   new ImageIcon(ImageFactory.getImage(ImageFactory.pathCheckersLarge).getScaledInstance(80, 70, BufferedImage.SCALE_SMOOTH));
                        //grassBackground =   new ImageIcon(ImageFactory.getImage(ImageFactory.backGrass).getScaledInstance(40, 40, BufferedImage.SCALE_SMOOTH)),
                        //rockBackground  =   new ImageIcon(ImageFactory.getImage(ImageFactory.backRock).getScaledInstance(40, 40, BufferedImage.SCALE_SMOOTH)),
                        //sandBackground  =   new ImageIcon(ImageFactory.getImage(ImageFactory.backSand).getScaledInstance(40, 40, BufferedImage.SCALE_SMOOTH));

    private JButton     stageButton     =   new JButton(stage),
                        toiletButton    =   new JButton(toilet),
                        foodStandButton =   new JButton(foodStand),
                        pathButton      =   new JButton(pathSand),
                        path1Button      =   new JButton(pathStones),
                        path2Button      =   new JButton(pathBlue),
                        path3Button      =   new JButton(pathCheckers);
                        //grassButton     =   new JButton(grassBackground),
                        //rockButton      =   new JButton(rockBackground),
                        //sandButton      =   new JButton(sandBackground);


    private FestiFrame  fFrame;

    public ObjectSelectorPanel(FestiFrame fFrame) {
        this.fFrame = fFrame;
        initComponents();
        initListeners();
    }

    public void initComponents() {
        this.setPreferredSize(new Dimension(150, 800));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(stageButton);
        add(toiletButton);
        add(foodStandButton);
        add(pathButton);
        add(path1Button);
        add(path2Button);
        add(path3Button);
        /*
        // Background buttons
        add(grassButton);
        add(rockButton);
        add(sandButton);
        */

        setFocusable(false);
        transferFocus();

    }

    private void initListeners() {
        initStageButtonListener();
        initFoodStandButtonListener();
        initToiletButtonListener();
        initPathButtonListener();
        initPath1ButtonListener();
        initPath2ButtonListener();
        initPath3ButtonListener();
        /*
        initGrassBackgroundButtonListener();
        initRockButtonButtonListener();
        initSandButtonButtonListener();
        */
    }

    private void initFoodStandButtonListener() {
        foodStandButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selected = "FoodStand";
            }
        });

        foodStandButton.setFocusable(false);
        foodStandButton.transferFocus();
    }

    private void initStageButtonListener() {
        stageButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selected = "Stage";
            }
        });
        stageButton.setFocusable(false);
        stageButton.transferFocus();
    }

    private void initToiletButtonListener() {
        toiletButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selected = "Toilet";
            }
        });

        toiletButton.setFocusable(false);
        toiletButton.transferFocus();
    }

    private void initPath1ButtonListener() {

        path1Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selected = "Path";
                theme    = Path.PATH_THEMES.STONES;
            }
        });
        pathButton.setFocusable(false);
        pathButton.transferFocus();
    }
    private void initPath2ButtonListener() {

        path2Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selected = "Path";
                theme    = Path.PATH_THEMES.BLUE;
            }
        });
        pathButton.setFocusable(false);
        pathButton.transferFocus();
    }
    private void initPath3ButtonListener() {

        path3Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selected = "Path";
                theme    = Path.PATH_THEMES.CHECKERS;
            }
        });
        pathButton.setFocusable(false);
        pathButton.transferFocus();
    }
    private void initPathButtonListener() {

        pathButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selected = "Path";
                theme    = Path.PATH_THEMES.SAND;
            }
        });
        pathButton.setFocusable(false);
        pathButton.transferFocus();
    }
/*
    private void initGrassBackgroundButtonListener() {

        grassButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                background = "Path";
            }
        });
        grassButton.setFocusable(false);
        grassButton.transferFocus();
    }

    private void initRockButtonButtonListener() {

        rockButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                background = "Path";
            }
        });
        rockButton.setFocusable(false);
        rockButton.transferFocus();
    }

    private void initSandButtonButtonListener() {

        sandButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                background = "Path";
            }
        });
        sandButton.setFocusable(false);
        sandButton.transferFocus();
    }
    */
}
