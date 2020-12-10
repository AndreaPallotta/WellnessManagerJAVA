import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Controller class delegating between the client and rest of subsystem.
 *
 * @author John Mule, Andrea Pallotta, Justin Nauta
 * @version 2.0 Final
 */
@SuppressWarnings("unchecked")
public class Controller implements Observer {
    private final CSV_Parser parser;
    private final CSV_Formatter formatter;
    private UIView_Console uiView_console;
    private UIView view;
    private List<Log> logs;
    private List<FoodComponent> foodComponents;
    private List<FoodComponent> foods;
    private List<FoodComponent> recipes;
    private List<Exercise> exercises;
    private List<UserData> usersData;
    private String year, month, day;
    private double totalCalories;

    /**
     * Parameterized Constructor for CLI.
     *
     * @param logs           : List<Log>
     * @param components     : List<Component>>
     * @param uiView_console : UIView_Console
     */
    public Controller(List<Log> logs, List<FoodComponent> components, List<Exercise> exercises, UIView_Console uiView_console) {
        this.uiView_console = uiView_console;
        this.logs = logs;
        this.foodComponents = components;
        this.exercises = exercises;
        this.parser = new CSV_Parser();
        this.formatter = new CSV_Formatter();
        this.totalCalories = 0.0;
    }

    /**
     * Parameterized Constructor for GUI.
     *
     * @param view : UIView
     */
    public Controller(UIView view) {
        this.view = view;
        this.parser = new CSV_Parser();
        this.formatter = new CSV_Formatter();
        this.totalCalories = 0.0;
        this.logs = parser.getLogFromCSV();
        this.logs.sort(Comparator.comparing(Log::getYear));
        this.foods = parser.getFoodFromCSV();
        this.foods.sort(Comparator.comparing(FoodComponent::getName));
        this.exercises = parser.getExerciseFromCSV();
        this.exercises.sort(Comparator.comparing(Exercise::getName));
        this.recipes = parser.getRecipeFromCSV();
        this.recipes.sort(Comparator.comparing(FoodComponent::getName));
        this.usersData = buildUserData();
        getCurrentDate();
        buildGUITable();
        createOverallChart();
        sendRecipeNamesToView();

    }

    public List<UserData> buildUserData() {
        List<Log> tempList = new ArrayList<>();
        List<UserData> userDataList = new ArrayList<>();
        for (Log log : this.logs) {
            if (log.toString().split(",").length == 5) {
                tempList.add(log);
            }
        }

        for (int i = 0; i < tempList.size(); i+=2) {
            String letter = tempList.get(i).getLetter();
            String year = tempList.get(i).getYear();
            String month = tempList.get(i).getMonth();
            String day = tempList.get(i).getDay();

            String date = year + "-" + month + "-" + day;

            if (i + 1 < tempList.size()) {
                String year1 = tempList.get(i + 1).getYear();
                String month1 = tempList.get(i + 1).getMonth();
                String day1 = tempList.get(i + 1).getDay();
                String date1 = year1 + "-" + month1 + "-" + day1;
                UserData userData = null;
                if (date.equalsIgnoreCase(date1) && letter.equalsIgnoreCase("c")) {
                     userData = new UserData(year, month, day, tempList.get(i).getCalories(), tempList.get(i + 1).getWeight());
                } else if (date.equalsIgnoreCase(date1) && letter.equalsIgnoreCase("w")) {
                    userData = new UserData(year, month, day, tempList.get(i + 1).getCalories(), tempList.get(i).getWeight());
                }
                userDataList.add(userData);
            }
        }
        return userDataList;
    }

