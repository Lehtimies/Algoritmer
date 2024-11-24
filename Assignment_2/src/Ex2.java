import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;

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
                    ArrayList<Node> nodeList = graph.getNodes();

                    for (String e:edges) {       // For all edges
                        String[] edgePair = e.trim().split(":"); //Split edge components v1:v2
                        System.out.println (edgePair[0].trim() + " " + edgePair[1].trim() );
                        // Here you should create an edge in the graph
                        Node node1 = null;
                        Node node2 = null;
                        for (Node node : nodeList) {
                            if (node.getName().equals(edgePair[0].trim())) {
                                node1 = node;
                            }
                            if (node.getName().equals(edgePair[1].trim())) {
                                node2 = node;
                            }
                        }
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
        graph.topSort();
        System.out.println("------------");
        System.out.println("Debug:");
        int i = 0;
        while (i < graph.getNodes().size()) {
            for (Node node : graph.getNodes()) {
                if (node.getTopNum() == i) {
                    System.out.println(node.getName() + " index: " + i);
                    i++;
                    break;
                }
            }
        }
        for (Node node : graph.getNodes()) {
            System.out.println("Node name: " + node.getName() + " index: " + node.getTopNum());
        }
        for (Node[] edge : graph.getEdges()) {
            System.out.println("Edges: " + edge[0].getName() + " " + edge[1].getName());
        }
    }

}


@SuppressWarnings("serial")
class FileFormatException extends Exception { //Input file has the wrong format
    public FileFormatException(String message) {
        super(message);
    }

}

class Graphs {
    private ArrayList<Node> nodes;
    private ArrayList<Node[]> edges;

    public Graphs() {
        nodes = new ArrayList<Node>();
        edges = new ArrayList<Node[]>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Node v1, Node v2) {
        Node[] edge = {v1, v2};
        edges.add(edge);
    }

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

    public void topSort() throws CycleFound {
        Node v;
        for (int counter = 0; counter < nodes.size(); counter++) {
            v = findNewVertexOfIndegreeZero();
            if ( v == null )
                throw new CycleFound();
            v.setIncomingConnections(-1);
            v.topNum(counter);
            for (Node w : v.getConnections()) {
                int newIncomingConnections = w.getIncomingConnections() - 1;
                w.setIncomingConnections(newIncomingConnections);
            }
        }
    }

    private Node findNewVertexOfIndegreeZero() {
        for (Node node : nodes) {
            if (node.getIncomingConnections() == 0) {
                return node;
            }
        }
        return null;
    }
}

class Node {
    private String name;
    private ArrayList<Node> connections;
    private int incomingConnections = 0;
    private int index = -1;

    public Node(String nodeName, ArrayList<Node> connectionList) {
        this.name = nodeName;
        this.connections = connectionList;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Node> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Node> connectionList) {
        this.connections = connectionList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addConnection(Node connection) {
        connections.add(connection);
    }

    public int getIncomingConnections() {
        return incomingConnections;
    }

    public void setIncomingConnections(int incomingConnections) {
        this.incomingConnections = incomingConnections;
    }

    public void topNum(int index) {
        this.index = index;
    }

    public int getTopNum() {
        return index;
    }
}

class CycleFound extends Throwable {
    public CycleFound() {
        super("Cycle found in graph");
    }
}

