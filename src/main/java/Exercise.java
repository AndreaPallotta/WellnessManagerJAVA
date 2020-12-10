import java.util.List;
import java.util.Map;

/**
 * Class containing the information about exercises.
 *
 * @author Andrea Pallotta, John Mule
 * @version 2.0 Final
 */

public class Exercise {
    private String name;
    private double calories;


    public Exercise(String name, double calories) {
        this.name = name;
        this.calories = calories;
    }

    /**
     * Get exercise name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Get calories expended for exercise.
     *
     * @return Double
     */
    public double getCalories() {
        return calories;
    }

    /**
     * Set name of exercise.
     *
     * @param name : String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set calories expended for exercise.
     *
     * @param calories : Double
     */
    public void setCalories(double calories) {
        this.calories = calories;
    }

    /**
     * Return Exercise object as a String.
     *
     * @return String
     */
    @Override
    public String toString() {
        return this.getName() + "," + this.getCalories();
    }




}
