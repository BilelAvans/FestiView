package People;

import Objects.People.NameTable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by Bilel on 16-2-2016.
 */
public class Visitor implements Serializable {

    public static Comparator<Visitor> sortByName = (o1, o2) -> (o1.getlName() + o1.getfName()).compareTo(o2.getlName() + o2.getlName());
    public static Comparator<Visitor> sortByToiletNeeds = (o1, o2) -> o1.getToiletNeeds() - o2.getToiletNeeds();
    public static Comparator<Visitor> sortByHunger = (o1, o2) -> o1.getHunger() - o2.getHunger();

    // Hungry every hour
    public static final int VISITOR_NEEDS_AT = 108000;

    private String  fName,
                    lName;

    private int     toiletNeeds = 0,
                    hunger      = 0;

    public Visitor(String fName, String lName) {
        this.fName = fName;
        this.lName = lName;
    }
    // Random generator
    public Visitor(){
        Random random = new Random();
        this.fName = NameTable.firstNames[random.nextInt(NameTable.firstNames.length - 1)];
        this.lName = NameTable.lastNames[random.nextInt(NameTable.lastNames.length - 1)];
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public void runOnce() {
        incrementToiletNeeds(1);
        incrementHungerNeeds(1);
    }

    public void incrementToiletNeeds(int amount) {
        if (toiletNeeds + amount < 0) {
            toiletNeeds = 0;
        } else if (toiletNeeds + amount > VISITOR_NEEDS_AT){
            toiletNeeds = VISITOR_NEEDS_AT;
        } else {
            toiletNeeds += amount;
        }
    }

    public void incrementHungerNeeds(int amount) {
        if (hunger + amount < 1) {
            hunger = 0;
        } else if (hunger + amount > VISITOR_NEEDS_AT){
            hunger = VISITOR_NEEDS_AT;
        } else {
            hunger += amount;
        }
    }

    public int getToiletNeeds() {
        return toiletNeeds;
    }

    public int getHunger() {
        return hunger;
    }

    public boolean fullBladder() {
        return toiletNeeds >= VISITOR_NEEDS_AT;
    }

    public boolean isHungry() {
        return hunger >= VISITOR_NEEDS_AT;
    }


}
