import dnl.utils.text.table.TextTable;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class containing the CLI.
 *
 * @version 1.0 Skeleton
 */

public class UIView_Console {
    private final Controller controller;
    private final Scanner reader;
    private final CSV_Parser parser;
    // All the CLI user actions.
    // String is the name of the action as displayed in the CLI.
    // Runnable is the method that will be run when the action is chosen.
    Map<String, Runnable> userActions;
    private List<Log> logs;
    private List<FoodComponent> foods;
    private List<Exercise> exercise;

    /**
     * Parameterized Constructor.
     */
    public UIView_Console() {
        parser = new CSV_Parser();
        logs = parser.getLogFromCSV();
        foods = parser.getFoodFromCSV();
        exercise = parser.getExerciseFromCSV();
        reader = new Scanner(System.in);

        controller = new Controller(logs, foods, exercise, this);
        controller.setLogs(logs);
        controller.setComponents(foods);
        controller.setExercises(exercise);

        // Set up all the possible user interactions that can be done in the CLI
        userActions = new LinkedHashMap<>();

        userActions.put("Add/Remove a Food", () -> {
            // Check for either add or remove
            System.out.println("1. Add a Food");
            System.out.println("2. Remove a Food");
            int choice = reader.nextInt();
            reader.nextLine();

            if (choice == 1) {
                System.out.println("Enter the food data in this exact format: name,calories,fat,carbs,protein");
                String foodData = reader.nextLine();
                try {
                    String[] data = foodData.split(",");

                    // Add data to components list in controller
                    Food newFood = new Food(
                            data[0],
                            Double.parseDouble(data[1]),
                            Double.parseDouble(data[2]),
                            Double.parseDouble(data[3]),
                            Double.parseDouble(data[4]));
                    List<FoodComponent> components = controller.getComponents();
                    components.add(newFood);
                    controller.setComponents(components);

                    // Add new food to the csv
                    List<String[]> linesToAdd = new ArrayList<>();
                    String[] line = new String[data.length + 1];
                    line[0] = "b";
                    System.arraycopy(data, 0, line, 1, line.length - 1);
                    linesToAdd.add(line);
                    controller.writeNewDataCSV("foods.csv", linesToAdd);

                    System.out.println("'" + data[0] + "' added!");
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("ERROR: Please make sure to enter the food in the correct format.");
                }
            } else {
                // Display list of foods
                System.out.println("Which food would you like to remove?:");
                List<FoodComponent> components = controller.getComponents();
                for (int i = 0; i < components.size(); i++) {
                    System.out.println((i + 1) + ". " + components.get(i).getName());
                }

                // Remove selected food from components list
                int index = reader.nextInt() - 1;
                String name = components.get(index).getName();
                components.remove(index);
                controller.setComponents(components);

                // Remove selected food from the CSV
                controller.deleteSelectedFoodFromCSV("foods.csv", name);
            }
        });

        /*
        userActions.put("Add/Remove a Recipe", () -> {
            System.out.println("1. Add a Recipe");
            System.out.println("2. Remove a Recipe");
            int choice = reader.nextInt();
            reader.nextLine();
            if(choice == 1) {
                // TODO: IMPLEMENT ADDING RECIPES
            } else {
                // TODO: IMPLEMENT REMOVE RECIPES
            }
        });
         */

//        userActions.put("Change Desired Daily Calorie Limit", () -> {
//            String date = getDateFromUser();
//            //check date
//            if (controller.checkUserDate(date)) {
//                System.out.print("Enter your Desired Calorie Limit: ");
//                double calories = reader.nextDouble();
//                controller.setUserDataDesiredDailyCalories(date, calories);
//
//            } else {
//                //new Food(2000.0, 12.1, 12.1, 12.1);
//                System.out.print("Enter your Desired Calorie Limit (Enter 'default' for default): ");
//                try {
//                    double calories = Double.parseDouble(reader.next());
//                    controller.setUserDataDesiredDailyCalories(date, calories);
//                    Log log = new Log("c", date.split("-"), calories);
//                    logs.add(new Log("c", calories));
//                    ArrayList<String[]> list = new ArrayList<>();
//                    list.add(log.toString().split(","));
//                    controller.writeNewDataCSV("log.csv", list);
//
//                } catch (Exception ime) {
//                    controller.setUserDataDesiredDailyCalories(date, 2000.0);
//                    Log log = new Log("c", date.split("-"), 2000.0);
//                    logs.add(new Log("c", 2000.0));
//                    ArrayList<String[]> list = new ArrayList<>();
//                    list.add(log.toString().split(","));
//                    controller.writeNewDataCSV("log.csv", list);
//                }
//
//
//            }
//
//        });
//
//        userActions.put("Change Desired Weight", () -> {
//            String date = getDateFromUser();
//            //check date
//            if (controller.checkUserDate(date)) {
//                System.out.print("Enter your Weight Limit: ");
//                double weight = reader.nextDouble();
//                controller.setUserDataDesiredWeight(date, weight);
//
//            } else {
//                //new Food(2000.0, 12.1, 12.1, 12.1);
//                System.out.print("Enter your Weight Limit (Enter 'default' for default): ");
//                try {
//                    double weight = Double.parseDouble(reader.next());
//                    controller.setUserDataDesiredWeight(date, weight);
//                    Log log = new Log("w", date.split("-"), weight);
//                    logs.add(new Log("w", weight));
//                    ArrayList<String[]> list = new ArrayList<>();
//                    list.add(log.toString().split(","));
//                    controller.writeNewDataCSV("log.csv", list);
//
//                } catch (Exception ime) {
//                    controller.setUserDataDesiredWeight(date, 150.0);
//                    Log log = new Log("w", date.split("-"), 150.0);
//                    logs.add(new Log("w", 150.0));
//                    ArrayList<String[]> list = new ArrayList<>();
//                    list.add(log.toString().split(","));
//                    controller.writeNewDataCSV("log.csv", list);
//                }
//
//
//            }
//
//
//        });
//
//        userActions.put("Build CLI Food Table", () -> controller.buildCLIFoodTable(foods));
//
//        userActions.put("Build CLI Log Table", () -> controller.buildCLILogTable(logs));
//
//        userActions.put("Get Info for Date", () -> controller.buildCLIInfoForDateTable(this.getDateFromUser()));
//
//        // Start the program based on the user input
//        System.out.print("What version do you want to use (type CLI or GUI): ");
//        String mode = reader.next();
//
//        switch (mode.toUpperCase()) {
//            case "CLI":
//                startCLI(); //comment out this line to test directly in main
//                break;
//            case "GUI":
//                startGUI();
//                break;
//        }
    }

