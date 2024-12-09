import javax.swing.*;
import java.io.*;
import java.util.*;

public class WordTransformer {
    /**
     * Inner class to represent a node in the graph.
     */
    private static class Node {
        String word;                                // Word represented by the node
        boolean known = false;                      // Flag to indicate if the node is known
        int dist = Integer.MAX_VALUE;               // Distance from the source node
        Node path = null;                           // Previous node in the shortest path
        List<Node> neighbors = new ArrayList<>();   // List of neighboring nodes

        // Constructor
        Node(String word) {
            this.word = word;
        }
    }
    // HashMap to store the graph
    private static final HashMap<String, Node> graph = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Load words from file
        Set<String> words = loadWords("Words.txt");

        // Create graph
        createGraph(words);

        // Read user input
        System.out.println("Enter start word (A): ");
        String start = scanner.nextLine();
        System.out.println("Enter end word (B): ");
        String end = scanner.nextLine();

        // Check if both words are in the dictionary
        if (!words.contains(start) || !words.contains(end)) {
            System.out.println("Both words must be in the dictionary.");
            return;
        }

        // Run Dijkstra's algorithm
        dijkstra(graph.get(start));

        // Print the shortest path
        Node endNode = graph.get(end);
        if (endNode.dist == Integer.MAX_VALUE) {
            System.out.println("No path found.");
        } else {
            System.out.println("Shortest path: ");
            printPath(endNode);
            System.out.println();
        }
    }

    /**
     * Load words from file. If file is not found, open a file selector.
     * @param file File name
     * @return Set of words
     */
    private static Set<String> loadWords(String file) {
        Set<String> words = new HashSet<>();
        try (InputStream inputStream = WordTransformer.class.getResourceAsStream("/" + file)) {
            // If the file could be found then read it, otherwise open the file chooser
            if (inputStream != null) {
                readWords(inputStream, words);
            } else {
                readFromFileChooser(words);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
        System.out.println(words);
        return words;
    }

    /**
     * Helper method for the loadWords() method. Read words from input stream and add them to the set.
     * @param inputStream Input stream of file or resource
     * @param words Set of words
     * @throws IOException If an I/O error occurs
     */
    private static void readWords(InputStream inputStream, Set<String> words) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
        }
    }

    /**
     * Helper method for the loadWords() method. Open a file selector to read words from a file.
     * @param words Set of words
     */
    private static void readFromFileChooser(Set<String> words) {
        System.out.println("File not found. Opening File-Selector...");
        JFileChooser jf = new JFileChooser("Documents");
        int result = jf.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jf.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            // Read words from the selected file and add them to the set
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    words.add(line.trim());
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the file.");
                e.printStackTrace();
            }
        } else {
            // Exit the program if no file is selected
            System.out.println("No file selected.");
            System.exit(0);
        }
    }

    /**
     * Create a graph from the set of words. Two words are connected if they differ by one character.
     * @param words Set of words
     */
    private static void createGraph(Set<String> words) {
        // Create nodes for each word
        for (String word : words) {
            graph.put(word, new Node(word));
        }

        // Connect nodes that differ by one character
        for (String word1 : words) {
            for (String word2 : words) {
                if (isOneCharacterDifferent(word1, word2)) {
                    graph.get(word1).neighbors.add(graph.get(word2));
                }
            }
        }
    }

    /**
     * Method to check if two words differ by one character.
     * @param word1 First word
     * @param word2 Second word
     * @return True if the words differ by only one character, false otherwise
     */
    private static boolean isOneCharacterDifferent(String word1, String word2) {
        if (word1.length() != word2.length()) {
            return false;
        }

        int diffCount = 0;
        for (int i = 0; i < word1.length(); i++) {
            if (word1.charAt(i) != word2.charAt(i)) {
                diffCount++;
                if (diffCount > 1) {
                    return false;
                }
            }
        }
        return diffCount == 1;
    }

    /**
     * Dijkstra's algorithm to find the shortest path from the source node to all other nodes.
     * @param source The source node
     */
    private static void dijkstra(Node source) {
        source.dist = 0;
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.dist));
        queue.add(source);

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (node.known) {
                continue;
            }
            node.known = true;

            // Update the distance of the neighbors
            for (Node neighbor : node.neighbors) {
                if (!neighbor.known) {
                    int newDist = node.dist + 1;
                    // If the new distance is less than the current distance, update the distance and path
                    if (newDist < neighbor.dist) {
                        neighbor.dist = newDist;
                        neighbor.path = node;
                        queue.add(neighbor);
                    }
                }
            }
        }
    }

    /**
     * Print the shortest path from the source node to the given node.
     * @param node The destination node
     */
    private static void printPath(Node node) {
        if (node.path != null) {
            printPath(node.path);
            System.out.print(" -> ");
        }
        System.out.print(node.word);
    }
}
