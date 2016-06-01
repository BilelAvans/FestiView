package Agenda;

import Objects.Buildings.Stage;
import Objects.ObjectManager;
import People.Band.Band;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Bilel on 15-2-2016.
 */
public class Agenda implements Serializable {

    private String name;
    private ArrayList<AgendaItem> planning = new ArrayList<>();
    private LocalDateTime starttime;

    public Agenda(String name, LocalDateTime starttime) {
        this.name = name;
        this.starttime = starttime;
    }

    public boolean add(AgendaItem item) {
        if (nameExist(item.getName())) {
            return false;
        } else {
            planning.add(item);
            return true;
        }
    }

    public boolean isBandAlreadyPlaying(Band b, LocalDateTime at){
        for (AgendaItem event: planning){
            if (event.getPlayingBandName().equals((b.getName())))
                if (event.isBetween(at))
                    return true;
        }

        return false;
    }

    public void setName(String name){
        this.name = name;
    }

    public boolean removeAgendaItem(String name) {
        planning.remove(planning.stream().filter(ai -> ai.getName().equals(name)).findFirst().get());

        return true;
    }

    public Optional<AgendaItem> findEvent(String name){
        return planning.stream().filter(e -> e.getName().equals(name)).findFirst();
    }

    public ArrayList<AgendaItem> itemsBefore(LocalDateTime time) {
        return planning.stream().filter(item -> item.getStarttime().plus(item.getTimespan()).isBefore(time))
                                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<AgendaItem> itemsNow(LocalDateTime time) {
        return planning.stream().filter(item -> item.getStarttime().isBefore(time) &&
                                                item.getStarttime().plus(item.getTimespan()).isAfter(time))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<AgendaItem> itemsAfter(LocalDateTime time) {
        return planning.stream().filter(item -> item.getStarttime().isAfter(time))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String getName() {
        return name;
    }

    public ArrayList<AgendaItem> getPlanning() {
        return planning;
    }

    public AgendaItem getLastItem(){
        if (planning.size() > 0) {
            planning.sort(AgendaItem.sortByNameComparator);
            return planning.get(planning.size() - 1);
        } else
        {
            return null;
        }
    }

    public LocalDateTime getStarttime() {
        return starttime;
    }

    public Duration getDuration() {
        return Duration.ofHours(2);
    }

    public boolean nameExist(String name) {
        if (planning.stream().filter(item -> item.getName().equals(name)).count() > 0)
            return true;

        return false;
    }

    public void replaceEvent(AgendaItem oldEvent, AgendaItem newEvent){
        planning.set(planning.indexOf(oldEvent), newEvent);
    }

    public void replaceEventsWithValidEvents(ObjectManager obMan){
        for (AgendaItem item: planning){
            for (Stage st: obMan.getStages()){
                if (item.getEventLocation().getID().equals(st.getID())) {
                    item.setEventLocation(st);
                }
            }
        }
    }
}
