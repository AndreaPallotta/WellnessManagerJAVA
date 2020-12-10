import java.util.Arrays;

/**
 * Enum class containing the table's column titles for the JTable.
 *
 * @author Andrea Pallotta
 * @version 1.0 Final
 */
public enum CSVEnum {
    foods("Name", "Calories", "Fat", "Carb", "Protein"),
    recipes("Recipe", "Ingredients", "Servings"),
    log("Year", "Month", "Day", "Type", "Object", "Portions"),
    exercises("Name","Calories"),
    infoByDate("Year", "Month", "Day", "Name", "Servings", "Calories", "% fats", "% carbs", "% proteins"),
    infoByDateGUI("Name", "Calories", "Servings", "% fats", "% carbs", "% proteins", "Name", "Minutes", "Calories");
    public final String[] constants;

    /**
     * Enum setter.
     *
     * @param constants : String...
     */
    CSVEnum(String... constants) {
        this.constants = constants;
    }

    /**
     * Enum getter.
     *
     * @return String[]
     */
    public String[] returnCSVEnum(int... index) {
        if (index.length > 0) {
            return Arrays.copyOfRange(constants, 0, index[0]);
        } else {
            return Arrays.toString(constants).split(",");
        }

    }
}
