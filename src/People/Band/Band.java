package People.Band;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by Bilel on 16-2-2016.
 */
public class Band implements Serializable {

    // Band name
    private String name;
    // Genre
    private Genres genre;
    // Founded in (year)
    private int foundedInYear;
    // Al our members
    private ArrayList<BandMember> members = new ArrayList<>();

    public Band(String name, int foundedInYear, Genres genre) {
        this.name = name;
        this.genre = genre;
        this.foundedInYear = foundedInYear;
    }

    public Band(String name, int foundedInYear, Genres genre, ArrayList<BandMember> bms) {
        this(name, foundedInYear, genre);
        members = bms;
    }

    // Standard band
    public Band(String name, ArrayList<BandMember> members) {
        this.name = name;
        this.members = members;
    }

    public boolean addMember(BandMember bm) {
        // Does it exist yet
        if (members.stream().anyMatch(b -> b.getlName().equals(bm.getlName()) &&
                b.getfName().equals(bm.getfName()))) {
            return false;
        }

        members.add(bm);
        return true;
    }

    public boolean removeMember(String name) {
        // Search for the member
        BandMember member = members.stream().filter(bm -> bm.getlName().equals(name)).findFirst().get();
        if (member != null) {
            members.remove(member);
            return true;
        }

        return false;
    }

    public void removeMember(BandMember bm){
        members.remove(bm);
    }

    public void replaceBandMember(BandMember original, BandMember newBM){
        try {
            members.set(members.indexOf(original), newBM);
        } catch (NullPointerException ex){
            // Do nothing
        }
    }

    public boolean hasMember(String name) {
        return members.stream().anyMatch(bm -> name.equals(bm.getfName() + " " + bm.getlName()));
    }

    public int getFoundedInYear() {
        return foundedInYear;
    }

    public String getName() {
        return name;
    }

    public ArrayList<BandMember> getMembers() {
        return members;
    }

    public Genres getGenre() {
        return genre;
    }

    public enum Genres {
        POP,
        ROCK,
        DRUM_N_BASS,
        DUBSTEP,
        HARDSTYLE,
        CLASSIC,
        REGGEA
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenre(Genres genre) {
        this.genre = genre;
    }

    public void setFoundedInYear(int foundedInYear) {
        this.foundedInYear = foundedInYear;
    }

    public void setMembers(ArrayList<BandMember> members) {
        this.members = members;
    }
}
