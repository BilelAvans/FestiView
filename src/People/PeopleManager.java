package People;

import Objects.ObjectManager;
import Objects.People.VisitorObject;
import People.Band.Band;
import People.Band.Band.Genres;
import People.Band.BandManager;
import People.Band.BandMember;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bilel on 16-2-2016.
 */
public class PeopleManager implements Serializable {

    // Our visitors
    private List<Visitor> people = Collections.synchronizedList(new ArrayList<>());
    // Our Bands
    private BandManager bMan = new BandManager();

    private ObjectManager obMan;

    public PeopleManager(ObjectManager obMan) {
        this.obMan = obMan;
    }

    public List<Visitor> getPeople() {
        return people;
    }

    public void setPeople(ArrayList<Visitor> people) {
        this.people = people;
    }

    public void setObMan(ObjectManager obMan) {
        this.obMan = obMan;
    }

    public void addVisitor(Visitor v) {
        people.add(v);
        obMan.addVisitor(new VisitorObject(50, 50, v, obMan));
    }

    public ArrayList<Visitor> getPeopleData(PeopleDataSortType sortType) {
        // Fetch all data and result
        ArrayList<Visitor> visitors = new ArrayList<>();
        // Fill with visitor data
        people.forEach(p -> visitors.add(p));

        // If sortType was added, sort the returned list
        switch (sortType) {
            case BY_NAME:
                visitors.sort(Visitor.sortByName);
            case BY_HUNGER:
                visitors.sort(Visitor.sortByHunger);
            case BY_TOILETNEEDS:
                visitors.sort(Visitor.sortByToiletNeeds);
        }

        return visitors;
    }

    public BandManager getbMan() {
        return bMan;
    }

    public void setBMan(BandManager bM) {
        this.bMan = bM;
    }

    enum PeopleDataSortType {
        BY_NAME,
        BY_TOILETNEEDS,
        BY_HUNGER
    }

    public ObjectManager getObMan() {
        return obMan;
    }
}
