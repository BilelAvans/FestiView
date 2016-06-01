package GUIcomponents;

import Agenda.*;
import Objects.Buildings.FoodStand;
import Objects.Buildings.Stage;
import Objects.Buildings.Toilet;
import Objects.ObjectManager;
import Objects.People.VisitorObject;
import Simulation.TimeLine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Bilel on 17-3-2016.
 */
public class SimulationPanel implements ActionListener, Observer {

    // Our premade timeline
    private TimeLine timeLine;
    private JPanel mainPanel;
    private JTable table1;
    private JTable table2;
    private JTable table3;
    private JLabel playButton;
    private JLabel pauseButton;
    private JLabel stopButton;
    private JLabel visAm;
    private JLabel runSince;
    private JLabel runFor;
    private JLabel currentTime;
    private JLabel stageAmountLabel;
    private JLabel stageUseAmountLabel;
    private JLabel toiletamountLabel;
    private JLabel toiletUsageLabel;
    private JLabel foodStandAmountLabel;
    private JLabel foodStandLabel;
    private JLabel timeLineEndsAtLabel;
    private JLabel totalEventsLabel;
    private JLabel eventsPassedAmountLabel;
    private JLabel statusLabel;
    private JSlider slider1;
    private JLabel timelineSpeedLabel;

    private ObjectManager obMan;

    private Timer timer = new Timer(1000/15, this);

    public SimulationPanel(ObjectManager obMan) {
        this.obMan = obMan;

        mainPanel.setPreferredSize(new Dimension(300, mainPanel.getHeight()));
        obMan.addObserver(this);
        initListeners();
        slider1.addChangeListener(c -> {
            if (slider1.getValue() == 0){
                timeLine.pause();
            } else {
                timeLine.setRunspeed(slider1.getValue());
                timelineSpeedLabel.setText(Integer.toString(slider1.getValue() * 2) + " Seconds in 1 'real' second");
                timeLine.getTimeLineThread().setDelay(1000 / slider1.getValue() * 2);
            }
        });
    }

    public void setTimeLine(TimeLine t){
        timeLine = t;
        timer.start();
    }

    public void stopTimeLine(){
        timer.stop();
        this.timeLine = null;
    }

    private void initListeners(){
        // Play button
        playButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (timeLine != null)
                    timeLine.start();
            }
        });


        pauseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (timeLine != null)
                    timeLine.pause();
            }
        });


        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (timeLine != null) {
                    timeLine.stop();
                    obMan.clearVisitors();
                }
            }
        });
    }
    public JPanel getPane(){
        return mainPanel;
    }

    public void resetAll(){
        resetButNotSoManyTimes();
        resetTimeLabels();
        resetObManInfo();
    }

    public void resetButNotSoManyTimes(){
        runSince.setText(timeLine.getStartTime().format(DataTable.NLTimeformat));
    }

    public void resetTimeLabels(){
        // Time
        currentTime.setText(timeLine.getCurrentTime().format(DataTable.NLTimeformat));
        runFor.setText(DataTable.DurationToString(timeLine.elapsedTime(), true));
        statusLabel.setText(timeLine.getStatus());

        resetCurrentEventsTable();

        currentTime.revalidate();
    }

    public void resetCurrentEventsTable(){
        // Reset tables
        table2.setModel(DataTable.getEventModel(timeLine.getAgenda().itemsNow(timeLine.getCurrentTime())));
    }

    // Reset all show info
    public void resetObManInfo(){
        Agenda agenda = timeLine.getAgenda();

        // Sim
        statusLabel.setText(timeLine.getStatus());
        visAm.setText(Integer.toString(obMan.getPeople().size()));

        // Building stats
        stageAmountLabel.setText(Integer.toString(timeLine.getObMan().getStages().size()));
        toiletamountLabel.setText(Integer.toString(timeLine.getObMan().getToilets().size()));
        foodStandAmountLabel.setText(Integer.toString(timeLine.getObMan().getFoodstands().size()));

        int stageUsage = 0, foodstandUsage = 0, toiletUsage = 0;
        // Find the amount of vistors visiting visitable objects
        for (VisitorObject obj: timeLine.getObMan().getPeople()){
            if (obj.getDestMan().hasCurrentLocation()){
                if (obj.getDestMan().currentLocation() instanceof Stage)
                    stageUsage++;
                else if (obj.getDestMan().currentLocation() instanceof FoodStand)
                    foodstandUsage++;
                else if (obj.getDestMan().currentLocation() instanceof Toilet)
                    toiletUsage++;
            }
        }

        stageUseAmountLabel.setText(Integer.toString(stageUsage));
        toiletUsageLabel.setText(Integer.toString(foodstandUsage));
        foodStandAmountLabel.setText(Integer.toString(toiletUsage));

        // Events

        totalEventsLabel.setText(Integer.toString(agenda.getPlanning().size()));
        eventsPassedAmountLabel.setText(Integer.toString(agenda.itemsBefore(timeLine.getCurrentTime()).size()));
        if (agenda.getPlanning().size() > 0)
            timeLineEndsAtLabel.setText(agenda.getLastItem().getStarttime().plus(agenda.getLastItem().getTimespan()).format(DataTable.NLDateFormat));
        else
            timeLineEndsAtLabel.setText("No Events Set");

    }

    public void setObMan(ObjectManager obMan){
        this.obMan = obMan;
    }


    @Override
    public void update(Observable o, Object arg) {
        resetObManInfo();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        resetTimeLabels();
    }
}
