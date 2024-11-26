import java.util.*;
import java.io.*;
import javax.swing.*;

public class Ex2 {
    public static void main(String[] args) throws IOException, FileFormatException, CycleFound {

        // Choose a file in the folder Graphs in the current directory
        JFileChooser jf = new JFileChooser("Graphs");
        int result = jf.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jf.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());   // Debug

            readGraph(selectedFile);    // Read nodes and edges from the selected file
        }
    }

    // Read in a graph from a file and print out the nodes and edges
    public static void readGraph(File selectedFile) throws IOException, FileFormatException, CycleFound {

        BufferedReader r = new BufferedReader(new FileReader(selectedFile));
        String line=null;

        Graphs graph = new Graphs();

        try {
            // Skip over comment lines in the beginning of the file
            while ( !(line = r.readLine()).equalsIgnoreCase("[Vertex]") ) {} ;
            System.out.println(); System.out.println("Nodes:");

            // Read all vertex definitions
            while (!(line=r.readLine()).equalsIgnoreCase("[Edges]") ) {
                if (line.trim().length() > 0) {  // Skip empty lines
                    try {
                        // Split the line into a comma separated list V1,V2 etc
                        String[] nodeNames=line.split(",");

                        for (String n:nodeNames) {
                            System.out.println(n.trim() );   // Trim and print the node name
                            // Here you should create a node in the graph

                            // Create a new node with the name n and an empty list of connections
                            Node node = new Node(n.trim(), new ArrayList<Node>());
                            graph.addNode(node);
                        }

                    } catch (Exception e) {   // Something wrong in the graph file
                        r.close();
                        throw new FileFormatException("Error in vertex definitions");
                    }
                }
            }

        } catch (NullPointerException e1) {  // The input file has wrong format
            throw new FileFormatException(" No [Vertex] or [Edges] section found in the file " + selectedFile.getName());
        }

        System.out.println(); System.out.println("Edges:");
        // Read all edge definitions
        while ( (line=r.readLine()) !=null ) {
            if (line.trim().length() > 0) {  // Skip empty lines
                try {
                    String[] edges=line.split(",");           // Edges are comma separated pairs e1:e2
                    ArrayList<Node> nodeList = graph.getNodes();    // Get the list of nodes in the graph

                    for (String e:edges) {       // For all edges
                        String[] edgePair = e.trim().split(":"); //Split edge components v1:v2
                        System.out.println (edgePair[0].trim() + " " + edgePair[1].trim() );
                        // Here you should create an edge in the graph

                        // Initialize two nodes to null, these will be used to reference the nodes in the nodeList
                        Node node1 = null;
                        Node node2 = null;
                        // For each node in the nodeList from the graph, check if the node name matches the edge name
                        for (Node node : nodeList) {
                            // If the node name matches the edge name, make the edge node reference the node in question
                            // This is done so that the connections that later get added to the edge node also get added
                            // to the node in the nodeList of the graph
                            if (node.getName().equals(edgePair[0].trim())) {
                                node1 = node;
                            }
                            if (node.getName().equals(edgePair[1].trim())) {
                                node2 = node;
                            }
                        }
                        // If both nodes are found, add the connection between them
                        if (node1 != null && node2 != null) {
                            node1.addConnection(node2);
                            node2.setIncomingConnections(node2.getIncomingConnections() + 1);
                            graph.addEdge(node1, node2);
                        } else {
                            throw new FileFormatException("Error, one or both nodes in edge not found for edge " + e);
                        }
                    }

                } catch (Exception e) { //Something is wrong, Edges should be in format v1:v2
                    r.close();
                    throw new FileFormatException("Error in edge definition");
                }
            }
        }
        r.close();  // Close the reader
        System.out.println("------------");
        System.out.println("Run topSort");
        graph.topSort(); // Sort the graph
        System.out.println("------------");
        //graph.debugGraph(); // Check that everything got added to the graph correctly
    }

}


@SuppressWarnings("serial")
class FileFormatException extends Exception { //Input file has the wrong format
    public FileFormatException(String message) {
        super(message);
    }

}

/**
 * Class for handling graphs
 */
class Graphs {
    private ArrayList<Node> nodes;
    private ArrayList<Node[]> edges;

    /**
     * Constructor for Graphs
     */
    public Graphs() {
        nodes = new ArrayList<Node>();
        edges = new ArrayList<Node[]>();
    }

