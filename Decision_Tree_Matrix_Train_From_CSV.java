import java.util.Scanner;
class loader {
    private final String filePath;

    public loader(String filePath) {
        this.filePath = filePath;
    }

    public static Object[][] loadCSV() {
        // Implement CSV loading logic here, returning a 2D array of Objects
        // This is a placeholder implementation and should be replaced with actual CSV parsing code
        return new Object[0][0];
    }
}
public class Decision_Tree_Matrix_Train_From_CSV {
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        String filePath = scanner.nextLine();
        if (!filePath.endsWith(".csv")) {
            filePath = filePath + ".csv";
        }
        // Load the CSV file and parse it into a 2D array        CSVLoader loader = new CSVLoader(filePath);
        Object[][] data = loader.loadCSV();    
        scanner.close();
    }
}