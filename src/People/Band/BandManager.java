package People.Band;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Bilel on 15-3-2016.
 */
public class BandManager implements Serializable {

    private ArrayList<Band> bands = new ArrayList<>();
    private ArrayList<BandMember> bandMembers = new ArrayList<>();

    public BandManager() {

    }

    public boolean addBand(Band b) {
        if (bandExists(b.getName())) // Does a band with this name already exist?
            return false;

        bands.add(b);
        return true;
    }

    public void removeBand(Band b) {
        bands.remove(b);
    }

    public boolean removeBand(String name) {
        try {
            bands.remove(bands.stream().filter(b -> b.getName().equals(name)).findFirst().get());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean bandExists(String name) {
        return bands.stream().anyMatch(b -> b.getName().equals(name));
    }

    public ArrayList<Band> getBands() {
        return bands;
    }

    public Band getBand(String bandName) {
        for (Band band : bands) {
            if (band.getName().equals(bandName))
                return band;
        }

        return null;
    }

    public boolean removeBandMember(String name) {
        if (bandExists(name)) {
            BandMember member = bandMembers.stream().filter(bm -> (bm.getfName() + " " + bm.getlName()).equals(name)).findFirst().get();
            removeFromAllBands(name);
            bandMembers.remove(member);
            return true;
        }
        return false;
    }

    public boolean removeFromAllBands(String name) {
        try {
            bands.stream().filter(b -> b.hasMember(name)).forEach(band -> band.removeMember(name));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public BandMember getBandMember(String bandMember) {
        for (BandMember bm : bandMembers) {
            if ((bm.getfName() + " " + bm.getlName()).equals(bandMember))
                return bm;
        }

        return null;
    }

    public ArrayList<BandMember> getBandMembers() {
        return bandMembers;
    }

    public void setBandMembers(ArrayList<BandMember> bandMembers) {
        this.bandMembers = bandMembers;
    }

    public void addBands(ArrayList<Band> bands) {

        bands.forEach(b -> {
            if (!bandExists(b.getName()))
                bands.add(b);
        });
    }

    public boolean addBandMemberToBand(String memberName, String bandName) {
        getBand(bandName).addMember(getBandMember(memberName));

        return true;
    }

    public boolean addBandMember(BandMember bM, String bandName) {
        bandMembers.add(bM);

        if (bandExists(bandName))
            getBand(bandName).addMember(bM);

        return false;

    }

    public void addBandMember(BandMember bm) {
        bandMembers.add(bm);
    }

    public void setBands(ArrayList<Band> bands) {
        this.bands = bands;
    }

    public Optional<Band> whichBandAmIIn(BandMember bm){
        for (Band b: bands){
            for (BandMember tempBm: b.getMembers()){
                if (tempBm.getlName().equals(bm.getfName()) && tempBm.getlName().equals(bm.getlName()))
                    return Optional.ofNullable(b);
            }
        }

        return Optional.ofNullable(null);
    }

    public void removeFromBand(BandMember bm){
        for (Band b: bands){
            for (BandMember tempBm: b.getMembers()){
                if (tempBm.getlName().equals(bm.getfName()) && tempBm.getlName().equals(bm.getlName()))
                    b.removeMember(tempBm);
            }
        }
    }
    // Bandname contains name of new band
    public void replaceBandMember(BandMember original, BandMember newBM, String bandName) {
        // Replace old?
        if (bandMembers.contains(original))
            bandMembers.set(bandMembers.indexOf(original), newBM);
        else
            bandMembers.add(newBM);

        // Does the new band exist? (prob does)
        Optional<Band> b = Optional.ofNullable(getBand(bandName));
        Optional<Band> originalBand =   bands.stream().filter(band -> band.getMembers().contains(original)).findAny();

        if (b.isPresent()) {
            if (originalBand.isPresent()){

                if (originalBand.get().equals(b.get())){
                    // Same band dont edit band, just the member
                    originalBand.get().replaceBandMember(original, newBM);
                } else {
                    originalBand.get().removeMember(original);
                }

            } else {
                b.get().addMember(newBM);
            }
        }
    }
}
