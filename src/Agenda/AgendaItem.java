package Agenda;

import Objects.Buildings.Stage;
import People.Band.Band;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Created by Bilel on 15-2-2016.
 */
public class AgendaItem implements Serializable {

    private String name;

    private LocalDateTime starttime;

    private Duration timespan;
    private Stage eventLocation;
    private Band playingBand;

    public AgendaItem(String name, LocalDateTime starttime, Duration period, Stage eventLocation, Band band) {
        this.name = name;
        this.starttime = starttime;
        this.timespan = period;
        this.eventLocation = eventLocation;
        this.playingBand = band;
    }

    public LocalDateTime getStarttime() {
        return starttime;
    }

    public Duration getTimespan() {
        return timespan;
    }

    public String getName() {
        return name;
    }

    public boolean isBetween(LocalDateTime time) {
        if (time.isAfter(starttime) && time.isBefore(starttime.plus(timespan)))
            return true;

        return false;
    }

    public Stage getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(Stage st){
        this.eventLocation = st;
    }

    public String getEventLocationName() {
        if (eventLocation != null)
            return eventLocation.getID();

        return "";
    }

    public String getPlayingBandName() {
        if (playingBand != null)
            return playingBand.getName();

        return "";
    }

    public static transient Comparator<AgendaItem> sortByTime = (AgendaItem o1, AgendaItem o2) -> {
        return o1.getStarttime().compareTo(o2.getStarttime());
    };
    public transient static Comparator<AgendaItem> sortByNameComparator = (o1, o2) -> {
        return o1.getName().compareTo(o2.getName());
    };

}
