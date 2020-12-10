import java.util.List;
import java.util.Map;


/**
 * Interface implemented by foods and recipes as part of the composite pattern.
 *
 * @author Justin Nauta
 * @version 1.0 Final
 */
public interface FoodComponent {
    /**
     * Get the component's name.
     *
     * @return String
     */
    String getName();

    /**
     * Set the component's name.
     *
     * @param name : String
     */
    void setName(String name);

    /**
     * Add a new component to a composite object.
     *
     * @param component : Component
     */
    void addComponent(FoodComponent component, double quantity);

    /**
     * Remove a component from a composite object.
     *
     * @param component : Component
     */
    void removeComponent(FoodComponent component);

    /**
     * Finds an object of component type at a given index
     *
     * @param index : int
     * @return Map<Component, Double>
     */
    Map<FoodComponent, Double> getChild(int index);


}
