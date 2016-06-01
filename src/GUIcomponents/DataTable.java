package GUIcomponents;

import Agenda.Agenda;
import Objects.Buildings.Stage;
import Objects.People.VisitorObject;
import People.Band.Band;
import People.Band.BandMember;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import Agenda.AgendaItem;
import People.Visitor;

/**
 * Created by Bilel on 19-2-2016.
 */
public class DataTable {

    public static DateTimeFormatter NLTimeformat = DateTimeFormatter.ofPattern("hh:mm:ss");
    public static DateTimeFormatter NLTimeformatNoSeconds = DateTimeFormatter.ofPattern("hh:mm");
    public static DateTimeFormatter NLDateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private String name;
    private LocalDateTime starttime;
    private Duration timespan;
    private Stage eventLocation;
    private Band playingBand;

    public static DefaultTableModel getBandModel(ArrayList<Band> bands) {
        // Agenda table header
        String[] columnNames = new String[]{"Name", "Size", "genre", "Founded in"};

        Object[][] columnData = bands.stream().map(band ->
                new Object[]{
                        band.getName(),
                        band.getMembers().size(),
                        band.getGenre(),
                        band.getFoundedInYear()
                }
        ).toArray(Object[][]::new);

        return new DefaultTableModel(columnData, columnNames);

    }

    public static DefaultTableModel getBandMemberModel(ArrayList<BandMember> members) {
        // Agenda table header
        String[] columnNames = new String[]{"Name", "Role", "Age", "Born in"};

        Object[][] columnData = members.stream().map(member ->
                new Object[]{
                        member.getfName() + " "+ member.getlName(),
                        member.getInstrument(),
                        member.getAge() + " ("+ member.getBirthDate().format(NLDateFormat)+ ")" ,
                        member.getBirthplace() + ", "+
                        member.getBirthcountry()
                }
        ).toArray(Object[][]::new);

        return new DefaultTableModel(columnData, columnNames);
    }

    public static DefaultTableModel getAgendaModel(Agenda agenda) {
        return getEventModel(agenda.getPlanning());
    }

    public static DefaultTableModel getEventModel(ArrayList<AgendaItem> items){
        // Agenda table header
        String[] columnNames = {"Name", "Band", "Location", "Start", "End", "Duration",};
        // Data
        Object[][] data = items.stream().map(item ->
                new Object[]{
                        item.getName(),
                        item.getPlayingBandName(),
                        item.getEventLocationName(),
                        item.getStarttime().format(NLTimeformatNoSeconds),
                        item.getStarttime().plus(item.getTimespan()).format(NLTimeformatNoSeconds),
                        DurationToString(item.getTimespan(), false)
                })
                .toArray(Object[][]::new);

        DefaultTableModel model = new DefaultTableModel(data, columnNames);

        return model;
    }

    public static DefaultTableModel getVisitorModel(ArrayList<Visitor> items){
        String[] columnNames = { "Name", "Toilet", "Hunger" };
        Object[][] data = items.stream().map(v ->
            new Object[]{
                    v.getlName() + v.getlName(),
                    v.getToiletNeeds(),
                    v.getHunger()
            }).toArray(Object[][]::new);

        return new DefaultTableModel(data, columnNames);
    }

    public static DefaultTableModel getVisitorObjectModel(ArrayList<VisitorObject> items){
        String[] columnNames = { "Name", "Currently", "Location",  "Toilet", "Hunger" };
        Object[][] data = items.stream().map(v ->
                new Object[]{
                        v.getID(),
                        v.getvState(),
                        v.getDestMan().currentLocation().getID(),
                        v.getVisitorData().getToiletNeeds(),
                        v.getVisitorData().getHunger(),
                }).toArray(Object[][]::new);

        return new DefaultTableModel(data, columnNames);
    }


    public static String DurationToString(Duration d, boolean secondsOn) {
        if (secondsOn)
            return d.toHours() + "H " + d.toMinutes() % 60 + "M " + d.toMinutes() % 60 + "S ";
        else
            return d.toHours() + "H " + d.toMinutes() % 60 + "M ";
    }
}
