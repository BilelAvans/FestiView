package GUIcomponents;

import Agenda.AgendaItem;
import People.Band.Band;
import People.Band.BandMember;
import People.PeopleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by Bilel on 16-3-2016.
 */
public class AddBandMemberGUI {
    private JButton createButton;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField birthplaceField;
    private JTextField birthCountryField;
    private JSpinner birthYear;
    private JSpinner birthMonth;
    private JSpinner birthDay;
    private JPanel mainPanel;
    private JComboBox comboBox1;
    private JComboBox instrumentsList;

    private MainPage mPage;
    private PeopleManager pMan;

    private BandMember editingBandMember;
    private boolean editMode = true;

    public AddBandMemberGUI(PeopleManager pMan, MainPage mPage) {
        this.pMan = pMan;
        this.mPage = mPage;

        initComponents();
        createButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Is: " + addBandMemberThroughGUI());
            }
        });
    }

    private void initComponents() {
        birthDay.setModel(new SpinnerNumberModel(1, 1, 30, 1));
        birthMonth.setModel(new SpinnerNumberModel(1, 1, 12, 1));
        birthYear.setModel(new SpinnerNumberModel(1980, 1960, LocalDate.now().getYear(), 1));
        // Add bands to selector
        resetBandList();
        // Add instrument types to instrument list component
        for (BandMember.Instruments instru : BandMember.Instruments.values()) {
            instrumentsList.addItem(instru.toString());
        }
    }

    public void resetBandList(){
        comboBox1.removeAllItems();

        comboBox1.addItem("");

        pMan.getbMan().getBands().forEach(b -> comboBox1.addItem(b.getName()));
    }

    private boolean addBandMemberThroughGUI() {
        if (firstNameField.getText().length() < 1 ||
                lastNameField.getText().length() < 1 ||
                birthplaceField.getText().length() < 1 ||
                birthCountryField.getText().length() < 1)
            return false;

        BandMember bMember = new BandMember(firstNameField.getText(), lastNameField.getText(),
                                            birthplaceField.getText(), birthCountryField.getText(),
                                            LocalDate.of(Integer.parseInt(birthYear.getValue().toString()),
                                            Integer.parseInt(birthMonth.getValue().toString()),
                                            Integer.parseInt(birthDay.getValue().toString())),
                                            BandMember.Instruments.valueOf(instrumentsList.getSelectedItem().toString()));

        String bandName = comboBox1.getSelectedItem().toString();
        // Add if band was selected
        if (editMode){
            if (!bandName.equals(""))
                pMan.getbMan().replaceBandMember(editingBandMember, bMember, bandName);
            else
                pMan.getbMan().replaceBandMember(editingBandMember, bMember, null);

            editMode = false;
        } else {
            if (!bandName.equals(""))
                pMan.getbMan().addBandMember(bMember, bandName);
            else
                pMan.getbMan().addBandMember(bMember);
        }

        mPage.setCenterFrame("showAgendaGUI", AgendaManagerGUI.refreshArgs.BANDMEMBERS);

        return true;
    }

    public void setEditableBandMemberInfo(BandMember bm){
        this.editingBandMember = bm;
        editMode = true;

        // Edit all gui components
        firstNameField.setText(bm.getfName());
        lastNameField.setText(bm.getlName());
        birthplaceField.setText(bm.getBirthplace());
        birthCountryField.setText(bm.getBirthcountry());
        birthDay.setValue(bm.getBirthDate().getDayOfYear());
        birthMonth.setValue(bm.getBirthDate().getDayOfYear());
        birthYear.setValue(bm.getBirthDate().getYear());
        instrumentsList.setSelectedItem(bm.getInstrument().toString());
        Optional<Band> bandCurrentlyIn = pMan.getbMan().whichBandAmIIn(bm);
        // In a band?
        if (bandCurrentlyIn.isPresent())
            comboBox1.setSelectedItem(bandCurrentlyIn.get());
        else
            comboBox1.setSelectedItem("");

        this.createButton.setText("Edit");
    }

    public JPanel getPane() {
        mainPanel.setVisible(true);
        return mainPanel;
    }


}
