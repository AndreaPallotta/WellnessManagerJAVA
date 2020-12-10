import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class containing all the methods related to reading and formatting from CSV.
 *
 * @author Andrea Pallotta, Justin Nauta
 * @version 1.0 Final
 */
public class CSV_Parser {
    String directory_path = "csv/";

    /**
     * Read data from csv and return a list of array strings.
     *
     * @param filename : String
     * @return List<String [ ]>
     */
    public List<String[]> getDataFromCSV(String filename) {
        List<String[]> listFromCSV = new ArrayList<>();
        String line;
        String file = directory_path + filename;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                listFromCSV.add(line.split(","));
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return listFromCSV;
    }

    /**
     * Create Food objects from foods.csv.
     *
     * @return List<Component>
     */
    public List<FoodComponent> getFoodFromCSV() {
        List<String[]> csvData = getDataFromCSV("foods.csv");
        List<FoodComponent> foods = new ArrayList<>();

        for (String[] row : csvData) {
            if (row[0].trim().equalsIgnoreCase("b")) {
                foods.add((new Food(row[1], Double.parseDouble(row[2]), Double.parseDouble(row[3]),
                        Double.parseDouble(row[4]), Double.parseDouble(row[5]))));
            }
        }

        return foods;
    }

    /**
     * Create Log objects from log.csv.
     *
     * @return List<Log>
     */
    public List<Log> getLogFromCSV() {
        List<String[]> logsFromCSV = getDataFromCSV("log.csv");
        List<Log> logs = new ArrayList<>();

        for (String[] row : logsFromCSV) {
            Log log;
            if (row.length >= 6) {
                log = new Log(row[3], Double.parseDouble(row[5]), row[4]);
            } else {
                log = new Log(row[3], Double.parseDouble(row[4]));
            }
            log.setYear(row[0]);
            log.setMonth(row[1]);
            log.setDay(row[2]);
            logs.add(log);
        }

        return logs;

    }

    /**
     * Create Recipe objects from foods.csv.
     *
     * @return List<Component>
     */
    public List<FoodComponent> getRecipeFromCSV() {
        List<String[]> csvData = getDataFromCSV("foods.csv");
        List<FoodComponent> foods = getFoodFromCSV();
        List<FoodComponent> recipes = new ArrayList<>();

        for (String[] row : csvData) {
            Map<FoodComponent, Double> children = new LinkedHashMap<>();
            if (row[0].trim().equalsIgnoreCase("r")) {
                for (int i = 2; i < row.length - 1; i += 2) {
                    boolean found = false;
                    for (FoodComponent food : foods) {
                        if (food.getName().equalsIgnoreCase(row[i])) {
                            children.put(food, Double.parseDouble(row[i + 1]));
                            found = true;
                        }
                    }

                    if (!found) {
                        for (FoodComponent recipe : recipes) {
                            if (recipe.getName().equalsIgnoreCase(row[i])) {
                                children.put(recipe, Double.parseDouble(row[i + 1]));
                            }
                        }
                    }
                }
                Recipe r = new Recipe(row[1], children);
                recipes.add(r);
            }
        }

        return recipes;
    }

    /**
     * Create Exercise objects from Exercise.csv.
     *
     * @return List<Exercise>
     */
    public List<Exercise> getExerciseFromCSV() {
        List<String[]> exerciseFromCSV = getDataFromCSV("exercise.csv");
        List<Exercise> exercises = new ArrayList<>();

        for (String[] row : exerciseFromCSV) {
            Exercise exercise = new Exercise(row[1], Double.parseDouble(row[2]));

            exercises.add(exercise);
        }

        return exercises;

    }
}
