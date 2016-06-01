package GUIcomponents;

import People.Band.Band;
import People.Band.Band.Genres;
import People.Band.BandMember;
import People.PeopleManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by Bilel on 15-3-2016.
 */
public class AddBandGUI {
    private JButton createButton;
    private JTextField bandNameField;
    private JComboBox youndYearComboBox;
    private JList bandMemberList;
    private JPanel mainPanel;
    private JComboBox genreComboBox;
    private JScrollBar bmScroller;
    private JList bmlist;

    private MainPage mPage;

    private PeopleManager pMan;

    private boolean editMode;
    private Band    editableBand;

    public AddBandGUI(PeopleManager pMan, MainPage mPage) {
        this.pMan = pMan;
        this.mPage = mPage;

        initListeners();
        initComponents();
        addBandMembers();
    }

    public void initListeners() {
        createButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                addBandThroughGUI(); // boolean return possible.
            }
        });
    }

    private void initComponents() {
        //bmScroller.

        // Add all genres to selection
        for (Genres genre : Genres.values()) {
            genreComboBox.addItem(genre.name());
        }
        // Add years to founding years selection
        short endYear = (short) LocalDate.now().getYear();
        for (short counter = 1960; counter < endYear; counter++) {
            youndYearComboBox.addItem(counter);
        }
    }

    private boolean addBandThroughGUI() {
        // Check if all required fields are filled in
        if (bandNameField.getText().length() < 1 || pMan.getbMan().bandExists(bandNameField.getText()) ||
                genreComboBox.getSelectedObjects().length < 1 ||
                youndYearComboBox.getSelectedIndex() < 0 )
            return false; // Must pass all criterium, else return false

        if (!editMode) {
            ArrayList<BandMember> bms = new ArrayList<>();
            for (Object str : bmlist.getSelectedValuesList()) {
                bms.add(pMan.getbMan().getBandMember((String) str));
            }

            Band b = new Band(bandNameField.getText(),
                    Short.toUnsignedInt((short) youndYearComboBox.getSelectedItem()),
                    Band.Genres.valueOf(genreComboBox.getSelectedItem().toString()),
                    bms);

            pMan.getbMan().addBand(b);
        } else {
            editableBand.setFoundedInYear(Short.toUnsignedInt((short) youndYearComboBox.getSelectedItem()));
            editableBand.setName(bandNameField.getText());
            editableBand.setGenre(Band.Genres.valueOf(genreComboBox.getSelectedItem().toString()));
            createButton.setText("Create");
            editMode = false;
            //pMan.getbMan().replaceBand(editableBand, b);
        }

        mPage.setCenterFrame("showAgendaGUI", AgendaManagerGUI.refreshArgs.BANDS);

        return true;
    } 

    public JPanel getPane() {
        mainPanel.setVisible(true);
        return mainPanel;
    }

    public void setEditableBand(Band b){
        editMode = true;
        editableBand = b;

        bandNameField.setText(b.getName());
        genreComboBox.setSelectedItem(b.getGenre().toString());
        youndYearComboBox.setSelectedItem(b.getFoundedInYear());

        // Clear and add current members
        bmlist.clearSelection();
        b.getMembers().forEach(bm -> ((DefaultListModel)bmlist.getModel()).addElement(bm.getfName() + " " + bm.getlName()));

        createButton.setText("Edit");
        //* something with member list here

        /////
    }

    public void addBandMembers() {
        bmlist.removeAll();
        DefaultListModel model = new DefaultListModel();

        pMan.getbMan().getBandMembers().forEach(bm -> model.addElement(bm.getfName() + " " + bm.getlName()));

        bmlist.setModel(model);
        bmlist.revalidate();
    }

}
