import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class containing all the methods related to formatting and writing to CSV.
 *
 * @author Andrea Pallotta
 * @version 1.0 Final
 */
public class CSV_Formatter {

    /**
     * Takes an array of string and formats it to CSV format.
     *
     * @param data : String[]
     * @return String
     */
    public String formatToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    /**
     * Append or write List of string arrays to a CSV file. Check for duplicates.
     *
     * @param filename : String
     * @param list     : List<String[]>
     */
    public void writeToCSV(String filename, List<String[]> list) {
        File csvOutput = new File("csv/" + filename);
        if (!duplicateFound(filename, list)) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(csvOutput, true))) {
                list.stream()
                        .map(this::formatToCSV)
                        .forEach(pw::println);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            System.out.println("Duplicated found");
        }
    }

    /**
     * Removes a line from a CSV file, by the food/recipe's name.
     *
     * @param filename The name of the CSV file
     * @param itemName The name of the food/recipe to be removed
     */
    public void removeLineByName(String filename, String itemName) throws IOException {
        File inputFile = new File("csv/" + filename);
        List<String[]> newLines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
            String[] data = currentLine.split(",");
            if (!data[1].equalsIgnoreCase(itemName)) {
                newLines.add(data);
            }
        }
        reader.close();

        // Clear the file
        PrintWriter writer = new PrintWriter(inputFile);
        writer.print("");
        writer.close();

        writeToCSV(filename, newLines);
    }

    /**
     * Replaces a line from a CSV file, by the food/recipe's name,
     *
     * @param filename The name of the CSV file
     * @param itemName The name of the food/recipe to be removed
     * @param newLine The line that will replace the old line
     */
    public void replaceLineByName(String filename, String itemName, String[] newLine) throws IOException {
        File inputFile = new File("csv/" + filename);
        List<String[]> newLines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
            String[] data = currentLine.split(",");
            if (!data[1].equalsIgnoreCase(itemName)) {
                newLines.add(data);
            } else {
                newLines.add(newLine);
            }
        }
        reader.close();

        // Clear the file
        PrintWriter writer = new PrintWriter(inputFile);
        writer.print("");
        writer.close();

        writeToCSV(filename, newLines);
    }

    public void replaceLog(Log oldLog, Log newLog) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("csv/log.csv")));
        List<String[]> matrix = new ArrayList<>();
        String currentLine;

        while ((currentLine = br.readLine()) != null) {
            if (currentLine.equals(oldLog.toString())) {
                matrix.add(newLog.toString().split(","));
            } else {
                matrix.add(currentLine.split(","));
            }
        }

        br.close();

        PrintWriter writer = new PrintWriter(new File("csv/log.csv"));
        writer.print("");
        writer.close();

        writeToCSV("log.csv", matrix);
    }

    public void updateUserData(UserData userData) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("csv/log.csv")));
        List<String[]> matrix = new ArrayList<>();
        String currentLine;

        while ((currentLine = br.readLine()) != null) {
            List<String> list = Arrays.asList(currentLine.split(","));
            if (list.contains(userData.getYear()) && list.contains(userData.getMonth()) && list.contains(userData.getDay())) {
                if (list.contains("c")) {
                    matrix.add(new String[]{ userData.getYear(), userData.getMonth(), userData.getDay(), "c", userData.getDesiredDailyCalories() + ""});
                } else if (list.contains("w")) {
                    matrix.add(new String[]{ userData.getYear(), userData.getMonth(), userData.getDay(), "w", userData.getDesiredDailyWeight() + ""});
                } else {
                    matrix.add(currentLine.split(","));
                }
            } else {
                matrix.add(currentLine.split(","));
            }
        }
        br.close();
        PrintWriter writer = new PrintWriter(new File("csv/log.csv"));
        writer.print("");
        writer.close();
        writeToCSV("log.csv", matrix);
    }

    public void writeNewUserData(UserData userData) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("csv/log.csv")));
        List<String[]> matrix = new ArrayList<>();
        String currentLine;

        while ((currentLine = br.readLine()) != null) {
            matrix.add(currentLine.split(","));
        }
        matrix.add(new String[] { userData.getYear(), userData.getMonth(), userData.getDay(), "w", userData.getDesiredDailyWeight() + "" });
        matrix.add(new String[] { userData.getYear(), userData.getMonth(), userData.getDay(), "c", userData.getDesiredDailyCalories() + "" });
        br.close();

        PrintWriter writer = new PrintWriter(new File("csv/log.csv"));
        writer.print("");
        writer.close();

        writeToCSV("log.csv", matrix);
    }

    public void removeLogFromCSV(Log log) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("csv/log.csv")));
        List<String[]> matrix = new ArrayList<>();
        String currentLine;

        while ((currentLine = br.readLine()) != null) {
            if (!currentLine.equals(log.toString())) {
                matrix.add(currentLine.split(","));
            }
        }

        br.close();

        PrintWriter writer = new PrintWriter(new File("csv/log.csv"));
        writer.print("");
        writer.close();

        writeToCSV("log.csv", matrix);
    }

    /**
     * Takes a string as argument and checks for special characters.
     *
     * @param data : String
     * @return String
     */
    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    /**
     * Check for duplicates in the CSV.
     *
     * @param filename : String
     * @param list     : List<String[]>
     * @return boolean
     */
    public boolean duplicateFound(String filename, List<String[]> list) {
        CSV_Parser parser = new CSV_Parser();
        boolean isUnique = false;
        List<String[]> listFromCSV = parser.getDataFromCSV(filename);

        if (listFromCSV == null) {
            return (list == null);
        }

        if (list == null) {
            return false;
        }

        for (String[] value : listFromCSV) {
            for (String[] strings : list) {
                if (Arrays.equals(value, strings)) {
                    isUnique = true;
                }
            }
        }
        return isUnique;
    }
}