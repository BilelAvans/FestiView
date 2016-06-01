package GUIcomponents;

import Agenda.AgendaItem;
import Agenda.AgendaManager;
import Objects.Buildings.Stage;
import Objects.ObjectManager;
import People.PeopleManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by Bilel on 16-3-2016.
 */
public class AddAgendaItemGUI {
    private JButton createButton;
    private JSpinner startMinSpinner;
    private JSpinner startHourSpinner;
    private JTextField nameField;
    private JSpinner howLongHourSpinner;
    private JSpinner howLongMinSpinner;
    private JPanel mainPanel;
    private JComboBox comboBox1;
    private JSpinner startMonthSpinner;
    private JSpinner startYearSpinner;
    private JSpinner startDaySpinner;
    private JComboBox comboBox2;
    private JComboBox comboBox3;
    private JComboBox comboBox4;

    private PeopleManager pMan;
    private MainPage mPage;
    private AgendaManager aMan;
    private ObjectManager obMan;

    private boolean     hasDefaultStage,
                        editMode;

    private AgendaItem  editingItem;

    public AddAgendaItemGUI(MainPage mPage, PeopleManager pMan, AgendaManager aMan, ObjectManager obMan) {
        this.mPage = mPage;
        this.pMan = pMan;
        this.aMan = aMan;
        this.obMan = obMan;

        initSpinners();
        initStages();
        adjustSelectorsToAgendaTime();

        initListeners();

    }

    private void initSpinners() {

        startMinSpinner.setModel(new SpinnerNumberModel(1, 0, 59, 1));
        startHourSpinner.setModel(new SpinnerNumberModel(1, 0, 23, 1));
        howLongMinSpinner.setModel(new SpinnerNumberModel(1, 0, 59, 1));
        howLongHourSpinner.setModel(new SpinnerNumberModel(1, 0, 23, 1));

        if (editMode)
        {
            // Setup the spinners
            startDaySpinner.setModel(new SpinnerNumberModel(editingItem.getStarttime().getDayOfMonth(),
                    1,
                    31,
                    1));
            startMonthSpinner.setModel(new SpinnerNumberModel(editingItem.getStarttime().getMonthValue(),
                    1,
                    12,
                    1));
            startYearSpinner.setModel(new SpinnerNumberModel(editingItem.getStarttime().getYear(),
                    editingItem.getStarttime().getYear(),
                    editingItem.getStarttime().getYear() + 1,
                    1));
        }
        else {
            // Setup the spinners
            startDaySpinner.setModel(new SpinnerNumberModel(aMan.getCurrentAgenda().getStarttime().getDayOfMonth(),
                    1,
                    31,
                    1));
            startMonthSpinner.setModel(new SpinnerNumberModel(aMan.getCurrentAgenda().getStarttime().getMonthValue(),
                    1,
                    12,
                    1));
            startYearSpinner.setModel(new SpinnerNumberModel(aMan.getCurrentAgenda().getStarttime().getYear(),
                    aMan.getCurrentAgenda().getStarttime().getYear(),
                    aMan.getCurrentAgenda().getStarttime().getYear() + 1,
                    1));
        }

    }

    private void initStages() {
        comboBox1.removeAllItems();

        obMan.getStages().forEach(s -> {
            comboBox1.addItem(s.getID());
        });
    }

    private void initBands() {
        comboBox2.removeAllItems();
        pMan.getbMan().getBands().forEach(s -> {
            comboBox2.addItem(s.getName());
        });
    }

    public void resetView(){
        initStages();
        initBands();
        adjustSelectorsToAgendaTime();
    }

    public void initListeners() {
        createButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                createAgendaItem();
            }
        });
    }

    public void adjustSelectorsToAgendaTime(){
        startDaySpinner.setValue(aMan.getCurrentAgenda().getStarttime().getDayOfMonth());
        startMonthSpinner.setValue(aMan.getCurrentAgenda().getStarttime().getMonthValue());
        startYearSpinner.setValue(aMan.getCurrentAgenda().getStarttime().getYear());

        startMinSpinner.setValue(aMan.getCurrentAgenda().getStarttime().getMinute() + 1);
        startHourSpinner.setValue(aMan.getCurrentAgenda().getStarttime().getHour());
    }

    public JPanel getPane() {
        mainPanel.setVisible(true);
        return mainPanel;
    }

    private boolean createAgendaItem() {
        try {
            LocalDateTime currentSet = LocalDateTime.of(Integer.parseInt(startYearSpinner.getValue().toString()),
                    Integer.parseInt(startMonthSpinner.getValue().toString()),
                    Integer.parseInt(startDaySpinner.getValue().toString()),
                    Integer.parseInt(startHourSpinner.getValue().toString()),
                    Integer.parseInt(startMinSpinner.getValue().toString()),
                    0);
            // Item must start after agenda start time.
            if (currentSet.isAfter(aMan.getCurrentAgenda().getStarttime())) {
                Duration eventDuration = Duration.ofHours(Integer.parseInt(howLongHourSpinner.getValue().toString()))
                        .plus(Duration.ofMinutes(Integer.parseInt(howLongMinSpinner.getValue().toString())));

                Stage stage = obMan.getStage(comboBox1.getSelectedItem().toString()).get();

                AgendaItem event = new AgendaItem(nameField.getText(), currentSet, eventDuration,
                        stage,
                        pMan.getbMan().getBand(comboBox2.getSelectedItem().toString()));

                // New item
                if (!editMode)
                    aMan.getCurrentAgenda().add(event);
                else // edit item
                {
                    aMan.getCurrentAgenda().replaceEvent(editingItem, event);
                    editingItem = event;
                }

                if (hasDefaultStage) {
                    mPage.setCenterFrame("festipanel", null);
                    hasDefaultStage = false;
                } else {
                    mPage.setCenterFrame("showAgendaGUI", AgendaManagerGUI.refreshArgs.ALL);
                }
            } else {
                System.out.println("Nope, is before");
            }
        } catch (NullPointerException e) {
            System.out.println("Cannot add agendaitem");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // Call in GUI when selecting a stage to put an event on
    public void setDefaultStage(Object args){
        if (args instanceof Stage) {
            comboBox1.setSelectedItem(((Stage) args).getID());
            hasDefaultStage = true;
        }
    }

    public void setEditableEventInfo(AgendaItem event){
        this.editingItem = event;
        editMode = true;
        // Edit all gui components
        this.nameField.setText(event.getName());
        this.comboBox1.setSelectedItem(event.getEventLocationName());
        this.comboBox2.setSelectedItem(event.getPlayingBandName());

        startDaySpinner.setValue(event.getStarttime().getDayOfMonth());
        startMonthSpinner.setValue(event.getStarttime().getMonthValue());
        startYearSpinner.setValue(event.getStarttime().getYear());

        startMinSpinner.setValue(event.getStarttime().getMinute());
        startHourSpinner.setValue(event.getStarttime().getHour());

        howLongMinSpinner.setValue(event.getTimespan().getSeconds() / 60);
        howLongHourSpinner.setValue(event.getTimespan().getSeconds() % 60);

        this.createButton.setText("Edit");
    }

    private void backToNewEvent(){
        this.editingItem = null;
        editMode = false;

        this.createButton.setText("Create");
    }

    public void setObMan(ObjectManager obMan){
        this.obMan = obMan;
    }

    public void setaMan(AgendaManager aMan) {
        this.aMan = aMan;
    }
}


