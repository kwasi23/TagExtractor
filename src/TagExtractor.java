// Importing required packages and classes.
import javax.swing.*;     // For Swing components like JFrame, JButton, etc.
import java.awt.*;        // For AWT classes like BorderLayout.
import java.io.*;         // For file I/O operations.
import java.util.*;       // For data structures like Map, Set, etc.
import java.util.Map.Entry;

// Defining a public class named TagExtractor, which inherits from JFrame.
public class TagExtractor extends JFrame {

    // Declaring private member variables.
    private JTextArea textArea;       // TextArea to display and interact with the content.
    private JFileChooser fileChooser; // For allowing the user to choose files.
    private Set<String> stopWords;    // A set to store stop words.
    private Map<String, Integer> wordFrequency; // A map to store the frequency of words.

    // Constructor for the TagExtractor class.
    public TagExtractor() {
        setTitle("Tag/Keyword Extractor");       // Setting the title of the JFrame.
        setSize(700, 500);                       // Setting the size of the JFrame.
        setDefaultCloseOperation(EXIT_ON_CLOSE);  // Setting the default close operation.
        initComponents();                        // Calling a method to initialize UI components.
    }

    // Method to initialize UI components.
    private void initComponents() {
        textArea = new JTextArea();               // Initializing the text area.
        JScrollPane scrollPane = new JScrollPane(textArea); // Scroll pane to make the text area scrollable.

        // Initializing file chooser, stop words set, and word frequency map.
        fileChooser = new JFileChooser();
        stopWords = new HashSet<>();
        wordFrequency = new HashMap<>();

        // Initializing buttons and setting their labels.
        JButton openFileButton = new JButton("Open Text File");
        JButton loadStopWordsButton = new JButton("Load Stop Words");
        JButton saveTagsButton = new JButton("Save Tags");

        // Adding action listeners to the buttons to define what happens on a button click.
        openFileButton.addActionListener(e -> openFile());
        loadStopWordsButton.addActionListener(e -> loadStopWords());
        saveTagsButton.addActionListener(e -> saveTags());

        // Creating a JPanel to hold the buttons.
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openFileButton);
        buttonPanel.add(loadStopWordsButton);
        buttonPanel.add(saveTagsButton);

        // Adding components to the main JFrame using BorderLayout.
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Method to open and process a file.
    private void openFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            processFile(file);  // Process the selected file.
        }
    }

    // Method to load stop words from a file.
    private void loadStopWords() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    stopWords.add(scanner.nextLine().trim().toLowerCase()); // Adding each stop word to the set.
                }
                JOptionPane.showMessageDialog(this, "Stop words loaded successfully."); // Displaying success message.
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();  // Printing stack trace for any exceptions.
            }
        }
    }

    // Method to process the file and compute word frequencies.
    private void processFile(File file) {
        wordFrequency.clear();   // Clearing previous word frequencies.
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] words = scanner.nextLine().split("\\W+"); // Splitting line into words.
                for (String word : words) {
                    word = word.toLowerCase().trim(); // Processing each word.
                    if (!word.isEmpty() && !stopWords.contains(word)) { // Checking conditions.
                        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1); // Updating frequency.
                    }
                }
            }
            displayTags();  // Displaying the tags.
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    // Method to display word frequencies in the text area.
    private void displayTags() {
        textArea.setText("");  // Clearing the text area.
        wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) // Sorting entries by frequency.
                .forEach(entry -> textArea.append(entry.getKey() + ": " + entry.getValue() + "\n")); // Appending to text area.
    }

    // Method to save the word frequencies to a file.
    private void saveTags() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                wordFrequency.forEach((word, count) -> writer.println(word + ": " + count)); // Writing to file.
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Main method to run the application.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TagExtractor().setVisible(true);  // Creating and displaying the application window.
        });
    }
}
