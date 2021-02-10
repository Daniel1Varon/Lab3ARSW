package edu.eci.arsw.highlandersim;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JScrollBar;

public class ControlFrame extends JFrame {

    private static final int DEFAULT_IMMORTAL_HEALTH = 100;
    private static final int DEFAULT_DAMAGE_VALUE = 10;

    private JPanel contentPane;

    private List<Immortal> immortals;

    private JTextArea output;
    private JLabel statisticsLabel;
    private JScrollPane scrollPane;
    private JTextField numOfImmortals;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ControlFrame frame = new ControlFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ControlFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 647, 248);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JButton btnStop = new JButton("STOP");
        JButton btnPauseAndCheck = new JButton("Pause and check");
        JButton btnResume = new JButton("Resume");
        JToolBar toolBar = new JToolBar();
        contentPane.add(toolBar, BorderLayout.NORTH);

        final JButton btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                immortals = setupInmortals();


                int sum = 0;

                for (Immortal im : immortals) {
                    sum += im.getHealth();
                }
                statisticsLabel.setText("<html>" + immortals.toString() + "<br>Health sum:" + sum);


                if (immortals != null) {
                    for (Immortal im : immortals) {
                        im.start();
                    }
                }
                btnResume.setEnabled(true);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnPauseAndCheck.setText("Pause and check");
            }
        });
        toolBar.add(btnStart);


        btnPauseAndCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                for (Immortal im : immortals) {
                    try {
                        im.pausa();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                int sum = 0;
                for (Immortal im : immortals) {
                    sum += im.getHealth();
                }

                statisticsLabel.setText("<html>" + immortals.toString() + "<br>Health sum:" + sum);


            }
        });
        toolBar.add(btnPauseAndCheck);


        btnResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (Immortal im : immortals) {
                    im.reanudar();
                }

            }
        });

        toolBar.add(btnResume);

        JLabel lblNumOfImmortals = new JLabel("num. of immortals:");
        toolBar.add(lblNumOfImmortals);

        numOfImmortals = new JTextField();
        numOfImmortals.setText("3");
        toolBar.add(numOfImmortals);
        numOfImmortals.setColumns(10);


        btnStop.setEnabled(false);

        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnStop.setEnabled(false);
                btnResume.setEnabled(false);
                btnPauseAndCheck.setText("Check");
                for (Immortal im : immortals) {
                    synchronized (immortals) {
                        im.stopp();
                    }
                }

            }
        });
        btnStop.setForeground(Color.RED);
        toolBar.add(btnStop);

        scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        output = new JTextArea();
        output.setEditable(false);
        scrollPane.setViewportView(output);


        statisticsLabel = new JLabel("Immortals total health:");
        contentPane.add(statisticsLabel, BorderLayout.SOUTH);

    }

    public List<Immortal> setupInmortals() {

        ImmortalUpdateReportCallback ucb = new TextAreaUpdateReportCallback(output, scrollPane);

        try {
            int ni = Integer.parseInt(numOfImmortals.getText());

            List<Immortal> il = new LinkedList<Immortal>();

            for (int i = 0; i < ni; i++) {
                Immortal i1 = new Immortal("im" + i, il, DEFAULT_IMMORTAL_HEALTH, DEFAULT_DAMAGE_VALUE, ucb);
                il.add(i1);
            }
            return il;
        } catch (NumberFormatException e) {
            JOptionPane.showConfirmDialog(null, "Número inválido.");
            return null;
        }

    }

}

class TextAreaUpdateReportCallback implements ImmortalUpdateReportCallback {

    JTextArea ta;
    JScrollPane jsp;

    public TextAreaUpdateReportCallback(JTextArea ta, JScrollPane jsp) {
        this.ta = ta;
        this.jsp = jsp;
    }

    @Override
    public void processReport(String report) {
        ta.append(report);

        //move scrollbar to the bottom
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                                                   public void run() {
                                                       JScrollBar bar = jsp.getVerticalScrollBar();
                                                       bar.setValue(bar.getMaximum());
                                                   }
                                               }
        );

    }

}