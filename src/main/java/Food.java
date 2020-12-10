import java.util.List;
import java.util.Map;

/**
 * Class containing the information about foods.
 *
 * @author Andrea Pallotta, Justin Nauta
 * @version 1.0 Final
 */
public class Food implements FoodComponent {
    private final double calories;
    private final double fat;
    private final double carb;
    private final double protein;
    private String name;

    /**
     * Parameterized Constructor.
     *
     * @param name     : String
     * @param calories : Double
     * @param fat      : Double
     * @param carb     : Double
     * @param protein  : Double
     */
    public Food(String name, double calories, double fat, double carb, double protein) {
        super();
        this.name = name;
        this.calories = calories;
        this.fat = fat;
        this.carb = carb;
        this.protein = protein;
    }

    /**
     * Get calories.
     *
     * @return Double
     */
    public double getCalories() {
        return calories;
    }

    /**
     * Get fat.
     *
     * @return Double
     */
    public double getFat() {
        return fat;
    }

    /**
     * Get carb.
     *
     * @return Double
     */
    public double getCarb() {
        return carb;
    }

    /**
     * Get protein.
     *
     * @return Double
     */
    public double getProtein() {
        return protein;
    }

    /**
     * Get name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Set name.
     *
     * @param name : String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Add Component.
     *
     * @param component : Component
     * @param quantity  : Double
     */
    public void addComponent(FoodComponent component, double quantity) { /* Leaf object, nothing to implement */ }

    /**
     * Remove Component.
     *
     * @param component : Component
     */
    public void removeComponent(FoodComponent component) { /* Leaf object, nothing to implement */ }

    /**
     * Get child.
     *
     * @param index : Integer
     * @return null
     */
    public Map<FoodComponent, Double> getChild(int index) { /* Leaf object, nothing to implement */
        return null;
    }

    /**
     * Return Food object as a String.
     *
     * @return String
     */
    @Override
    public String toString() {
        return this.getName() + "," + this.getCalories() + "," + this.getFat() + "," +
                this.getCarb() + "," + this.getProtein();
    }
}