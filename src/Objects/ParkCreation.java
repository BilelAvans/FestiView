package Objects;

import javax.swing.*;

/**
 * We will use this to design our park
 */
public class ParkCreation extends JPanel {

    public ObjectManager obMan;
    // Park name
    private String name;

    public ParkCreation(String name, ObjectManager obMan) {
        this.name = name;
        this.obMan = obMan;
    }

    public void setObMan(ObjectManager obMan){
        this.obMan = obMan;
    }
}
