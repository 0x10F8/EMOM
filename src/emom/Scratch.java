package emom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class Scratch {

    static class Index {
        private int index;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

    }

    public static void main(String[] args) {

        JFrame app = null;
        try {
            /* Set system look and feel */
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            /* Start App */
            app = new JFrame();
        } catch (Exception e) {
            e.printStackTrace();
        }

        app.setTitle("EMOM");

        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JPanel mainPanel = new JPanel(new BorderLayout());
        final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        final JTextField time = new JTextField("00:00:00");
        time.setFont(new Font("Arial", 1, 39));
        time.setEditable(false);

        final JTextArea itemsLabel = new JTextArea();
        itemsLabel.setText("\n\n\n\n\n\n");
        itemsLabel.setEditable(false);
        itemsLabel.setFont(new Font("Arial", 0, 39));

        topPanel.add(time);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(itemsLabel, BorderLayout.CENTER);

        app.add(mainPanel);
        app.setSize(500, 500);
        app.setVisible(true);

        final List<String> items = Arrays.asList(
                "Lunge",
                "Clean and Jerk",
                "Snatch",
                "Squat",
                "Row",
                "Swing",
                "Push Up",
                "Thrusters",
                "Crunches");

        final int itemsAtATime = 4;

        final Index startIndex = new Index();
        startIndex.setIndex(0);

        final Timer timer = new Timer();

        final Index previousSeconds = new Index();
        previousSeconds.setIndex(-1);

        final long startTime = new Date().getTime();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int secondsSinceStart = (int) ((new Date().getTime() - startTime) / 1000);
                // New Second
                if (secondsSinceStart != previousSeconds.getIndex()) {
                    // Update timer
                    previousSeconds.setIndex(secondsSinceStart);
                    int hours = secondsSinceStart / 3600;
                    int minutes = (secondsSinceStart % 3600) / 60;
                    int seconds = secondsSinceStart % 60;
                    final String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    time.setText(timeString);

                    // Do timer stuff every minute
                    if (seconds == 0) {
                        itemsLabel.setBackground(Color.WHITE);
                        playSound("bell.wav");
                        displayItems(startIndex, itemsAtATime, items, itemsLabel);
                    } else if (seconds >= 57) {
                        itemsLabel.setBackground(Color.RED);
                        playSound("beep.wav");
                    }
                }
            }
        }, 0, 100);
    }

    private static synchronized void playSound(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem
                            .getAudioInputStream(Scratch.class.getResourceAsStream(url));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    private static void displayItems(final Index startIndex, int itemsAtATime, final List<String> allItems,
            final JTextArea items) {
        final List<String> runItems = getItems(allItems, itemsAtATime, startIndex.getIndex());
        startIndex.setIndex(startIndex.getIndex() + 1);
        if (startIndex.getIndex() == allItems.size()) {
            startIndex.setIndex(0);
        }
        String text = "";
        for (String item : runItems) {
            text += item + "\n";
        }
        items.setText(text);
    }

    private static List<String> getItems(final List<String> allItems, final int atATime, final int startIndex) {
        final List<String> items = new ArrayList<>();
        int taken = 0;
        for (int i = startIndex; i < allItems.size(); i++) {
            items.add(allItems.get(i));
            taken++;
            if (taken == atATime) {
                break;
            } else if (i == allItems.size() - 1) {
                i = -1;
            }
        }
        return items;
    }

}
