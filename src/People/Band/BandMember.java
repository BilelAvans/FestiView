package People.Band;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

/**
 * Created by Bilel on 16-2-2016.
 */
public class BandMember implements Serializable {


    // Personal information
    private String fName,
            lName,
            birthplace,
            birthcountry;
    private Instruments Instrument;
    private LocalDate birthDate;

    public BandMember(String fName, String lName, String birthplace, String birthcountry, LocalDate birthDate, Instruments Instrument) {
        this.fName = fName;
        this.lName = lName;
        this.birthplace = birthplace;
        this.birthcountry = birthcountry;
        this.birthDate = birthDate;
        this.Instrument = Instrument;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public String getBirthcountry() {
        return birthcountry;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public Instruments getInstrument() {
        return Instrument;
    }

    public enum Instruments {
        VOCAL,
        GUITAR,
        BASSGUITAR,
        PIANO,
        VIOLIN,
        DRUMMER
    }
}
