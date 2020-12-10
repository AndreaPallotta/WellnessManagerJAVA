import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class containing the information about the user
 *
 * @version 1.0 Final
 */
public class UserData {
    private double desiredCalories;
    private double desiredWeight;
    private final String year;
    private final String month;
    private final String day;


    public UserData(String year, String month, String day, double desiredCalories, double desiredWeight) {
        this.desiredCalories = desiredCalories;
        this.desiredWeight = desiredWeight;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * Sets desired weight limit for specific date.
     *
     * @param desiredWeight : Double
     */
    public void setDesiredWeight(double desiredWeight) {
        this.desiredWeight = desiredWeight;
    }

    /**
     * Shows the user the desired daily calories limit for requested date.
     *
     * @return Double
     */
    public double getDesiredDailyCalories() {
        return this.desiredCalories;
    }

    /**
     * Sets desired daily calorie limit for specific date.
     *
     * @param desiredCalories : Double
     */
    public void setDesiredDailyCalories(double desiredCalories) {

        this.desiredCalories = desiredCalories;

    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    /**
     * Shows the user the desired weight limit for requested date.
     *
     * @return Double
     */
    public double getDesiredDailyWeight() {
        return this.desiredWeight;
    }

    @Override
    public String toString() {
        return this.getYear() + "," + this.getMonth() + "," + this.getDay() + "," +
                this.getDesiredDailyCalories() + "," + this.getDesiredDailyWeight();

    }
}
