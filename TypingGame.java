/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TypingGame extends JFrame {

    private final JPanel panel;
    private JLabel[] labels;
    private final JLabel timerLabel; // New label to display the timer
    private String targetText;
    private int currentIndex = 0;
    private long startTime;
    private int errorCount = 0; // New variable to count errors
    private Timer timer;

    public TypingGame() {
        super("Typing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 400);
        setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setFocusable(true);

        timerLabel = new JLabel("Given time: 30s");
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(timerLabel, BorderLayout.NORTH);

        add(panel, BorderLayout.CENTER);

        initializeGame();

        JButton repeatButton = new JButton("Repeat Level"); //repeat with same generated text
        repeatButton.addActionListener((ActionEvent e) -> {
            resetGame();
            resetTimer(); // Reset the timer when repeating the level
            startGameWithText(targetText);
        });

        JButton newPromptButton = new JButton("New Prompt");//repeat with another generated text
        newPromptButton.addActionListener((ActionEvent e) -> {
            resetGame();
            resetTimer(); // Reset the timer when generating a new prompt
            generateRandomPrompt();
            startGameWithText(targetText);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(repeatButton);
        buttonPanel.add(newPromptButton);

        add(buttonPanel, BorderLayout.SOUTH);

        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (currentIndex == 0 && timer == null) {
                    startTimer();
                }

                char typedChar = e.getKeyChar();

                if (typedChar == KeyEvent.VK_BACK_SPACE) {
                    currentIndex = Math.max(0, currentIndex - 1);
                } else {
                    char targetChar = targetText.charAt(currentIndex);

                    if (typedChar == targetChar) {
                        labels[currentIndex].setForeground(Color.GREEN);
                    } else {
                        labels[currentIndex].setForeground(Color.RED);
                        errorCount++; // Increment error count
                    }

                    currentIndex++;
                }

                if (currentIndex == targetText.length()) {
                    stopTimer();
                    calculateScore();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Not needed for this example
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Not needed for this example
            }
        });

        startTime = System.currentTimeMillis();
    }

    private void initializeGame() {
        generateRandomPrompt();
        labels = new JLabel[targetText.length()];
        for (int i = 0; i < targetText.length(); i++) {
            labels[i] = new JLabel(String.valueOf(targetText.charAt(i)));
            labels[i].setFont(new Font("SansSerif", Font.BOLD, 20));
            panel.add(labels[i]);
        }
    }

    private void generateRandomPrompt() {
        targetText = getPassage();
    }

    private void startGameWithText(String text) {
        for (JLabel label : labels) {
            label.setForeground(Color.BLACK);
        }

        for (int i = 0; i < text.length(); i++) {
            labels[i].setText(String.valueOf(text.charAt(i)));
        }

        panel.requestFocusInWindow();
        startTime = System.currentTimeMillis();
    }

    private void resetGame() {
        currentIndex = 0;
        errorCount = 0;
        stopTimer();
    }

    private void resetTimer() {
        updateTimerLabel(30);
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int seconds = 30;

            @Override
            public void run() {
                if (seconds > 0) {
                    seconds--;
                    updateTimerLabel(seconds);
                } else {
                    stopTimer();
                    calculateScore();
                }
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void calculateScore() {
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        int totalWords = targetText.split("\\s+").length;
        double minutes = timeTaken / 60000.0;
        double wpm = totalWords / minutes;

        int accuracy = (int) Math.round(((double) (currentIndex - errorCount) / currentIndex) * 100);

        JOptionPane.showMessageDialog(null, "Congratulations! You typed the sentence correctly.\nWPM: " + wpm
                + "\nErrors: " + errorCount + "\nAccuracy: " + accuracy + "%");
        resetGame();
    }

    private void updateTimerLabel(int seconds) {
        SwingUtilities.invokeLater(() -> {
            timerLabel.setText("Time left: "+ seconds + "s");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TypingGame typingGame = new TypingGame();
            typingGame.setVisible(true);
        });
    }

    //passage that will display in our test
    public static String getPassage(){
        ArrayList<String> Passages=new ArrayList<>();
        String pas1="If you're not doing something with your life, then it doesn’t matter how long you live. If you're doing something with your life, then it doesn't matter how short your life may be. A life is not measured by years lived, but by its usefulness. If you are giving, loving, serving, helping, encouraging, and adding value to others, then you're living a life that counts!";
        String pas2="An application programming interface(API) is a way for two or more computer programs to communicate with each other. It is a type of software interface, offering a service to other pieces of software. A document or standard that describes how to build or use such a connection or interface is called an API specification.";
        String pas3="Virtual reality is the computer-generated simulation of a three-dimensional image or environment that can be interacted with in a seemingly real or physical way by a person using special electronic equipment, such as a helmet with a screen inside or gloves fitted with sensors.";
        String pas4="A technological revolution is a period in which one or more technologies is replaced by another novel technology in a short amount of time. It is a time of accelerated technological progress characterized by innovations whose rapid application and diffusion typically cause an abrupt change in society.";
        String pas5="Augmented reality (AR) is the real-time use of information in the form of text, graphics, audio and other virtual enhancements integrated with real-world objects. It is this real world element that differentiates AR from virtual reality.";
        String pas6="Multimedia content helps to vary and enhance the learning process, and leads to better knowledge retention. Educational video can provide more opportunities for students to engage with the content. Students around the world can learn from course content made available through video.";
        String pas7="Some periods of our growth are so confusing that we don't even recognize that growth is happening...Those long periods when something inside ourselves seems to be waiting, holding its breath, unsure about what the next step should be, eventually become the periods we wait for, for it is in those periods that we realize that we are being prepared for the next phase of our life and that, in all probability, a new level of the personality is about to be revealed.";
        String pas8="Human life without technology is like birds without feathers. We cannot imagine to survive without technology in today’s fast-moving world. The Corona virus pandemic has moreover proven, the importance of technology in our daily lives. ";
        String pas9="Globalization wouldn’t have been possible without internet. The fact that we can connect and work from any part of the world is because we have internet. We can have client meets and requirements from all over the globe and we can assimilate information and process delivery because we have internet.";
        String pas10="The foremost purpose of technology is communication. Social media and other technological applications have brought families together. Today we can find long lost school mates over Facebook, twitter, Instagram and we can reinstate our communication. In fact, we can even communicate with world leaders, prominent figures over these platforms. Communication is required in professional fields as well, and technology ensures that we can communicate with the world from wherever we are.";
        
        Passages.add(pas1);
        Passages.add(pas2);
        Passages.add(pas3);
        Passages.add(pas4);
        Passages.add(pas5);
        Passages.add(pas6);
        Passages.add(pas7);
        Passages.add(pas8);
        Passages.add(pas9);
        Passages.add(pas10);
        
        Random r=new Random();
        //Getting a random position from 0-9
        int place=(r.nextInt(10)); 
        
        //to use 200 characters in our typing test so I am taking a substring of that passage from 0 to 200
        String toReturn=Passages.get(place).substring(0,200); 
        if (toReturn.charAt(199)==32){
            toReturn=toReturn.strip(); //removing the blank spaces before the after substring we have taken
            toReturn=toReturn+"."; //Adding a full stop at the last instead of a space
        }
        return(toReturn); //We have got our Passage
        
    }
}
