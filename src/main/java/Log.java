import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Class containing the info about user logs
 *
 * @author Andrea Pallotta
 * @version 1.0 Skeleton
 */
public class Log {
    private final String letter;
    private String year;
    private String month;
    private String day;
    private double weight;
    private double calories;
    private String name;
    private double servings;
    private double minutes;

    /**
     * constructor
     *
     * @param letter : String
     * @param num    : Double
     * @param names  : String[]
     */
    public Log(String letter, double num, String... names) {
        String[] date = getCurrentDate();
        this.year = date[0];
        this.month = date[1];
        this.day = date[2];
        this.letter = letter.trim();

        if (this.letter.equalsIgnoreCase("w")) {
            this.weight = num;
        } else if (this.letter.equalsIgnoreCase("c")) {
            this.calories = num;
        } else if (this.letter.equalsIgnoreCase("f")) {
            this.name = names[0];
            this.servings = num;
        } else if (this.letter.equalsIgnoreCase("e")) {
            this.name = names[0];
            this.minutes = num;
        } else {
            System.out.println("Letter not recognized.");
        }
    }

    public Log(String letter, String[] dateValues, double num, String... names) {
        this.year = dateValues[0];
        this.month = dateValues[1];
        this.day = dateValues[2];
        this.letter = letter;

        if (letter.equalsIgnoreCase("w")) {
            this.weight = num;
        } else if (letter.equalsIgnoreCase("c")) {
            this.calories = num;
        } else if (letter.equalsIgnoreCase("f")) {
            this.name = names[0];
            this.servings = num;
        } else if (this.letter.equalsIgnoreCase("e")) {
            this.name = names[0];
            this.minutes = num;
        } else {
            System.out.println("Letter not recognized.");
        }
    }

    /**
     * Get year.
     *
     * @return String
     */
    public String getYear() {
        return this.year;
    }

    /**
     * Set year.
     *
     * @param year : String
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Get month.
     *
     * @return String
     */
    public String getMonth() {
        return this.month;
    }

    /**
     * Set month.
     *
     * @param month : String
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Get day.
     *
     * @return String
     */
    public String getDay() {
        return this.day;
    }

    /**
     * Set day.
     *
     * @param day : String
     */
    public void setDay(String day) {
        this.day = day;
    }

    /**
     * Get weight.
     *
     * @return Double
     */
    public double getWeight() {
        return this.weight;
    }

    /**
     * Get calories.
     *
     * @return Double
     */
    public double getCalories() {
        return this.calories;
    }

    /**
     * Get name.
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * get number of servings
     *
     * @return Double
     */
    public double getServings() {
        return this.servings;
    }

    /**
     * Get letter.
     *
     * @return String
     */
    public String getLetter() {
        return this.letter;
    }

    /**
     * Get minutes.
     *
     * @return Double
     */
    public double getMinutes() {
        return this.minutes;
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
     * Formats the line for entry into the csv file
     *
     * @return String
     */
    @Override
    public String toString() {
        String build = "";
        if (this.getLetter().equals("w")) {
            build = this.getYear() + "," + this.getMonth() + "," + this.getDay() + "," +
                    this.getLetter() + "," + this.getWeight();
        } else if (this.getLetter().equalsIgnoreCase("c")) {
            build = this.getYear() + "," + this.getMonth() + "," + this.getDay() + "," +
                    this.getLetter() + "," + this.getCalories();
        } else if (this.getLetter().equalsIgnoreCase("f")) {
            build = this.getYear() + "," + this.getMonth() + "," + this.getDay() + "," +
                    this.getLetter() + "," + this.getName() + "," + this.getServings();
        } else if (this.getLetter().equalsIgnoreCase("e")) {
            build = this.getYear() + "," + this.getMonth() + "," + this.getDay() + "," +
                    this.getLetter() + "," + this.getName() + "," + this.getCalories();
        }

        return build;

    }


}
