package GUIcomponents;

import Agenda.Agenda;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;

/**
 * Created by Bilel on 24-2-2016.
 */
public class AddAgendaGUI {
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JSpinner spinner3;
    private JSpinner spinner4;
    private JSpinner spinner5;
    private JButton createButton;
    private JTextArea textArea1;
    private JPanel mainpanel;

    // Our main function of this gui is to create an agenda
    private Agenda agenda;

    private MainPage mPage;

    private boolean editMode = false;
    private Agenda  editAgenda;

    public AddAgendaGUI(MainPage mPage) {
        this.mPage = mPage;

        initListeners();
    }

    public void initListeners() {
        createButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isValidTime() == ValidTime.YES) {
                    // Check if name is valid
                    if (isValidName(textArea1.getText()) == ValidName.YES) {
                        // We can create the agenda
                        System.out.println("Agenda created");
                        Agenda newAgenda = new Agenda(
                                textArea1.getText(),
                                LocalDateTime.of(Integer.parseInt(spinner3.getValue().toString()),
                                Integer.parseInt(spinner2.getValue().toString()),
                                Integer.parseInt(spinner1.getValue().toString()),
                                Integer.parseInt(spinner4.getValue().toString()),
                                Integer.parseInt(spinner5.getValue().toString()),
                                0));
                        if (!editMode) {
                            mPage.getAMan().addAgenda(newAgenda);
                            mPage.setAgenda(newAgenda);
                        } else {
                            mPage.getAMan().replaceAgenda(editAgenda, newAgenda);

                            createButton.setText("Create");
                            editMode = false;
                        }

                        // Nav to agenda page
                        mPage.getMainNavBar().setAgendasAtSelectionMenu();
                        mPage.setCenterFrame("showAgendaGUI", AgendaManagerGUI.refreshArgs.AGENDA);
                    }
                }
            }
        });
    }

    public void setEditable(Agenda agenda){
        editAgenda = agenda;

        textArea1.setText(agenda.getName());
        spinner1.setValue(agenda.getStarttime().getDayOfMonth());
        spinner2.setValue(agenda.getStarttime().getMonthValue());
        spinner3.setValue(agenda.getStarttime().getYear());
        spinner4.setValue(agenda.getStarttime().getHour());
        spinner5.setValue(agenda.getStarttime().getMinute());
        createButton.setText("Edit");

        editMode = true;


    }

    public void initComponents() {

    }

    public JPanel getPane() {
        mainpanel.setVisible(true);
        return mainpanel;
    }

    public ValidName isValidName(String name) {
        if (name.length() > 6)
            return ValidName.YES;

        return ValidName.INCORRECT_NAME;
    }

    ;

    public ValidTime isValidTime() {
        if (Integer.parseInt(spinner3.getValue().toString()) >= agenda.getStarttime().getYear() &&
                isBetween(Integer.parseInt(spinner2.getValue().toString()), 1, 12) &&
                isBetween(Integer.parseInt(spinner1.getValue().toString()), 1, 30) &&
                isBetween(Integer.parseInt(spinner4.getValue().toString()), 0, 23) &&
                isBetween(Integer.parseInt(spinner5.getValue().toString()), 0, 59))
            return ValidTime.YES;

        return ValidTime.INCORRECT_DATE;
    }

    public boolean isBetween(int number, int min, int max) {
        if (number >= min && number <= max)
            return true;

        return false;
    }

    enum ValidName {
        YES,
        INCORRECT_NAME
    }

    enum ValidTime {
        YES,
        INCORRECT_DATE
    }
}


