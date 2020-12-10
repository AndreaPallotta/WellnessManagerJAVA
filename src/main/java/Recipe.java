import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class containing the information about recipes.
 *
 * @author Andrea Pallotta, Justin Nauta
 * @version 1.0 Final
 */
public class Recipe implements FoodComponent {
    private final Map<FoodComponent, Double> children;
    private String name;

    /**
     * Constructor
     *
     * @param name : String
     */
    public Recipe(String name, Map<FoodComponent, Double> children) {
        this.name = name;
        this.children = children;
    }

    public String[] createRecipeEntry() {
        List<String> list = new ArrayList<>();
        String RECIPE_LETTER = "r";
        list.add(RECIPE_LETTER);
        list.add(this.getName());
        if (!this.children.isEmpty()) {
            for (Map.Entry<FoodComponent, Double> entry : this.children.entrySet()) {
                list.add(entry.getKey().getName());
                list.add(entry.getValue() + "");
            }
        }

        return list.toArray(new String[0]);

    }

    public Map<FoodComponent, Double> getChildren() {
        return children;
    }

    public int getAmountOfChildren() { return children.size(); }

    // Implemented from Component:
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addComponent(FoodComponent component, double quantity) {
        children.put(component, quantity);
    }

    public void removeComponent(FoodComponent component) {
        children.remove(component);
    }

    public Map<FoodComponent, Double> getChild(int index) {
        int count = 0;
        for (Map.Entry<FoodComponent, Double> entry : this.children.entrySet()) {
            if (count == index) {
                return Map.of(entry.getKey(), entry.getValue());
            } else {
                count++;
            }
        }
        return null;
    }

    public String getChildFoodComponent(String name) {
        for (Map.Entry<FoodComponent, Double> entry : this.children.entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase(name)) {
                return entry.getKey().getName();
            }
        }

        return null;
    }


    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(name + ",");
        for (Map.Entry<FoodComponent, Double> entry : this.children.entrySet()) {
            string.append(entry.getKey().getName()).append(",").append(entry.getValue()).append(",");
        }
        // remove last comma
        string.deleteCharAt(string.length() - 1);
        return string.toString();
    }
}