    /**
     * Starts program in CLI view.
     */
    public void startCLI() {

        // Convert the userActions to arrays so they can be indexed
        String[] actionNames = userActions.keySet().toArray(new String[0]);
        Runnable[] actionMethods = userActions.values().toArray(new Runnable[0]);

        int input = -1;
        while (input != 0) {
            //update view
            logs = parser.getLogFromCSV();
            foods = parser.getFoodFromCSV();

            // Display options
            System.out.println("\nChoose an option:");
            for (int i = 0; i < actionNames.length; i++) {
                System.out.println((i + 1) + ". " + actionNames[i]);
            }
            System.out.println("0. Quit");
            System.out.println("---------------------------");

            // Get input & run the associated action
            input = reader.nextInt();
            if (input != 0) actionMethods[input - 1].run();
        }
    }

    /**
     * Starts program in GUI view.
     */
    public void startGUI() {
        new UIView();
    }


    /**
     * Build CLI Table.
     *
     * @param data : String[][]
     */
    public void buildLogTable(String[][] data) {
        TextTable tt = new TextTable(CSVEnum.log.returnCSVEnum(findMaxLength(data)), data);
        tt.printTable();

    }

    /**
     * Build food table in CLI from foods.csv.
     *
     * @param data : String[][]
     */
    public void buildFoodTable(String[][] data) {
        TextTable tt = new TextTable(CSVEnum.foods.returnCSVEnum(findMaxLength(data)), data);
        tt.printTable();
    }

    /**
     * Builds log table in CLI from log.csv.
     *
     * @param data : String[][]
     */
    public void buildInfoForDateTable(String[][] data) {
        TextTable tt = new TextTable(CSVEnum.infoByDate.returnCSVEnum(findMaxLength(data)), data);
        tt.printTable();
    }

    /**
     * Find longest array in matrix.
     *
     * @param matrix : String[][]
     * @return int
     */
    public int findMaxLength(String[][] matrix) {
        int maxLength = matrix[0].length;

        for (String[] strings : matrix) {
            if (strings.length >= maxLength) {
                maxLength = strings.length;
            }
        }

        return maxLength;
    }

    /**
     * Gets the date entered by the user.
     *
     * @return string
     */
    public String getDateFromUser() {
        System.out.print("Enter the date you want to set in this format \"YYYY-MM-DD\": ");
        return reader.next();
    }

    /**
     * Get current date and set year, month, day accordingly.
     *
     * @return Date
     */
    public String[] getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        return date.split("-");
    }

    /**
     * Removes food from foods.csv based on user input.
     *
     * @return String
     */
    public String getFoodToRemove() {
        String foodToRemove = "";
        System.out.print("Name of the food to remove: ");
        foodToRemove += reader.nextLine();

        return foodToRemove;
    }

}