    /**
     * Add a node to the graph
     * @param node Node to add
     */
    public void addNode(Node node) {
        nodes.add(node);
    }

    /**
     * Add an edge to the graph
     * @param v1 Node 1
     * @param v2 Node 2
     */
    public void addEdge(Node v1, Node v2) {
        Node[] edge = {v1, v2};
        edges.add(edge);
    }

    /*
     * Setters and getters
    */

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public void setEdges(ArrayList<Node[]> edges) {
        this.edges = edges;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Node[]> getEdges() {
        return edges;
    }

    /**
     * Topological sort of the graph
     * @throws CycleFound if a cycle is found in the graph
     */
    public void topSort() throws CycleFound {
        Node node;
        ArrayList<String> sortedNodesNames = new ArrayList<>(); // List of sorted nodes, names only
        for (int counter = 0; counter < nodes.size(); counter++) {
            // Find a node with no incoming connections
            node = findNewVertexOfIndegreeZero();
            // If no such node exists, a cycle is found
            if ( node == null )
                throw new CycleFound();
            // Set the incoming connections to -1 to remove it from consideration,
            // then set the index to the current count and add it to the end of the sorted list
            node.setIncomingConnections(-1);
            node.setIndex(counter);
            sortedNodesNames.add(node.getName());
            // Remove the connections from the node to its adjacent nodes
            for (Node adjacentNode : node.getConnections()) {
                int newIncomingConnections = adjacentNode.getIncomingConnections() - 1;
                adjacentNode.setIncomingConnections(newIncomingConnections);
            }

            /* UNCOMMENT FOR DEBUGGING!

            // DEBUG
            // Print out the nodes and their incoming connections after each iteration
            // This is to check that the incoming connections are removed correctly and that the correct nodes
            // are being selected to be handled next, i.e. the nodes with no incoming connections in the order of the nodeList
            // In short, if multiple nodes have no incoming connections, the first one in the nodeList should be selected
            ArrayList<String> connections = new ArrayList<>();
            for (Node nodeCheck : nodes) {
                connections.add(nodeCheck.getName() + " " + nodeCheck.getIncomingConnections());
            }
            System.out.println("City + incoming connections: " + connections);
            */
        }
        // Print out the sorted nodes
        System.out.println("Sorted Nodes: " + sortedNodesNames);
    }

    /**
     * Finds a new node with indegree zero (no incoming connections)
     * @return Node with indegree zero, or null if no such node exists
     */
    private Node findNewVertexOfIndegreeZero() {
        for (Node node : nodes) {
            if (node.getIncomingConnections() == 0) {
                return node;
            }
        }
        return null;
    }

    /**
     * Debug method for graphs, prints out the nodes + indexes and edges to check that everything got added and handled correctly
     */
    public void debugGraph() {
        System.out.println("Debug:");
        int i = 0;
        while (i < nodes.size()) {
            for (Node node : nodes) {
                if (node.getIndex() == i) {
                    System.out.println(node.getName() + " index: " + i);
                    i++;
                    break;
                }
            }
        }
        for (Node node : nodes) {
            System.out.println("Node name: " + node.getName() + " index: " + node.getIndex());
        }
        for (Node[] edge : edges) {
            System.out.println("Edges: " + edge[0].getName() + " " + edge[1].getName());
        }
    }
}

/**
 * Class for nodes in the graph
 */
class Node {
    private String name;
    private ArrayList<Node> connections;
    private int incomingConnections = 0;
    private int index = -1;

    /**
     * Constructor for Node class
     * @param nodeName Name of the node
     * @param connectionList List of connections to other nodes
     */
    public Node(String nodeName, ArrayList<Node> connectionList) {
        this.name = nodeName;
        this.connections = connectionList;
    }

    /**
     * Add a connection to the node
     * @param connectingNode Node to connect to this node
     */
    public void addConnection(Node connectingNode) {
        connections.add(connectingNode);
    }

    /*
     * Setters and getters
    */

    public void setConnections(ArrayList<Node> connectionList) {
        this.connections = connectionList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIncomingConnections(int incomingConnections) {
        this.incomingConnections = incomingConnections;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Node> getConnections() {
        return connections;
    }

    public int getIncomingConnections() {
        return incomingConnections;
    }

    public int getIndex() {
        return index;
    }
}

/**
 * Exception for when a cycle is found in the graph
 */
class CycleFound extends Throwable {
    public CycleFound() {
        super("Cycle found in graph");
    }
}