    /**
     * Get current date.
     */
    public void getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        this.year = currentDate.getYear() + "";
        this.month = currentDate.getMonthValue() + "";
        this.day = currentDate.getDayOfMonth() + "";
    }

    /**
     * Send recipe names to view.
     */
    public void sendRecipeNamesToView() {
        List<String> list = new ArrayList<>();
        for (FoodComponent component : this.recipes) {
            list.add(component.getName());
        }

        view.setRecipeNames(list.toArray(new String[0]));
    }

    /**
     * Build GUI Table for logs and foods.
     */
    public void buildGUITable() {
        this.view.buildJTable(this.foods, CSVEnum.foods.returnCSVEnum(), true, true, true, true, true);
        this.view.appendTable();
        this.view.createPopupMenu("Add Food", "Remove Food", "Add Food to Recipe");
    }

    /**
     * Send data to UIVIew to build overall BarChart.
     */
    public void createOverallChart() {
        Map<String, Double> map = new LinkedHashMap<>();
        List<Double> list = getNutrients();
        map.put("Fat", list.get(0));
        map.put("Protein", list.get(1));
        map.put("Carb", list.get(2));
        view.createOverallBarChart("Overall Nutrients Bar Chart", map);
    }

    /**
     * Create Map to populate BarChart for user's input date.
     *
     * @param year  : String
     * @param month : String
     * @param day   : String
     * @return Map<String, Double>
     */
    public Map<String, Double> createChartForDate(String year, String month, String day) {
        Map<String, Double> map = new LinkedHashMap<>();
        List<Double> list = getNutrientsForDate(year, month, day);
        map.put("Fat", list.get(0));
        map.put("Protein", list.get(1));
        map.put("Carb", list.get(2));
        return map;
    }

    /**
     * Get nutrients for foods for user's input date.
     *
     * @param year  : String
     * @param month : String
     * @param day   : String
     * @return List<Double>
     */
    public List<Double> getNutrientsForDate(String year, String month, String day) {
        List<Double> list = new ArrayList<>();
        List<String> listOfNames = getLogNamesForDate(year, month, day);
        double fat = 0;
        double carb = 0;
        double protein = 0;
        for (FoodComponent component : this.foods) {
            Food food = (Food) component;
            for (String name : listOfNames) {
                if (food.getName().equalsIgnoreCase(name)) {
                    fat += food.getFat();
                    carb += food.getCarb();
                    protein += food.getProtein();
                }
            }
        }
        list.add(fat);
        list.add(carb);
        list.add(protein);
        return list;
    }

    public Double getCaloriesForDate(String year, String month, String day) {
        List<String> listOfNames = getLogNamesForDate(year, month, day);
        double calories = 0;
        for (FoodComponent component : this.foods) {
            Food food = (Food) component;
            for (String name : listOfNames) {
                if (food.getName().equalsIgnoreCase(name)) {
                    calories += food.getCalories();
                }
            }
        }

        return calories;
    }

    /**
     * Get list of log names for user's input date.
     *
     * @param year  : String
     * @param month : String
     * @param day   : String
     * @return List<String>
     */
    public List<String> getLogNamesForDate(String year, String month, String day) {
        List<String> list = new ArrayList<>();

        for (Log log : this.logs) {
            if (log.getYear().equalsIgnoreCase(year) && log.getMonth().equalsIgnoreCase(month) && log.getDay().equalsIgnoreCase(day)) {
                list.add(log.getName());
            }
        }

        return list;
    }

    /**
     * Get nutrients from all foods.
     *
     * @return List<Double>
     */
    public List<Double> getNutrients() {
        List<Double> list = new ArrayList<>();
        double fat = 0;
        double carb = 0;
        double protein = 0;

        for (FoodComponent component : this.foods) {
            Food food = (Food) component;
            fat += food.getFat();
            carb += food.getCarb();
            protein += food.getProtein();
        }

        list.add(fat);
        list.add(carb);
        list.add(protein);

        return list;
    }

    /**
     * Build CLI Log table.
     *
     * @param logs : List<Log>
     */
    public void buildCLILogTable(List<Log> logs) {
        String[][] data = ObjectListToMatrix(logs);

        uiView_console.buildLogTable(data);
    }

    /**
     * Build CLI Food table.
     *
     * @param foods : List<Component>
     */
    public void buildCLIFoodTable(List<FoodComponent> foods) {

        String[][] data = ObjectListToMatrix(foods);
        uiView_console.buildFoodTable(data);
    }

    /**
     * Build CLI info for date table.
     *
     * @param date : String
     */
    public void buildCLIInfoForDateTable(String date) {
        String[][] data = this.buildInfoTable(date).toArray(new String[0][]);
        uiView_console.buildInfoForDateTable(data);
    }


    /**
     * Generic function that takes a List of Object
     * and transforms it into a String matrix.
     *
     * @param list : List<T>
     * @param <T>  : Object
     * @return String[][]
     */
    public <T> String[][] ObjectListToMatrix(List<T> list) {
        List<String[]> stringList = new ArrayList<>();
        String convertedString = "";
        for (T obj : list) {
            if (obj instanceof Food) {
                convertedString = "b," + obj;
            } else if (obj instanceof Log) {
                convertedString = obj + "";
            }

            String[] convertedArray = convertedString.split("[-,]");
            stringList.add(convertedArray);
        }

        return stringList.toArray(new String[0][]);
    }


    /**
     * Set list of Log.
     *
     * @param logs : List<Log>
     */
    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    /**
     * Get list of Component.
     *
     * @return List<Component>
     */
    public List<FoodComponent> getComponents() {
        return this.foodComponents;
    }

    /**
     * Set List of Component.
     *
     * @param components : List<Component>
     */
    public void setComponents(List<FoodComponent> components) {
        this.foodComponents = components;
    }

    /**
     * Set list of Exercises.
     *
     * @param exercises : List<Exercise>
     */
    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }


    /**
     * Build List<String[]> containing all foods, foods in recipes, servings, percentages of nutrients.
     *
     * @param dateFromInput : String
     * @return List<String [ ]>
     */
    public List<String[]> buildInfoTable(String dateFromInput) {
        List<String[]> infoList = new ArrayList<>();
        List<FoodComponent> components = parser.getRecipeFromCSV();
        double weight;
        double desiredCalories = 0.0;

        // For each Log in list of Log
        for (Log log : logs) {
            String dateFromLog = log.getYear() + "-" + log.getMonth() + "-" + log.getDay();
            List<String> entryList = new ArrayList<>();

            // if date from user input is the same as the one stored in the log object
            if (dateFromLog.equalsIgnoreCase(dateFromInput)) {

                // check what letter the log contains.
                // if it's "w", store weight
                // if it's "c", store calories
                // if it's "f", recursively iterate through tree and get: name, servings, nutrients
                if (log.getLetter().equalsIgnoreCase("w")) {
                    weight = log.getWeight();
                    System.out.println("Total weight for the date: " + weight + " pounds");
                } else if (log.getLetter().equalsIgnoreCase("c")) {
                    desiredCalories = log.getCalories();

                } else if (log.getLetter().equalsIgnoreCase("f")) {
                    entryList.addAll(Arrays.asList(dateFromLog.split("-")));
                    for (FoodComponent component : components) {

                        infoTableRecursion(component, entryList, log, infoList, dateFromLog);
                    }
                }
                // add entry to list of String Arrays
                infoList.add(entryList.toArray(new String[0]));

                // remove any possible empty Array in List
                infoList.removeIf(array -> (array.length == 0));

            }
        }
        System.out.println("Total calories for the date: " + this.getTotalCalories() + " calories");
        System.out.println("Desired calories: " + desiredCalories + " calories");

        if (this.getTotalCalories() <= desiredCalories) {
            System.out.println("You are below your calorie limit by: " + (desiredCalories - this.getTotalCalories()) + " calories");
        } else {
            System.out.println("You are above your calorie limit by: " + (this.getTotalCalories() - desiredCalories) + " calories");
        }

        return infoList;
    }


    /**
     * Recursive part of building info for date table.
     * Traverses through foods and recipes tree until all leaves are foods and no recipe is left.
     *
     * @param component : Component
     * @param entryList : List<String>
     * @param log       : Log
     * @param finalList : List<String[]>
     * @return List<String>
     */
    public List<String> infoTableRecursion(FoodComponent component, List<String> entryList, Log log, List<String[]> finalList, String date) {

        // if component is a food
        if (component instanceof Food) {
            Food food = (Food) component;

            // if the names of the food in logs and Component list are the same, add information to String list
            if (food.getName().trim().equalsIgnoreCase(log.getName().trim())) {
                entryList.add(food.getName());
                entryList.add(log.getServings() + "");
                getCaloriesForCLIDate(entryList, food);
                setTotalCalories(food.getCalories());
            }

            // food is last leaf, does not have any children
            return entryList;

        }

        // if component is a recipe
        else if (component instanceof Recipe) {
            Recipe recipe = (Recipe) component;

            // if the names of the recipe in logs and Component list are the same, add information to String list
            if (recipe.getName().equalsIgnoreCase(log.getName())) {

                Map<FoodComponent, Double> map = recipe.getChildren();
                // Traverse through map of recipes/foods and servings
                for (Map.Entry<FoodComponent, Double> entry : map.entrySet()) {

                    // if leaf is a food, add info to String list, add the String Array list and clear the String list
                    if (entry.getKey() instanceof Food) {

                        Food food = (Food) entry.getKey();
                        if (entryList.size() != 3) {
                            entryList.addAll(Arrays.asList(date.split("-")));
                        }

                        entryList.add(food.getName());
                        entryList.add(entry.getValue() + "");
                        setTotalCalories(food.getCalories());
                        getCaloriesForCLIDate(entryList, food);

                        finalList.add(entryList.toArray(new String[0]));
                        entryList.clear();

                    }

                    // if leaf is a recipe, set the String list to the function itself and do the process above again.
                    else if (entry.getKey() instanceof Recipe) {
                        entryList = infoTableRecursion(entry.getKey(), entryList, log, finalList, date);
                    }
                }
            }

            // once all leaves are foods, return list
            return entryList;
        }

        return null; // WILL NEVER BE REACHED DUE TO COMPONENTS ONLY BEING INSTANCES OF RECIPE OR FOOD
    }


    /**
     * Adds percentages of nutrients to a list.
     *
     * @param entryList : List<String> entryList
     * @param food      : Food
     */
    private void getCaloriesForCLIDate(List<String> entryList, Food food) {
        entryList.add(food.getCalories() + "");
        double nutrientsSum = food.getFat() + food.getCarb() + food.getProtein();
        entryList.add(String.format("%.2f", (food.getFat() / nutrientsSum) * 100) + "%");
        entryList.add(String.format("%.2f", (food.getCarb() / nutrientsSum) * 100) + "%");
        entryList.add(String.format("%.2f", (food.getProtein() / nutrientsSum * 100)) + "%");
    }

    /**
     * Write list to CSV.
     *
     * @param filename : String
     * @param list     : List<String[]>
     */
    public void writeNewDataCSV(String filename, List<String[]> list) {
        formatter.writeToCSV(filename, list);
    }

    /**
     * delete food from CSV based on name
     *
     * @param filename  : String
     * @param entryName : String
     */
    public void deleteSelectedFoodFromCSV(String filename, String entryName) {
        try {
            formatter.removeLineByName(filename, entryName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get total number of calories for the date.
     *
     * @return Double
     */
    public double getTotalCalories() {
        return totalCalories;
    }

    /**
     * Add calories to total calories count.
     *
     * @param calories : Double
     */
    public void setTotalCalories(double calories) {
        this.totalCalories += calories;
    }


    /**
     * Build List<List<String>> containing foods for user's input date.
     *
     * @param year  : String
     * @param month : String
     * @param day   : String
     * @return List<List < String>>
     */
    public List<List<String>> buildGUIInfoTable(String year, String month, String day) {
        List<List<String>> matrix = new ArrayList<>();
        List<String> entry;
        this.totalCalories = 0;
        for (Log log : this.logs) {
            if (log.getYear().equalsIgnoreCase(year) && log.getMonth().equalsIgnoreCase(month) && log.getDay().equalsIgnoreCase(day)) {
                for (FoodComponent foodComponent : this.foods) {
                    Food food = (Food) foodComponent;
                    if (food.getName().equalsIgnoreCase(log.getName())) {
                        entry = new ArrayList<>();
                        entry.add(food.getName());
                        entry.add(food.getCalories() + "");
                        totalCalories += food.getCalories();
                        entry.add(log.getServings() + "");
                        double sum = food.getFat() + food.getCarb() + food.getProtein();
                        int percFat = (int) Math.round(food.getFat() / sum) * 100;
                        int percCarb = (int) Math.round(food.getCarb() / sum) * 100;
                        int percProtein = (int) Math.round(food.getProtein() / sum) * 100;
                        entry.add(percFat + "%");
                        entry.add(percCarb + "%");
                        entry.add(percProtein + "%");
                        matrix.add(entry);
                    }
                }
            }
        }
        return matrix;
    }

    /**
     * Build List<List<String>> containing exercises for user's input date.
     *
     * @param year  : String
     * @param month : String
     * @param day   : String
     * @return List<List < String>>
     */
    public List<List<String>> buildGUIExerciseTable(String year, String month, String day) {
        List<List<String>> matrix = new ArrayList<>();
        List<String> entry;

        for (Log log : this.logs) {
            if (log.getYear().equalsIgnoreCase(year) && log.getMonth().equalsIgnoreCase(month) && log.getDay().equalsIgnoreCase(day)) {
                for (Exercise exercise : this.exercises) {
                    if (exercise.getName().equalsIgnoreCase(log.getName())) {
                        entry = new ArrayList<>();
                        entry.add(exercise.getName());
                        entry.add(exercise.getCalories() + "");
                        entry.add(log.getMinutes() + "");
                        matrix.add(entry);
                    }
                }
            }
        }

        for (List<String> list : matrix) {
            System.out.println(list.toString());
        }
        return matrix;
    }

    /**
     * Method that handles data from view.
     *
     * @param o   : Observable
     * @param arg : Object
     */
    @Override
    public void update(Observable o, Object arg) {
        LinkedHashMap<String, List<String>> map = (LinkedHashMap<String, List<String>>) arg;
        AtomicReference<ArrayList<String[]>> matrix = new AtomicReference<>();
        DateValidator dateValidator = new DateValidator("yyyy-MM-dd");

        map.forEach((k, v) -> {
            // ADD FOOD WITH CURRENT DATE
            if (k.equalsIgnoreCase("add food default")) {
                getCurrentDate();
                if (isNumberValid(v.subList(4, v.size()))) {
                    addFoodFromView(matrix, v);
                    matrix.get().add(new String[]{this.year, this.month, this.day, "f", v.get(3), v.get(8)});
                    addNewFoodToLog(matrix);
                } else {
                    view.createErrorMessage("Some fields are incorrectly formatted.", "Form Error.");
                }
            }
            // ADD FOOD WITH USER INPUT FOR DATE
            else if (k.equalsIgnoreCase("add food")) {
                if (dateValidator.isDateValid(v.get(0) + "-" + v.get(1) + "-" + v.get(2))) {
                    if (Integer.parseInt(v.get(0)) <= Integer.parseInt(this.year)) {
                        if (isNumberValid(v.subList(4, v.size()))) {
                            addFoodFromView(matrix, v);
                            matrix.get().add(new String[]{v.get(0), v.get(1), v.get(2), "f", v.get(3), v.get(8)});
                            addNewFoodToLog(matrix);
                        } else {
                            view.createErrorMessage("Some fields are incorrectly formatted.", "Form Error.");
                        }
                    } else {
                        view.createErrorMessage("Cannot add a food for a future date.", "Date Error.");
                    }
                } else {
                    view.createErrorMessage("Date not valid.", "Date Error.");
                }
            }

            //REMOVE FOOD FROM TABLE
            else if (k.equalsIgnoreCase("remove food")) {
                matrix.set(new ArrayList<>());
                FoodComponent foodToDelete = new Food(v.get(0), Double.parseDouble(v.get(1)), Double.parseDouble(v.get(2)), Double.parseDouble(v.get(3)), Double.parseDouble(v.get(4)));
                matrix.get().add(foodToDelete.toString().trim().split(","));
                try {
                    formatter.removeLineByName("foods.csv", foodToDelete.getName());
                } catch (IOException e) {
                    view.createErrorMessage("Error writing change from table.", "Internal Error.");
                }

                foods = parser.getFoodFromCSV();
                foods.sort(Comparator.comparing(FoodComponent::getName));
                view.updateTable(this.foods, CSVEnum.foods.returnCSVEnum(), true, true, true, true, true);
            }
            //ADD FOOD TO RECIPE
            else if (k.equalsIgnoreCase("add food to recipe")) {
                matrix.set(new ArrayList<>());
                String recipeName = v.get(0);
                double servings = Double.parseDouble(v.get(1));
                v.remove(0);
                v.remove(0);
                Recipe recipe = null;
                FoodComponent foodToAdd = new Food(v.get(0), Double.parseDouble(v.get(1)), Double.parseDouble(v.get(2)), Double.parseDouble(v.get(3)), Double.parseDouble(v.get(4)));

                for (FoodComponent component : this.recipes) {
                    if (component.getName().equalsIgnoreCase(recipeName)) {
                        component.addComponent(foodToAdd, servings);
                        recipe = (Recipe) component;
                    }
                }

                assert recipe != null;
                List<String> list = new ArrayList<>(Arrays.asList(recipe.toString().split(",")));
                list.add(0, "r");
                String[] newEntry = list.toArray(new String[0]);
                matrix.get().add(newEntry);

                try {
                    formatter.removeLineByName("foods.csv", recipe.getName());
                    formatter.writeToCSV("foods.csv", matrix.get());
                } catch (IOException e) {
                    view.createErrorMessage("Error writing change from table.", "Internal Error.");
                }
                this.recipes = parser.getRecipeFromCSV();
                this.recipes.sort(Comparator.comparing(FoodComponent::getName));
                view.updateTable(this.recipes, CSVEnum.recipes.returnCSVEnum(), false, false, false);
            }
            else if (k.equalsIgnoreCase("Remove Food From Recipe")) {
                // Row the user selected
                int rowIndex = Integer.parseInt(v.get(0));

                // Get indexes for the selected item
                int currentRowIndex = 0;
                int currentRecipeIndex = 0;
                int currentRecipeRowIndex = 0;
                int currentChildIndex = 0;
                while (currentRowIndex != rowIndex) {
                    Boolean goToNextRecipe = true;
                    for(currentChildIndex = 0; currentChildIndex < ((Recipe)recipes.get(currentRecipeIndex)).getAmountOfChildren(); currentChildIndex++) {
                        currentRowIndex++;
                        if(currentRowIndex == rowIndex) {
                            goToNextRecipe = false;
                            break;
                        }
                    }

                    if (goToNextRecipe) {
                        currentRecipeRowIndex += ((Recipe)recipes.get(currentRecipeIndex)).getAmountOfChildren() + 1;
                        currentRecipeIndex++;
                        currentRowIndex++;
                    }
                }

                if(currentRowIndex == currentRecipeRowIndex) {
                    // User tried to delete the actual recipe
                    return;
                } else {
                    // Create a list of the recipe without the selected item
                    String[] oldData = this.recipes.get(currentRecipeIndex).toString().split(",");
                    List<String> newDataList = new ArrayList<>();

                    newDataList.add("r");
                    newDataList.add(oldData[0]);
                    int ingredientNum = 0;
                    for(int i = 1; i < oldData.length; i++) {
                        if(ingredientNum == currentChildIndex) {
                            i++;
                            ingredientNum++;
                        } else {
                            if (i % 2 == 0) ingredientNum++;

                            newDataList.add(oldData[i]);
                        }
                    }

                    // Replace the old recipe
                    try {
                        formatter.replaceLineByName("foods.csv", oldData[0], newDataList.toArray(new String[0]));
                    } catch (Exception e) {
                        System.out.println("ERROR removing food from recipe");
                    }

                    // Update the table
                    this.recipes = parser.getRecipeFromCSV();
                    this.recipes.sort(Comparator.comparing(FoodComponent::getName));
                    view.updateTable(this.recipes, CSVEnum.recipes.returnCSVEnum(), false, false, false);
                }
            }

            //ADD LOG WITH CURRENT DATE
            else if (k.equalsIgnoreCase("add log default")) {
                matrix.set(new ArrayList<>());
                if (v.size() == 2) {
                    if (v.get(0).trim().equalsIgnoreCase("c") || v.get(0).trim().equalsIgnoreCase("w")) {
                        if (isNumberValid(Collections.singletonList(v.get(1)))) {
                            Log log = new Log(v.get(0), Double.parseDouble(v.get(1).trim()));
                            matrix.get().add(log.toString().split(","));

                        } else {
                            view.createErrorMessage("value is incorrectly formatted.", "Form Error.");
                        }
                    } else {
                        view.createErrorMessage("Type value must be either 'c' or 'w'.", "Form Error.");
                    }
                } else {
                    if (v.get(0).trim().equalsIgnoreCase("f") || v.get(0).trim().equalsIgnoreCase("e")) {
                        if (isNumberValid(Collections.singletonList(v.get(2)))) {
                            Log log = new Log(v.get(0), Double.parseDouble(v.get(2).trim()), v.get(1));
                            matrix.get().add(log.toString().split(","));
                        } else {
                            view.createErrorMessage("value is incorrectly formatted.", "Form Error.");
                        }
                    } else {
                        view.createErrorMessage("Type value must be either 'f' or 'e'.", "Form Error.");
                    }
                }

                formatter.writeToCSV("log.csv", matrix.get());
                logs = parser.getLogFromCSV();
                logs.sort(Comparator.comparing(Log::getYear));
                view.updateTable(this.logs, CSVEnum.log.returnCSVEnum(), true, true, true, false, true, true);
            }

            //ADD LOG WITH USER INPUT FOR DATE
            else if (k.equalsIgnoreCase("add log")) {
                matrix.set(new ArrayList<>());
                if (dateValidator.isDateValid(v.get(0) + "-" + v.get(1) + "-" + v.get(2))) {
                    if (Integer.parseInt(v.get(0)) <= Integer.parseInt(this.year)) {
                        if (v.size() == 5) {
                            if (v.get(3).trim().equalsIgnoreCase("c") || v.get(3).trim().equalsIgnoreCase("w")) {
                                if (isNumberValid(Collections.singletonList(v.get(4)))) {
                                    String[] date = new String[]{v.get(0), v.get(1), v.get(2)};
                                    Log log = new Log(v.get(3), date, Double.parseDouble(v.get(4).trim()));
                                    matrix.get().add(log.toString().split(","));
                                } else {
                                    view.createErrorMessage("value is incorrectly formatted.", "Form Error.");
                                }
                            } else {
                                view.createErrorMessage("Type value must be either 'c' or 'w'.", "Form Error.");
                            }
                        } else {
                            if (v.get(3).trim().equalsIgnoreCase("f") || v.get(3).trim().equalsIgnoreCase("e")) {
                                if (isNumberValid(Collections.singletonList(v.get(5)))) {
                                    Log log = new Log(v.get(3), Double.parseDouble(v.get(5).trim()), v.get(4));
                                    matrix.get().add(log.toString().split(","));
                                } else {
                                    view.createErrorMessage("value is incorrectly formatted.", "Form Error.");
                                }
                            } else {
                                view.createErrorMessage("Type value must be either 'f' or 'e'.", "Form Error.");
                            }
                        }
                    } else {
                        view.createErrorMessage("Cannot add a log for a future date.", "Date Error.");
                    }
                } else {
                    view.createErrorMessage("Date not valid.", "Date Error.");
                }
                formatter.writeToCSV("log.csv", matrix.get());
                logs = parser.getLogFromCSV();
                logs.sort(Comparator.comparing(Log::getYear));
                view.updateTable(this.logs, CSVEnum.log.returnCSVEnum(), true, true, true, false, true, true);
            }

            //REMOVE LOG FROM TABLE
            else if (k.equalsIgnoreCase("remove log")) {
                matrix.set(new ArrayList<>());
                Log log;
                if (v.size() == 5) {
                    log = new Log(v.get(3), new String[]{v.get(0), v.get(1), v.get(2)}, Double.parseDouble(v.get(4)));
                } else {
                    log = new Log(v.get(3), new String[]{v.get(0), v.get(1), v.get(2)}, Double.parseDouble(v.get(5)), v.get(4));
                }
                matrix.get().add(log.toString().trim().split(","));
                try {
                    formatter.removeLogFromCSV(log);
                } catch (IOException e) {
                    view.createErrorMessage("Error writing change from table.", "Internal Error.");
                }

                logs = parser.getLogFromCSV();
                logs.sort(Comparator.comparing(Log::getYear));
                view.updateTable(this.logs, CSVEnum.log.returnCSVEnum(), true, true, true, false, true, true);
            }

            // ADD EXERCISE
            else if (k.equalsIgnoreCase("add exercise")) {
                matrix.set(new ArrayList<>());
                if (isNumberValid(Collections.singletonList(v.get(1)))) {

                    Exercise exercise = new Exercise(v.get(0),Double.parseDouble(v.get(1)));
                    matrix.get().add(new String[] { "e", exercise.getName(), exercise.getCalories() + "" });
                    formatter.writeToCSV("exercise.csv", matrix.get());

                    if (exercises.size() != parser.getDataFromCSV("exercise.csv").size()) {
                        exercises.add(exercise);
                        exercises = parser.getExerciseFromCSV();
                        exercises.sort(Comparator.comparing(Exercise::getName));
                        view.updateTable(this.exercises, CSVEnum.exercises.returnCSVEnum(), true, true);
                        view.disposeJDialog();
                    } else {
                        view.createErrorMessage("Entry is already present.", "Duplicate Entry Error.");
                    }
                } else {
                    view.createErrorMessage("Values is not correctly formatted","Form Error");
                }
            }

            //REMOVE Exercise
            else if (k.equalsIgnoreCase("Remove Exercise")) {
                view.createRemoveExerciseDialog(exercises);
            }
            else if (k.equalsIgnoreCase("Remove Exercise-READY")) {
                for (String exercise : v) {
                    try {
                        formatter.removeLineByName("exercise.csv", exercise);
                    } catch (Exception e) {
                        System.out.println("ERROR removing exercises");
                    }
                }
                exercises = parser.getExerciseFromCSV();
                view.updateTable(this.exercises,CSVEnum.exercises.returnCSVEnum(),true, true);
            }

            //CHANGE DESIRED WEIGHT
            else if (k.equalsIgnoreCase("change desired weight")) {
                boolean isFound;
                if (isNumberValid(Collections.singletonList(v.get(v.size() - 1)))) {
                    if (v.subList(0, 3).stream().allMatch(x -> x.equalsIgnoreCase(""))) {
                        getCurrentDate();
                        String currentDate = this.year + "-" + this.month + "-" + this.day;
                        isFound = handleWeightChange(v, false, currentDate);

                        if (!isFound) {
                            view.createErrorMessage("Preferred Weight for " + this.year + "-" + this.month + "-" + this.day +
                                    " does not exist. Creating it.", "Creating Entry.");
                            UserData userData = new UserData(this.year, this.month, this.day, 2000.0, Double.parseDouble(v.get(v.size() - 1)));
                            usersData.add(userData);
                            try {
                                formatter.writeNewUserData(userData);
                            } catch (IOException e) {
                                view.createErrorMessage("Error writing new weight entry to csv.", "Internal Error.");
                            }
                        }

                    } else if (v.subList(0, 3).stream().noneMatch(x -> x.equalsIgnoreCase(""))) {
                        if (dateValidator.isDateValid(v.get(0) + "-" + v.get(1) + "-" + v.get(2))) {
                            String dateFromUser = v.get(0) + "-" + v.get(1) + "-" + v.get(2);
                            isFound = handleWeightChange(v, false, dateFromUser);
                            if (!isFound) {
                                view.createErrorMessage("Preferred Weight for " + v.get(0) + "-" + v.get(1) + "-" + v.get(2) +
                                        " does not exist. Creating it.", "Creating Entry.");
                                UserData userData = new UserData(v.get(0), v.get(1), v.get(2), 2000.0, Double.parseDouble(v.get(v.size() - 1)));
                                usersData.add(userData);
                                try {
                                    formatter.writeNewUserData(userData);
                                } catch (IOException e) {
                                    view.createErrorMessage("Error writing new weight entry to csv.", "Internal Error.");
                                }
                            }
                        } else {
                            view.createErrorMessage("Date not valid.", "Date Error.");
                        }
                    } else {
                        view.createErrorMessage("Date must be either empty or specified.", "Form Error.");
                    }
                } else {
                    view.createErrorMessage("Weight value format is not correct.", "Form Error.");
                }

                logs = parser.getLogFromCSV();
                logs.sort(Comparator.comparing(Log::getYear));
                view.updateTable(this.logs, CSVEnum.log.returnCSVEnum(), true, true, true, false, true, true);
            }
            //CHANGE DESIRED CALORIES
            else if (k.equalsIgnoreCase("change desired calories")) {
                boolean isFound;
                if (isNumberValid(Collections.singletonList(v.get(v.size() - 1)))) {
                    if (v.subList(0, 3).stream().allMatch(x -> x.equalsIgnoreCase(""))) {
                        getCurrentDate();
                        String currentDate = this.year + "-" + this.month + "-" + this.day;
                        isFound = handleCaloriesChange(v, false, currentDate);

                        if (!isFound) {
                            view.createErrorMessage("Preferred calories for " + this.year + "-" + this.month + "-" + this.day +
                                    " does not exist. Creating it.", "Creating Entry.");
                            UserData userData = new UserData(this.year, this.month, this.day, Double.parseDouble(v.get(v.size() - 1)), 150.0);
                            usersData.add(userData);
                            try {
                                formatter.writeNewUserData(userData);
                            } catch (IOException e) {
                                view.createErrorMessage("Error writing new calories entry to csv.", "Internal Error.");
                            }
                        }

                    } else if (v.subList(0, 3).stream().noneMatch(x -> x.equalsIgnoreCase(""))) {
                        if (dateValidator.isDateValid(v.get(0) + "-" + v.get(1) + "-" + v.get(2))) {
                            String dateFromUser = v.get(0) + "-" + v.get(1) + "-" + v.get(2);
                            isFound = handleCaloriesChange(v, false, dateFromUser);
                            if (!isFound) {
                                view.createErrorMessage("Preferred calories for " + v.get(0) + "-" + v.get(1) + "-" + v.get(2) +
                                        " does not exist. Creating it.", "Creating Entry.");
                                UserData userData = new UserData(v.get(0), v.get(1), v.get(2), Double.parseDouble(v.get(v.size() - 1)), 150.0);
                                usersData.add(userData);
                                try {
                                    formatter.writeNewUserData(userData);
                                } catch (IOException e) {
                                    view.createErrorMessage("Error writing new calories entry to csv.", "Internal Error.");
                                }
                            }
                        } else {
                            view.createErrorMessage("Date not valid.", "Date Error.");
                        }
                    } else {
                        view.createErrorMessage("Date must be either empty or specified.", "Form Error.");
                    }
                } else {
                    view.createErrorMessage("Calories value format is not correct.", "Form Error.");
                }

                logs = parser.getLogFromCSV();
                logs.sort(Comparator.comparing(Log::getYear));
                view.updateTable(this.logs, CSVEnum.log.returnCSVEnum(), true, true, true, false, true, true);
            }
            // UPDATE LOG TABLE
            else if (k.equalsIgnoreCase("Log Table")) {
                view.updateTable(this.logs, CSVEnum.log.returnCSVEnum(), true, true, true, false, true, true);
                this.view.createPopupMenu("Add Log", "Remove Log");
            }
            // UPDATE RECIPE TABLE
            else if (k.equalsIgnoreCase("Recipe Table")) {
                this.recipes = parser.getRecipeFromCSV();
                this.recipes.sort(Comparator.comparing(FoodComponent::getName));
                view.updateTable(this.recipes, CSVEnum.recipes.returnCSVEnum(), false, false, false);
                this.view.createPopupMenu("Add Recipe", "Remove Recipe", "Remove Food From Recipe");
            }
            // UPDATE FOOD TABLE
            else if (k.equalsIgnoreCase("Food Table")) {
                view.updateTable(this.foods, CSVEnum.foods.returnCSVEnum(), true, true, true, true, true);
                this.view.createPopupMenu("Add Food", "Remove Food", "Add Food to Recipe");
            }

            // UPDATE EXERCISE TABLE
            else if (k.equalsIgnoreCase("Exercise Table")) {
                view.updateTable(this.exercises, CSVEnum.exercises.returnCSVEnum(), true, true);
                this.view.createPopupMenu("Add Exercise", "Remove Exercise");
            }

            // UPDATE FOOD TABLE WHEN USER CHANGES A VALUE. IT'S REFLECTED IN THE CSV FILE
            else if (k.equalsIgnoreCase("food entry")) {
                matrix.set(new ArrayList<>());
                if (isDataInList(this.foods, v)) {
                    v.remove(0);
                    Food newFood = new Food(v.get(0), Double.parseDouble(v.get(1)),
                            Double.parseDouble(v.get(2)), Double.parseDouble(v.get(3)), Double.parseDouble(v.get(4)));

                    String[] newEntry = new String[]{
                            "b", v.get(0), v.get(1), v.get(2), v.get(3), v.get(4)
                    };
                    matrix.get().add(newEntry);

                    try {
                        formatter.removeLineByName("foods.csv", newFood.getName());
                        formatter.writeToCSV("foods.csv", matrix.get());
                    } catch (IOException e) {
                        view.createErrorMessage("Error writing change from table.", "Internal Error.");
                    }
                    foods = parser.getFoodFromCSV();
                    foods.sort(Comparator.comparing(FoodComponent::getName));
                    view.updateTable(this.foods, CSVEnum.foods.returnCSVEnum(), true, true, true, true, true);
                }
            }
            // UPDATE LOG TABLE WHEN USER CHANGES A VALUE. IT'S REFLECTED IN THE CSV FILE
            else if (k.equalsIgnoreCase("log entry")) {
                matrix.set(new ArrayList<>());
                if (isDataInList(this.logs, v)) {
                    int rowIndex = Integer.parseInt(v.get(0));
                    v.remove(0);
                    String[] date = new String[]{v.get(0), v.get(1), v.get(2)};
                    String[] newEntry;
                    Log newLog;
                    if (v.get(3).equalsIgnoreCase("f")) {
                        newLog = new Log(v.get(3), date, Double.parseDouble(v.get(5)), v.get(4));
                        newEntry = new String[]{
                                v.get(0), v.get(1), v.get(2), v.get(3), v.get(4), v.get(5)
                        };
                    } else if (v.get(3).equalsIgnoreCase("e")) {
                        newLog = new Log(v.get(3), date, Double.parseDouble(v.get(5)), v.get(4));
                        newEntry = new String[]{
                                v.get(0), v.get(1), v.get(2), v.get(3), v.get(4), v.get(5)
                        };
                    } else {
                        newLog = new Log(v.get(3), date, Double.parseDouble(v.get(4)));
                        newEntry = new String[]{
                                v.get(0), v.get(1), v.get(2), v.get(3), v.get(4)
                        };
                    }
                    matrix.get().add(newEntry);

                    try {
                        formatter.replaceLog(logs.get(rowIndex), newLog);
                    } catch (IOException e) {
                        view.createErrorMessage("Error writing change from table.", "Internal Error.");
                    }
                    logs = parser.getLogFromCSV();
                    logs.sort(Comparator.comparing(Log::getYear));
                    view.updateTable(this.logs, CSVEnum.log.returnCSVEnum(), true, true, true, false, true, true);
                }
            }
            //UPDATE EXERCISE TABLE WHEN USER CHANGES A VALUE. IT'S REFLECTED IN THE CSV FILE
            else if (k.equalsIgnoreCase("exercise entry")) {

                matrix.set(new ArrayList<>());
                if (isDataInList(this.exercises, v)) {
                    int rowIndex = Integer.parseInt(v.get(0));
                    v.set(0, "e");
                    String[] newEntry = new String[]{v.get(0), v.get(1), v.get(2)};
                    matrix.get().add(newEntry);

                    try {
                        formatter.removeLineByName("exercise.csv", this.exercises.get(rowIndex).getName());
                        formatter.writeToCSV("exercise.csv", matrix.get());
                    } catch (IOException e) {
                        view.createErrorMessage("Error writing change from table.", "Internal Error.");
                        e.printStackTrace();
                    }
                    exercises = parser.getExerciseFromCSV();
                    exercises.sort(Comparator.comparing(Exercise::getName));
                    view.updateTable(this.exercises, CSVEnum.exercises.returnCSVEnum(), true, true, true);
                }

            }
            //TODO: RECIPE TABLE WHEN USER CHANGES A VALUE. IT'S REFLECTED IN THE CSV FILE
            else if (k.equalsIgnoreCase("recipe entry")) {
                matrix.set(new ArrayList<>());
            }


            // UPDATE TABLE WITH DATA FROM USER INPUT DATE
            else if (k.equalsIgnoreCase("Info For Date")) {
                if (v.get(0) != null && v.get(1) != null && v.get(2) != null) {
                    boolean isFound = false;
                    List<List<String>> foodList = buildGUIInfoTable(v.get(0), v.get(1), v.get(2));
                    List<List<String>> exerciseList = buildGUIExerciseTable(v.get(0), v.get(1), v.get(2));
                    String dateFromUser = v.get(0) + "-" + v.get(1) + "-" + v.get(2);
                    System.out.println("From user: " + dateFromUser);
                    if (foodList.size() > 0 || exerciseList.size() > 0) {
                        for (UserData userData : usersData) {
                            String date = (userData.getYear() + "-" + userData.getMonth() + "-" + userData.getDay());
                            System.out.println("From list: " + date);
                            if (date.equalsIgnoreCase(dateFromUser) && userData.getDesiredDailyCalories() > 0.0) {
                                view.createDialogForDate(createChartForDate(v.get(0), v.get(1), v.get(2)), foodList, exerciseList,
                                        getCaloriesForDate(v.get(0), v.get(1), v.get(2)), userData.getDesiredDailyCalories(), CSVEnum.infoByDateGUI.returnCSVEnum(),
                                        false, false, false, false, false, false);
                                isFound = true;
                                break;
                            }
                        }
                        if (!isFound) {
                            view.createErrorMessage("Desired calories not set.", "Internal Error.");
                            view.createJDialogForUserData("Preferred Calories: ", "change desired calories");
                        }

                    } else {
                        view.createErrorMessage("No data for selected date.", "DatePicker Error.");
                    }

                } else {
                    view.createErrorMessage("Please select a date.", "DatePicker Error.");
                }
            }
            //ADD RECIPE
            else if (k.equalsIgnoreCase("Add Recipe")) {
                view.createAddRecipeDialog(new ArrayList<>() {{
                    addAll(foods);
                    addAll(recipes);
                }});
            }
            else if (k.equalsIgnoreCase("Add Recipe-READY")) {
                List<String[]> data = new ArrayList<>();
                data.add(v.toArray(new String[0]));
                formatter.writeToCSV("foods.csv", data);
            }
            //REMOVE RECIPE
            else if (k.equalsIgnoreCase("Remove Recipe")) {
                view.createRemoveRecipeDialog(recipes);
            }
            else if (k.equalsIgnoreCase("Remove Recipe-READY")) {
                for (String recipe : v) {
                    try {
                        formatter.removeLineByName("foods.csv", recipe);
                    } catch (Exception e) {
                        System.out.println("ERROR removing recipes");
                    }
                }
            }
        });
    }

    private boolean handleCaloriesChange(List<String> v, boolean isFound, String currentDate) {
        for (UserData userData : usersData) {
            String date = (userData.getYear() + "-" + userData.getMonth() + "-" + userData.getDay());
            if (date.equalsIgnoreCase(currentDate)) {
                userData.setDesiredDailyCalories(Double.parseDouble(v.get(v.size() - 1)));
                try {
                    formatter.updateUserData(userData);
                } catch (IOException e) {
                    view.createErrorMessage("Error writing calories change to csv.", "Internal Error.");
                }
                view.disposeJDialog();
                isFound = true;
                break;
            }

        }
        return isFound;
    }

    private boolean handleWeightChange(List<String> v, boolean isFound, String currentDate) {
        for (UserData userData : usersData) {
            String date = (userData.getYear() + "-" + userData.getMonth() + "-" + userData.getDay());
            if (date.equalsIgnoreCase(currentDate)) {
                userData.setDesiredWeight(Double.parseDouble(v.get(v.size() - 1)));
                try {
                    formatter.updateUserData(userData);
                } catch (IOException e) {
                    view.createErrorMessage("Error writing weight change to csv.", "Internal Error.");
                }
                view.disposeJDialog();
                isFound = true;
                break;
            }

        }
        return isFound;
    }

    /**
     * Add new food to logs and log.csv.
     *
     * @param matrix : AtomicReference<ArrayList<String[]>>
     */
    private void addNewFoodToLog(AtomicReference<ArrayList<String[]>> matrix) {
        formatter.writeToCSV("log.csv", matrix.get());
        if (logs.size() != parser.getDataFromCSV("log.csv").size()) {
            logs = parser.getLogFromCSV();
            logs.sort(Comparator.comparing(Log::getYear));
            view.updateTable(this.logs, CSVEnum.log.returnCSVEnum(), true, true, true, false, true, true);
        } else {
            view.createErrorMessage("Entry is already present.", "Duplicate Entry Error.");
        }
    }

    /**
     * Populate a matrix, writes it to the csv and updates local list.
     *
     * @param matrix : AtomicReference<ArrayList<String[]>>
     * @param v      : List<String>
     */
    private void addFoodFromView(AtomicReference<ArrayList<String[]>> matrix, List<String> v) {
        matrix.set(new ArrayList<>());
        matrix.get().add(new String[]{"b", v.get(3), Double.parseDouble(v.get(4)) + "",
                Double.parseDouble(v.get(5)) + "", Double.parseDouble(v.get(6)) + "", Double.parseDouble(v.get(7)) + ""});
        formatter.writeToCSV("foods.csv", matrix.get());
        if (foods.size() != parser.getDataFromCSV("foods.csv").size()) {
            foods = parser.getFoodFromCSV();
            foods.sort(Comparator.comparing(FoodComponent::getName));
            view.updateTable(this.foods, CSVEnum.foods.returnCSVEnum(), true, true, true, true, true);
        }
        if (recipes.size() != parser.getDataFromCSV("foods.csv").size()) {
            recipes = parser.getRecipeFromCSV();
        }

        matrix.set(new ArrayList<>());

    }


    /**
     * Check if List contains a List<String>.
     *
     * @param list : List<T>
     * @param data : List<String>
     * @param <T>  : Generic
     * @return boolean
     */
    private <T> boolean isDataInList(List<T> list, List<String> data) {
        boolean isValid = true;
        for (T t : list) {
            String[] listRow = t.toString().split(",");
            if (Arrays.equals(listRow, data.toArray())) {
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * Check if Strings can be converted to Doubles.
     *
     * @param numbers : List<String>
     * @return boolean
     */
    private boolean isNumberValid(List<String> numbers) {
        try {
            for (String number : numbers) {
                Double.parseDouble(number);
            }
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }


    /**
     * Class that contains method to validate date Strings
     */
    private static class DateValidator {
        private final String dateFormat;

        /**
         * Parameterized Constructor.
         *
         * @param dateFormat : String
         */
        private DateValidator(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        /**
         * Check if date String is valid.
         *
         * @param dateString : String
         * @return boolean
         */
        private boolean isDateValid(String dateString) {
            DateFormat df = new SimpleDateFormat(this.dateFormat);
            df.setLenient(false);
            try {
                df.parse(dateString);
            } catch (ParseException pe) {
                return false;
            }

            return true;
        }
    }

}




