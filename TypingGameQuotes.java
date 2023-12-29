/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quotes223;

/**
 *
 * @author farisyaalyssa
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TypingGameQuotes extends JFrame{

    private final JPanel panel;
    private JLabel[] labels;
    private final JLabel timerLabel; // New label to display the timer
    private String targetText;
    private int currentIndex = 0;
    private int correctlyTyped = 0;
    private int previousSpaceIndex = 0;
    private int nextSpaceIndex = 0;
    private int wrongWordFirstChar = 0;
    private int missed = 0;
    private int errorCount = 0; // New variable to count errors
    private Timer timer;
    private int givenTime = 30;

    public TypingGameQuotes() {
        super("Type-A-Thon");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 900);
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
            targetText = null;
            for (JLabel label : labels) {
                panel.remove(label);
            }
            panel.revalidate();
            panel.repaint();
            initializeGame();
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
                    // start timer when the user types the first character
                    startTimer();
                }

                char typedChar = e.getKeyChar();
                char targetChar = targetText.charAt(currentIndex);
                
                if (typedChar == KeyEvent.VK_BACK_SPACE) {
                    // Handle backspace separately
                    labels[currentIndex].setForeground(Color.BLACK); //without this, the character will turn red since backspace is taking its place
                    currentIndex = Math.max(0, currentIndex - 1); //go back to the previous index, minimum 0
                    labels[currentIndex].setForeground(Color.BLACK); //turn the previous character to black again denoting we erased it
                } else if (typedChar == KeyEvent.VK_SPACE && targetChar != KeyEvent.VK_SPACE && currentIndex>0){
                    // Allow the user to move to the next word if they press space
                    previousSpaceIndex = findPreviousSpaceIndex(targetText, currentIndex-1);
                    wrongWordFirstChar = previousSpaceIndex + 1;
                    correctlyTyped -= Math.max(0, currentIndex - wrongWordFirstChar - 1); //to minus the already calculated correctly typed characters because the word is now incorrectly spelled
                    nextSpaceIndex = targetText.indexOf(" ", currentIndex);
                    missed = nextSpaceIndex - currentIndex;
                    errorCount += missed;
                    currentIndex = nextSpaceIndex + 1; //go to the next word's first character
                } else {
                    if (typedChar == targetChar && targetChar != KeyEvent.VK_SPACE) {
                        labels[currentIndex].setForeground(Color.GREEN);
                        currentIndex++;
                        correctlyTyped++;
                    } else if (typedChar == targetChar && targetChar == KeyEvent.VK_SPACE) {
                        labels[currentIndex].setForeground(Color.GREEN);
                        currentIndex++; 
                    } else {
                        labels[currentIndex].setForeground(Color.RED);
                        errorCount++; // Increment error count
                        currentIndex++;
                    }
                }

                if (currentIndex == targetText.length()) { //if user finish typing
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
    }

    private void resetGame() {
        currentIndex = 0;
        errorCount = 0;
        previousSpaceIndex = 0;
        wrongWordFirstChar = 0;
        correctlyTyped = 0;
        nextSpaceIndex = 0;
        missed = 0;
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
        String[] expectedWords = targetText.split("\\s+"); //array that stores the words of the text prompt
        int[] totalCharExpected = new int[expectedWords.length]; //array that stores the number of characters in each word of the text prompt
        for(int ewi=0; ewi<expectedWords.length; ewi++){ //ewi:Expected Words Index
            totalCharExpected[ewi] = expectedWords[ewi].length();
        }
        
        String[] wordsTyped = getTypedWordsFromLabels(); //array that stores the words typed by the user

        int[] countGreen = new int[wordsTyped.length]; //array that stores the number of green characters for each word typed 
        int[] countRed = new int[wordsTyped.length]; //array that stores the number of red characters for each word typed
        int[] totalCharTyped = new int[wordsTyped.length]; //array that stores the total characters typed in each word typed (excluding missed letters)

        int currentWordIndex = 0;  // Track the current word index
        boolean insideWord = true;  // Track whether the current character is inside a word

        //Checking correctness based on foreground color
        for (int i = 0; i < labels.length; i++) {
            char currentChar = labels[i].getText().charAt(0);

            if (currentChar == ' ') {
                insideWord = false;  // Set insideWord to false when encountering a space
            } else {
                if (!insideWord) { //when insideWord is false (previous character is space)
                    currentWordIndex++;  // Entering a new word
                    insideWord = true;
                }

                if (labels[i].getForeground() == Color.GREEN) {
                    countGreen[currentWordIndex]++;
                } else if (labels[i].getForeground() == Color.RED) {
                    countRed[currentWordIndex]++;
                }
                totalCharTyped[currentWordIndex] = countGreen[currentWordIndex] + countRed[currentWordIndex]; 
            }
        }

        /* if want to check the calculations
        for (int j = 0; j < wordsTyped.length; j++) {
            System.out.println("Word Typed: " + wordsTyped[j]);
            System.out.println("Number of characters typed correctly(green): " + countGreen[j]);
            System.out.println("Number of characters typed incorrectly(red): " + countRed[j]);
            System.out.println("Number of characters expected: " + totalCharExpected[j]);
            System.out.println("Number of characters typed: " + totalCharTyped[j]);
        }*/
        
        for (int k = 0; k < wordsTyped.length; k++) {
            if(countRed[k]>0 && countGreen[k]>0 && totalCharTyped[k]==totalCharExpected[k]){
                correctlyTyped -= countGreen[k]; //to minua the green characters in incorrectly typed words that are not corrected by backspace or skipped by space
            }
        }

        int charactersTyped = 0;
        for(int wti = 0; wti<wordsTyped.length; wti++){ //wti: Words Typed Index
            charactersTyped += totalCharTyped[wti]; //to calculate total characters typed
        }
        
        double minutes = givenTime / 60.0; //can't use timeTaken=endTime-startTime because startTime is the time when the game is initiated, not when first character is typed
        int wpm = (int) Math.round(((double) correctlyTyped/5) / minutes);

        int accuracy = (int) Math.round(((double) (charactersTyped - errorCount) / charactersTyped) * 100);

        JOptionPane.showMessageDialog(null, "Time's up!\nWPM: " + wpm
                + "\nErrors: " + errorCount + "\nAccuracy: " + accuracy + "%");
        resetGame();
    }
    
    private String[] getTypedWordsFromLabels() {
        java.util.List<String> wordsTypedList = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();

        for (JLabel label : labels) {
            String text = label.getText();
            if (!text.equals(" ")) {
                currentWord.append(text);
            } else if (currentWord.length() > 0) {
                wordsTypedList.add(currentWord.toString());
                currentWord.setLength(0); // Reset currentWord for the next word
            }
        }

        // Add the last word if the sentence doesn't end with a space
        if (currentWord.length() > 0) {
            wordsTypedList.add(currentWord.toString());
        }

        return wordsTypedList.toArray(String[]::new);
    }

    private void updateTimerLabel(int seconds) {
        SwingUtilities.invokeLater(() -> {
            timerLabel.setText("Time left: "+ seconds + "s");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TypingGameQuotes typingGame = new TypingGameQuotes();
            typingGame.setVisible(true);
        });
    }

    //passage that will display in our test
    public static String getPassage(){
        ArrayList<String> Passages=new ArrayList<>();
        String pas1="how you doin’? we were on a break! she’s your lobster. could i be wearing any more clothes? gum would be perfection. what if i don't want to be a shoe? unagi is a state of total awareness.   - Friends";
        String pas2="they don’t know that we know that they know! joey doesn’t share food! pivot! pivot! pivot! i wasn’t supposed to put beef in the trifle! smelly cat, smelly cat, what are they feeding you?     - Friends";
        String pas3="coffee, coffee, coffee! oy with the poodles already. you have to sleep, it's what keeps you pretty. if eating cake is wrong, i don't want to be right. i smell snow.                     - Gilmore Girls";
        String pas4="on wednesdays we wear pink. that is so fetch! get in, loser. we're going shopping! so you agree? You think you’re really pretty? whatever , i'm getting cheese fries.                       - Mean Girls";
        String pas5="i love you. most ardently. my good opinion, once lost, is lost forever. i’m very fond of walking. may i have the next dance, miss elizabeth? are you so severe on your own sex?    - Pride and Prejudice";
        String pas6="you are my fire. the one desire. believe when I say. i want it that way. tell me why? ain't nothin' but a heartache. tell me why? ain't nothin' but a mistake. tell me why?            - Backstreet Boys";
        String pas7="remember how I used to be so stuck in one place, so cold? feeling like my heart just froze. nowhere to go with no one, nobody. suddenly, you came through. making me make a move.  - Tomorrow X Together";
        String pas8="didn't they tell us don't rush into things? didn't you flash your green eyes at me? didn't you hear what becomes of curious mind? didn't all seem new and exciting?                       - Taylor Swift";
        String pas9="all i know is oh oh oh we can go anywhere we could do anything girl whatever the mood we're in. yeah all i know is oh oh oh getting lost late at night under stars. finding love standing right   - Lauv";
        String pas10="she's my sunshine in the rain. my tylenol when i'm in pain. let me tell you what she means to me. like a tall glass of lemonade, when it's burning hot on summer days. she's exactly  - Jeremy Passion";
        String pas11="i met the devil by the window. traded my life. temptation touched my tongue. spread the wings of desire. he's whispering give up don't you put up a fight. said the devil by the   - Tomorrow X Together";

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
        Passages.add(pas11);
        
        
        Random r=new Random();
        //Getting a random position from 0-9
        int place=(r.nextInt(11)); 
        
        //to use 200 characters in our typing test so I am taking a substring of that passage from 0 to 200
        String toReturn=Passages.get(place).substring(0,200); 
        if (toReturn.charAt(199)==32){
            toReturn=toReturn.strip(); //removing the blank spaces before the after substring we have taken
            toReturn=toReturn+"."; //Adding a full stop at the last instead of a space
        }
        return(toReturn); //We have got our Passage
        
    }
    
    private static int findPreviousSpaceIndex(String text, int currentIndex) {
        for (int i = currentIndex; i >= 0; i--) {
            if (text.charAt(i) == ' ') {
                return i;
            }
        }
        // If no space is found, return -1
        return -1;
    }
    
    
}

