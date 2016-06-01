package Objects;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by Bilel on 11-5-2016.
 */
public class ClickListener extends MouseAdapter implements ActionListener {

    private final static int clickInterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");

    MouseEvent lastEventLeft, lastEventRight;
    Timer      leftClickTimer, rightClickTimer;

    public ClickListener()
    {
        this(clickInterval);
    }

    public ClickListener(int delay)
    {
        leftClickTimer = new Timer( delay, null);
        leftClickTimer.addActionListener(a -> {
                leftClickTimer.stop();
                singleClickLeft( lastEventLeft );
        });

        rightClickTimer = new Timer( delay, this);
    }

    public void mousePressed(MouseEvent e){

    }

    public void mouseClicked (MouseEvent e) {
        if (e.getClickCount() > 2) return;

        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                lastEventLeft = e;

                if (leftClickTimer.isRunning()) {
                    leftClickTimer.stop();
                    doubleClickLeft(lastEventLeft);
                } else {
                    leftClickTimer.restart();
                }
            break;
            case MouseEvent.BUTTON3:
                lastEventRight = e;

                if (rightClickTimer.isRunning()) {
                    rightClickTimer.stop();
                    doubleClickRight(lastEventRight);
                } else {
                    rightClickTimer.restart();
                }
            break;
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        rightClickTimer.stop();
        singleClickRight( lastEventRight );
    }

    public void singleClickLeft(MouseEvent e) {
    }
    public void doubleClickLeft(MouseEvent e) {
    }

    public void singleClickRight(MouseEvent e) {
    }
    public void doubleClickRight(MouseEvent e) {
    }
}
