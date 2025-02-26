package pageRank;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class pageRank {
    private static List<ArrayList<Integer>> adjacencyList;
    private static double[] pageRank;
    private static double[] contribution;
    private static double[] old_pageRank;
    private static int nodeCount;
    private static int edgeCount;
    private static int iterations;
    private static final double dampingFactor = 0.85;
    private static double offset = 0;

    public static void main(String[] args) {

        String filename = "C:\\Users\\Juan Cla\\Desktop\\IntroEDTI\\pgRank\\graph.txt";
        int initialValue = -1;
        iterations =  0;

        String input = "";
        try {
            input = readFile(filename);
        } catch (IOException e) {
            System.out.println("Could not read the file!");
            e.printStackTrace();
            return;
        }
        String[] rows = input.split("\n");
        try {
            int[] headerArray = extractNodeValues(rows[0]);
            nodeCount = headerArray[0];
            edgeCount = headerArray[1];

            initializeVariables(initialValue);

            createAdjacencyList(rows);

            calculateContribution();

            int count =0;
            printPageRank(count);
            do {
                calculatePageRank();

                ++count;

            } while (!didConverge(count));

                printPageRank(count);

        } catch (Exception e) {
            // Auto-generated catch block
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public static String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        List<String> inputFileList =  Files.readAllLines(path, StandardCharsets.UTF_8);
        String inputString = "";
        for (String string : inputFileList) {
            inputString = inputString.concat(string) + "\n";
        }
        return inputString;
    }

    public static void initializeVariables(int initialValue) {
        adjacencyList = new ArrayList<ArrayList<Integer>>(nodeCount);
        pageRank = new double[nodeCount];
        contribution = new double[nodeCount];
        old_pageRank= new double[nodeCount];
        offset = (1 - dampingFactor)/nodeCount;

        double init = 0;
        switch (initialValue) {
            case 0:
            case 1:
                init = initialValue;
                break;

            case -1:
                init = 1 / (double)nodeCount;
                break;

            case -2:
                init = 1 / Math.sqrt(nodeCount);
                break;
            default:
                init = initialValue;
                break;
        }

        for (int i = 0; i < nodeCount; i++) {
            adjacencyList.add(new ArrayList<>());
            pageRank[i] = init;
            old_pageRank[i] = init;
        }
    }

    public static void createAdjacencyList(String[] rows) throws Exception {
        int[] row;
        int node1, node2;

        for (int index = 0; index <= edgeCount; index++) {

            row = extractNodeValues(rows[index]);
            node1 = row[0];
            node2 = row[1];

            if (node1 >= nodeCount || node2 >= nodeCount) {
                System.out.println("Input invalido: "+node1+" "+node2+ " "+ adjacencyList.size());
                return;
            }
            adjacencyList.get(node1).add(node2);
        }
    }

    public static int[] extractNodeValues(String line) throws Exception {
        String []row = line.split(" ");

        if (row.length != 2) {
            throw new Exception("Input invalido:"+row);
        }

        int[] result = new int[2];
        result[0] = Integer.parseInt(row[0]);
        result[1] = Integer.parseInt(row[1]);

        return result;
    }

    public static void calculateContribution() {
        for (int i = 0; i < contribution.length; i++) {
            contribution[i] = adjacencyList.get(i).size();
        }
    }
    public static void calculatePageRank() {

        double[] newPageRankArray = new double[nodeCount];
        double intermediateCalculation;
        for (int i = 0; i < pageRank.length; i++) {
            intermediateCalculation = 0;
            for (int j = 0; j < adjacencyList.size(); j++) {
                if (adjacencyList.get(j).contains(i)) {
                    intermediateCalculation += pageRank[j] / contribution[j];
                }
            }
            newPageRankArray[i] = offset + dampingFactor * intermediateCalculation;
        }

        old_pageRank = pageRank;
        pageRank = newPageRankArray;
    }

    public static boolean didConverge(int current_iteration) {
        double multiplicationFactor = 0;
        if (iterations > 0) {
            return current_iteration == iterations;
        }
        else {
            if (iterations == 0) {
                multiplicationFactor = 100000;
            }
            else  {
                multiplicationFactor = Math.pow(10, (iterations * -1));
            }

            for (int i = 0; i < pageRank.length; i++) {
                if ((int)Math.floor(pageRank[i]*multiplicationFactor) != (int)Math.floor(old_pageRank[i]*multiplicationFactor)) {
                    return false;
                }
            }
            return true;
        }
    }
    public static void printPageRank(int iteration) {
            System.out.print("Iterat : "+ iteration + " : ");

        DecimalFormat numberFormat = new DecimalFormat("0.0000000");

        for (int i = 0; i < pageRank.length; i++) {
            System.out.print("PR["+ i + "] = "+numberFormat.format(pageRank[i]) + " ");
        }
        System.out.println();
    }

}