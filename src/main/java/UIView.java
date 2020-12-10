import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Class containing the JFrame and other graphic components
 *
 * @author Andrea Pallotta, John Mule, Justin Nauta
 * @version 2.0
 * FUTURE IMPLEMENTATION. NOT REQUIRED FOR THE SKELETON.
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class UIView extends Observable implements ActionListener {

    private final JFrame jFrame;
    private final JPanel jpCalendar;
    private JDialog jDialog;
    private final JScrollPane jsPane;
    private JTable jTable;
    private String year, month, day;
    private final Map<String, List<String>> mapToSend;
    private String[] recipeNames;

    /**
     * Default constructor. Creates and shows JFrame.
     */
    public UIView() {
        this.jFrame = new JFrame();
        this.jFrame.setTitle("Wellness Manager");
        this.jFrame.getContentPane().setLayout(new BorderLayout());
        JPanel jpTitle = new JPanel();
        jpTitle.add(new JLabel("Wellness Manager"), BorderLayout.NORTH);
        this.jFrame.add(jpTitle, BorderLayout.NORTH);
        this.jpCalendar = new JPanel(new GridLayout(0, 1, 10, 10));
        this.jpCalendar.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.jsPane = new JScrollPane();
        this.jsPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.mapToSend = new LinkedHashMap<>();
        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.setBorder(BorderFactory.createDashedBorder(Color.BLACK));
        createAndAddMenu("Food", jMenuBar, createItemsForMenu("Add Food"));
        createAndAddMenu("Recipe", jMenuBar, createItemsForMenu("Add Recipe", "Remove Recipe"));
        createAndAddMenu("Log", jMenuBar, createItemsForMenu("Add Log"));
        createAndAddMenu("Exercise", jMenuBar, createItemsForMenu("Add Exercise", "Remove Exercise"));
        createAndAddMenu("Goal Info", jMenuBar, createItemsForMenu("Change Weight", "Change Calories"));
        this.jFrame.setJMenuBar(jMenuBar);
        UtilDateModel dateModel = new UtilDateModel();
        buildCalendar(dateModel);
        this.jFrame.getContentPane().setPreferredSize(new Dimension(950, 400));
        this.jFrame.pack();
        this.jFrame.setVisible(true);
        this.jFrame.setLocationRelativeTo(null);
        this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Set recipe names.
     *
     * @param recipeNames : String[]
     */
    public void setRecipeNames(String[] recipeNames) {
        this.recipeNames = recipeNames;
    }

    /**
     * Create BarChart for main JFrame.
     *
     * @param title : String
     * @param map   : Map<String, Double>
     */
    public void createOverallBarChart(String title, Map<String, Double> map) {
        ChartPanel chartPanel = buildChartPanel(title, map);
        this.jFrame.getContentPane().add(chartPanel, BorderLayout.CENTER);
    }

    /**
     * Create BarChart based on user's input date.
     *
     * @param jDialog : JDialog
     * @param title   : String
     * @param map     : Map<String, Double>
     */
    public void createBarChartForDate(JDialog jDialog, String title, Map<String, Double> map) {
        ChartPanel chartPanel = buildChartPanel(title, map);
        jDialog.add(chartPanel);
    }

    /**
     * Build ChartPanel for BarChart.
     *
     * @param title : String
     * @param map   : Map<String, Double>
     * @return ChartPanel
     */
    public ChartPanel buildChartPanel(String title, Map<String, Double> map) {
        DefaultCategoryDataset barChartData = new DefaultCategoryDataset();

        map.forEach((k, v) -> {
            barChartData.setValue(v, k, k);
        });

        JFreeChart barChart = ChartFactory.createBarChart(title, "Nutrients", "Amount",
                barChartData, PlotOrientation.VERTICAL, true, true, false);

        CategoryPlot categoryPlot = barChart.getCategoryPlot();

        categoryPlot.setRangeGridlinePaint(Color.ORANGE);

        return new ChartPanel(barChart);
    }


    /**
     * Create JRadioButtons and append it to an AWT Container.
     *
     * @param jpPanel : JPanel
     */
    public void createRadioGroup(JPanel jpPanel) {
        JPanel radioPanel = new JPanel();
        JRadioButton jrbFoodTable = new JRadioButton("Food Table", true);
        JRadioButton jrbRecipeTable = new JRadioButton("Recipe Table");
        JRadioButton jrbLogTable = new JRadioButton("Log Table");
        JRadioButton jrbExerciseTable = new JRadioButton("Exercise Table");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(jrbFoodTable);
        buttonGroup.add(jrbRecipeTable);
        buttonGroup.add(jrbLogTable);
        buttonGroup.add(jrbExerciseTable);

        jrbFoodTable.addActionListener(e -> {
            setChanged();
            mapToSend.put("Food Table", new ArrayList<>());
            notifyObservers(mapToSend);
            mapToSend.clear();
        });

        jrbRecipeTable.addActionListener(e -> {
            setChanged();
            mapToSend.put("Recipe Table", new ArrayList<>());
            notifyObservers(mapToSend);
            mapToSend.clear();
        });

        jrbLogTable.addActionListener(e -> {
            setChanged();
            mapToSend.put("Log Table", new ArrayList<>());
            notifyObservers(mapToSend);
            mapToSend.clear();
        });

        jrbExerciseTable.addActionListener(e -> {
            setChanged();
            mapToSend.put("Exercise Table", new ArrayList<>());
            notifyObservers(mapToSend);
            mapToSend.clear();
        });

        radioPanel.add(jrbFoodTable);
        radioPanel.add(jrbRecipeTable);
        radioPanel.add(jrbLogTable);
        radioPanel.add(jrbExerciseTable);
        jpPanel.add(radioPanel);
    }

    /**
     * Build calendar to select date
     *
     * @param utilDateModel : UtilDateModel
     */
    public void buildCalendar(UtilDateModel utilDateModel) {
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(utilDateModel, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        datePicker.addActionListener(e -> {
            try {
                this.year = datePicker.getJFormattedTextField().getText().split("-")[0];
                this.month = datePicker.getJFormattedTextField().getText().split("-")[1];
                this.day = datePicker.getJFormattedTextField().getText().split("-")[2];
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        });
        JButton jButton = new JButton("Show Information by Date");
        jButton.addActionListener(e -> {
            ArrayList<String> date = new ArrayList<>();
            date.add(this.year);
            date.add(this.month);
            date.add(this.day);
            setChanged();
            mapToSend.put("Info For Date", date);
            notifyObservers(mapToSend);
            mapToSend.clear();
        });
        JPanel jPanelDatePicker = new JPanel();
        JPanel jPanelButton = new JPanel();
        jPanelDatePicker.add(new JLabel("Date Picker"));
        jPanelDatePicker.add(datePicker);
        jPanelButton.add(jButton);


        jpCalendar.add(jPanelDatePicker);
        jpCalendar.add(jPanelButton);
        jFrame.add(jpCalendar, BorderLayout.EAST);
    }

    /**
     * Create new JFrame and append JButtons. When a button is clicked, generate JDialog with some data.
     *
     * @param foodList    : List<List<String>>
     * @param columnNames : String[]
     * @param editable    : Boolean[]
     */
    public void createDialogForDate(Map<String, Double> map, List<List<String>> foodList, List<List<String>> exerciseList, Double calories, Double preferredCalories, String[] columnNames, boolean... editable) {
        JFrame jFrame = new JFrame("Selected date: " + this.year + "-" + this.month + "-" + this.day);
        jFrame.getContentPane().setLayout(new GridLayout(0, 1, 10, 10));
        JPanel jPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JPanel jpUserData = new JPanel(new GridLayout(0, 1, 10, 10));
        JButton jbFoodForDate = new JButton("View Food Table");
        JButton jbExerciseForDate = new JButton("View Exercise Table");
        JButton jbBarChart = new JButton("View BarChart");
        addToPanel(jPanel, jbFoodForDate, jbExerciseForDate, jbBarChart);
        jpUserData.add(new JLabel("Your desired calories: " + preferredCalories));
        jpUserData.add(new JLabel("Calories for the date: " + calories));

        if (calories > preferredCalories) {
            jpUserData.add(new JLabel("Net calories exceed desired calories by " + (calories - preferredCalories)));
        } else if (calories < preferredCalories) {
            jpUserData.add(new JLabel("Net calories is lower than the desired calories by " + (preferredCalories - calories)));
        } else {
            jpUserData.add(new JLabel("Net calories is equal to the desired calories."));
        }

        jFrame.add(jPanel);
        jFrame.add(jpUserData);
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);
        jFrame.pack();

        jbFoodForDate.addActionListener(e -> {
            try {
                jDialog = new JDialog(jFrame, "Foods for: " + this.year + "-" + this.month + "-" + this.day);
                String[] foodColumns = Arrays.copyOfRange(columnNames, 0, 6);
                buildTableForDate(foodList, foodColumns, editable);
                jDialog.add(jpUserData);
            } catch (IndexOutOfBoundsException ioobe) {
                createErrorMessage("No food for selected date.", "DatePicker Error.");
            }
        });

        jbBarChart.addActionListener(e -> {
            jDialog = new JDialog(jFrame, "Nutrients for: " + this.year + "-" + this.month + "-" + this.day);
            createBarChartForDate(jDialog, jDialog.getTitle(), map);
            jDialog.pack();
            jDialog.setVisible(true);
            jDialog.setLocationRelativeTo(null);
        });

        jbExerciseForDate.addActionListener(e -> {
            try {
                jDialog = new JDialog(jFrame, "Exercises for: " + this.year + "-" + this.month + "-" + this.day);
                String[] exerciseColumns = Arrays.copyOfRange(columnNames, 6, columnNames.length);
                buildTableForDate(exerciseList, exerciseColumns, editable);
            } catch (IndexOutOfBoundsException ioobe) {
                createErrorMessage("No exercise for selected date.", "DatePicker Error.");
            }
        });
    }

    private void buildTableForDate(List<List<String>> exerciseList, String[] columnNames, boolean[] editable) {
        JTable jTable = new JTable(buildTableModel(exerciseList, columnNames, editable));
        jTable.setPreferredScrollableViewportSize(jTable.getPreferredSize());
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jDialog.add(new JScrollPane(jTable));
        jDialog.pack();
        jDialog.setVisible(true);
        jDialog.setLocationRelativeTo(null);
    }


    /**
     * Append JTable to JScrollPane's viewport.
     */
    public void appendTable() {
        jsPane.getViewport().add(this.jTable);
        EventQueue.invokeLater(jsPane::updateUI);
        JPanel jPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        jPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel jpRadio = new JPanel();
        createRadioGroup(jpRadio);
        jPanel.add(jsPane);
        jPanel.add(jpRadio);
        EventQueue.invokeLater(jPanel::updateUI);
        jFrame.add(jPanel, BorderLayout.WEST);
    }


    /**
     * build JTable from a generic List.
     *
     * @param list        : List<T>
     * @param columnNames : String[]
     * @param <T>         : Generic type
     */
    public <T> void buildJTable(List<T> list, String[] columnNames, boolean... editable) {
        this.jTable = new JTable(buildTableModel(list, columnNames, editable));
        jTable.setPreferredScrollableViewportSize(jTable.getPreferredSize());
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable.addMouseListener(new MouseAdapter() {
            // TABLE ROW GET SELECTED ON RIGHT CLICK
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int currentRow = jTable.rowAtPoint(point);
                jTable.setRowSelectionInterval(currentRow, currentRow);
            }
        });

    }

    /**
     * Build and return TableModel. Add TableModelListener to it to check for changed cells.
     *
     * @param list        : List<T>
     * @param columnNames : String[]
     * @param <T>         : Generic type
     * @return TableModel
     */
    public <T> TableModel buildTableModel(List<T> list, String[] columnNames, boolean... editable) {
        String[][] data;

        if (list.size() <= 0) {
            return new DefaultTableModel(new String[][]{
                    { "No Data Found" }
            }, columnNames);
        }

        // Generate the table differently for recipes
        if (list.get(0) instanceof Recipe) {
            // Initialize data array
            int amtRows = 0;
            for (T r : list) {
                amtRows += 1 + ((r.toString().split(",").length - 1) / 2);
            }
            data = new String[amtRows][];

            // Organize the recipes into the proper format for the data array
            int currentDataRow = 0;
            for (T r : list) {
                // Get one recipe at a time
                String[] items = r.toString().split(",");

                // Add the recipe name as its own row
                data[currentDataRow] = new String[]{items[0], "", ""};
                currentDataRow++;

                // Add each ingredient as its own row
                for (int i = 1; i < items.length; i += 2) {
                    data[currentDataRow] = new String[]{"", items[i], items[i + 1]};
                    currentDataRow++;
                }
            }
        } else {
            data = new String[list.size()][];

            for (int i = 0; i < list.size(); i++) {
                data[i] = list.get(i).toString().split(",");
            }
        }

        TableModel model;
        model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                try {
                    if (this.getValueAt(rowIndex, columnIndex).equals("")) {
                        return false;
                    } else return editable[columnIndex];
                } catch (NullPointerException npe) {
                    return false;
                }
            }
        };

        model.addTableModelListener(e -> {
            int row = jTable.getSelectedRow();
            int colCount = jTable.getModel().getColumnCount();
            List<String> rowData = new ArrayList<>();
            AtomicReference<String> listType = new AtomicReference<>("");
            if (list.get(0) instanceof Recipe) {
                listType.set("recipe entry");
                int lowerBound = getLowerIndexRecipeTable(jTable, row);

                int upperBound;

                if (jTable.getValueAt(row, jTable.getSelectedColumn()).equals("")) {
                    upperBound = getUpperIndexRecipeTable(jTable, row);
                } else {
                    upperBound = getUpperBoundCellNotEmpty(jTable, row);
                }
                rowData.add(getRecipeNumber(jTable, lowerBound) + "");

                for (int i = lowerBound; i < upperBound; i++) {
                    for (int j = 0; j < colCount; j++) {
                        if (!jTable.getValueAt(i, j).toString().equals("")) {
                            rowData.add(jTable.getValueAt(i, j).toString());
                        }
                    }
                }
            } else {
                rowData.add(jTable.getSelectedRow() + "");
                for (int i = 0; i < colCount; i++) {
                    try {
                        rowData.add(jTable.getValueAt(row, i).toString().trim());

                    } catch (NullPointerException ignored) {
                    }
                }
                if (list.get(0) instanceof Food) {
                    listType.set("food entry");
                } else if (list.get(0) instanceof Log) {
                    listType.set("log entry");
                } else if (list.get(0) instanceof Exercise) {
                    listType.set("exercise entry");
                }
            }
            setChanged();
            mapToSend.put(listType.get(), rowData);
            notifyObservers(mapToSend);
            mapToSend.clear();
            rowData.clear();
        });
        return model;
    }

    /**
     * Get recipe index in table.
     *
     * @param table      : JTable
     * @param lowerBound : Integer
     * @return Integer
     */
    public int getRecipeNumber(JTable table, int lowerBound) {
        int recipeNumber = 0;
        for (int i = 0; i < lowerBound; i++) {
            if (!table.getValueAt(i, 0).toString().equals("")) {
                recipeNumber++;
            }
        }

        return recipeNumber;
    }

    /**
     * Get lower bound for row without an empty cell as first cell.
     *
     * @param table    : JTable
     * @param rowIndex : Integer
     * @return : Integer
     */
    public int getUpperBoundCellNotEmpty(JTable table, int rowIndex) {
        rowIndex++;
        while (table.getValueAt(rowIndex, 0).equals("")) {
            if (rowIndex >= table.getRowCount() - 1) {
                rowIndex++;
                break;
            } else rowIndex++;

        }

        return rowIndex;
    }

    /**
     * Get beginning of recipe in the table.
     *
     * @param table    : JTable
     * @param rowIndex : Integer
     * @return Integer
     */
    public int getUpperIndexRecipeTable(JTable table, int rowIndex) {
        while (table.getValueAt(rowIndex, 0).equals("")) {
            if (rowIndex >= table.getRowCount() - 1) {
                rowIndex++;
                break;
            } else rowIndex++;

        }

        return rowIndex;
    }

    /**
     * Get end of recipe in the table.
     *
     * @param table    : JTable
     * @param rowIndex : Integer
     * @return Integer
     */
    public int getLowerIndexRecipeTable(JTable table, int rowIndex) {
        while (table.getValueAt(rowIndex, 0).equals("")) {
            rowIndex--;
        }

        return rowIndex;
    }

    /**
     * Update TableModel in JTable.
     *
     * @param list        : List<T>
     * @param columnNames : String[]
     * @param <T>         : Generic type
     */
    public <T> void updateTable(List<T> list, String[] columnNames, boolean... editable) {
        this.jTable.setModel(buildTableModel(list, columnNames, editable));
    }


    /**
     * creates JMenu and adds it to the JFrame
     *
     * @param title      : String
     * @param jMenuBar   : jMenuBar
     * @param jMenuItems : List<JMenuItem>
     */
    public void createAndAddMenu(String title, JMenuBar jMenuBar, List<JMenuItem> jMenuItems) {
        JMenu jMenu = new JMenu(title);
        jMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        for (JMenuItem item : jMenuItems) {
            jMenu.add(item);
            jMenu.addSeparator();
        }
        jMenuBar.add(jMenu);
    }

    /**
     * Create JPopupMenu
     *
     * @param names : String[ ]
     */
    public void createPopupMenu(String... names) {
        JPopupMenu popupMenu = new JPopupMenu();
        List<JMenuItem> list = createItemsForMenu(names);
        for (JMenuItem jMenuItem : list) {
            popupMenu.add(jMenuItem);
        }

        jTable.setComponentPopupMenu(popupMenu);
    }

    /**
     * create JMenuItems from an array of string
     *
     * @param titles : String...
     * @return List<JMenuItem>
     */
    public List<JMenuItem> createItemsForMenu(String... titles) {
        List<JMenuItem> jMenuItemList = new ArrayList<>();
        for (String title : titles) {
            JMenuItem jMenuItem = new JMenuItem(title);
            jMenuItem.addActionListener(this);
            jMenuItem.setActionCommand(title);
            jMenuItemList.add(jMenuItem);
        }
        return jMenuItemList;
    }

    public void disposeJDialog() {
        jDialog.dispose();
    }

    /**
     * ActionListener handler for JMenuItems.
     *
     * @param e : ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {

            case "Add Food": {
                // disable the JMenuItem
                JMenuItem jMenuItem = (JMenuItem) e.getSource();
                jMenuItem.setEnabled(false);
                JOptionPane.showMessageDialog(jFrame, "Leave year, month, and day empty for today's date.", "Today's date", JOptionPane.INFORMATION_MESSAGE);

                // create JDialog
                jDialog = new JDialog(jFrame, "Add new Food");
                jDialog.setLocationRelativeTo(null);
                JPanel jPanel = new JPanel(new GridLayout(0, 2, 10, 10));
                jPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 10));

                // create JLabel and JTextField
                Map<JLabel, JTextField> map = createJlAndJtfMap(
                        new String[]{"Year: ", "Month: ", "Day: ", "Food Name: ", "Calories: ", "Fat: ", "Carbs: ", "Proteins: ", "Servings: "},
                        new Boolean[]{true, true, true, false, true, true, true, true, true}
                );

                ArrayList<JTextField> jTextFields = new ArrayList<>();
                map.forEach((k, v) -> {
                    addToPanel(jPanel, k, v);
                    if (Boolean.parseBoolean(v.getName())) {
                        jTextFields.add(v);
                    }
                });
                // create JButton
                JButton jbAddFood = createJButton("Add food");
                // if JButton is clicked, notify Controller and send the Food and Log to it
                jbAddFood.addActionListener(eButton -> {
                    List<String> list = iterateThroughPanel(jPanel);
                    setChanged();
                    if (list.subList(3, list.size()).stream().noneMatch(x -> x.equalsIgnoreCase(""))) {
                        if (list.subList(0, 3).stream().allMatch(x -> x.equalsIgnoreCase(""))) {
                            mapToSend.put("add food default", list);
                        } else {
                            if (list.subList(0, 3).stream().anyMatch(x -> x.equalsIgnoreCase(""))) {
                                JOptionPane.showMessageDialog(jDialog, "Date must be either empty or filled.", "Form Error.", JOptionPane.ERROR_MESSAGE);
                            } else {
                                mapToSend.put("add food", list);
                            }
                        }
                        notifyObservers(mapToSend);
                        mapToSend.clear();
                        disposeJDialog();
                    } else {
                        JOptionPane.showMessageDialog(jDialog, "Form has empty fields.", "Form Error.", JOptionPane.ERROR_MESSAGE);
                    }
                });

                appendToDialog(jPanel, jbAddFood, jMenuItem);

                break;
            }

            case "Remove Food": {
                if (jTable.getSelectedRow() != -1) {
                    List<String> rowData = getRowDataFromTable();
                    mapToSend.put("remove food", rowData);
                    setChanged();
                    notifyObservers(mapToSend);
                    mapToSend.clear();
                    rowData.clear();
                } else {
                    JOptionPane.showMessageDialog(jDialog, "Please select the row with the food to delete.", "PopUp Error.", JOptionPane.ERROR_MESSAGE);
                }
                break;
            }


            //ADD FOOD TO RECIPES
            case "Add Food to Recipe": {
                if (jTable.getSelectedRow() != -1) {

                    int selectedRow = jTable.getSelectedRow();
                    int colCount = jTable.getColumnCount();
                    List<String> rowData = new ArrayList<>();

                    jDialog = new JDialog(jFrame, "Add new Food");
                    JPanel jPanel = new JPanel(new GridLayout(0, 1, 10, 10));
                    jPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                    JList<String> jList = new JList<>(this.recipeNames);
                    jList.setCellRenderer(getRenderer());
                    jPanel.add(new JLabel("List of Recipes"));
                    jPanel.add(jList);
                    JLabel label = new JLabel("Servings: ");
                    JTextField jTextField = createJTextField(true);
                    JPanel jpServings = new JPanel(new GridLayout(1, 0));
                    addToPanel(jpServings, label, jTextField);

                    JButton jButton = new JButton("Select Recipe");
                    jButton.addActionListener(eSelectRecipe -> {
                        if (!jTextField.getText().equalsIgnoreCase("")) {
                            rowData.add(jList.getSelectedValue());
                            rowData.add(jTextField.getText());
                            for (int i = 0; i < colCount; i++) {
                                rowData.add(jTable.getValueAt(selectedRow, i).toString().trim());
                            }
                            setChanged();
                            mapToSend.put("add food to recipe", rowData);
                            notifyObservers(mapToSend);
                            mapToSend.clear();
                            rowData.clear();
                        } else {
                            JOptionPane.showMessageDialog(jDialog, "Servings cannot be empty.", "Form Error.", JOptionPane.ERROR_MESSAGE);
                        }

                    });

                    jPanel.add(jpServings);
                    jPanel.add(jButton);
                    jDialog.add(jPanel);
                    jDialog.pack();
                    jDialog.setLocationRelativeTo(null);
                    jDialog.setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(jDialog, "Please select the row with the food to delete.", "PopUp Error.", JOptionPane.ERROR_MESSAGE);
                }

                break;
            }

            case "Remove Food From Recipe": {
                int selectedRow = jTable.getSelectedRow();
                if (selectedRow != -1) {
                    List<String> selected = new ArrayList<>();
                    selected.add(selectedRow + "");
                    mapToSend.put("Remove Food From Recipe", selected);
                    setChanged();
                    notifyObservers(mapToSend);
                    mapToSend.clear();
                }

                break;
            }

            case "Add Recipe": {
                mapToSend.put("Add Recipe", new ArrayList<>());
                setChanged();
                notifyObservers(mapToSend);
                mapToSend.clear();

                break;
            }

            case "Remove Recipe": {
                mapToSend.put("Remove Recipe", new ArrayList<>());
                setChanged();
                notifyObservers(mapToSend);
                mapToSend.clear();

                break;
            }

            //ADD LOG
            case "Add Log": {
                JOptionPane.showMessageDialog(jFrame, "Leave year, month, and day empty for today's date.", "Today's date", JOptionPane.INFORMATION_MESSAGE);

                // create JDialog
                jDialog = new JDialog(jFrame, "Add new Log");
                JPanel jpContainer = new JPanel();
                JPanel jpRadio = new JPanel(new GridLayout(0, 2, 10, 10));
                JPanel jpForm = new JPanel(new GridLayout(0, 2, 10, 10));
                jpContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 10));

                ButtonGroup buttonGroup = new ButtonGroup();
                JRadioButton jrbCaloriesAndWeight = new JRadioButton("Weight/Calories");
                JRadioButton jrbFoodOrExercise = new JRadioButton("Food/Recipe/Exercise");
                buttonGroup.add(jrbCaloriesAndWeight);
                buttonGroup.add(jrbFoodOrExercise);

                jrbCaloriesAndWeight.addActionListener(el -> {
                    createAddLogFields(jpForm);
                    jpForm.add(new JLabel("Type (c/w): "));
                    jpForm.add(createJTextField(false));
                    jpForm.add(new JLabel("Value: "));
                    jpForm.add(createJTextField(true));
                    jpForm.updateUI();
                    jDialog.pack();
                });

                jrbFoodOrExercise.addActionListener(el -> {
                    createAddLogFields(jpForm);
                    jpForm.add(new JLabel("Type (f/e): "));
                    jpForm.add(createJTextField(false));
                    jpForm.add(new JLabel("Name: "));
                    jpForm.add(createJTextField(false));
                    jpForm.add(new JLabel("Value: "));
                    jpForm.add(createJTextField(true));
                    jpForm.updateUI();
                    jDialog.pack();
                });

                JButton jButton = new JButton("Add Log");
                jButton.addActionListener(el -> {
                    List<String> listFromForm = iterateThroughPanel(jpForm);
                    setChanged();
                    if (listFromForm.subList(3, listFromForm.size()).stream().noneMatch(x -> x.equalsIgnoreCase(""))) {
                        if (listFromForm.subList(0, 3).stream().allMatch(x -> x.equalsIgnoreCase(""))) {
                            mapToSend.put("add log default", listFromForm.subList(3, listFromForm.size()));
                        } else {
                            if (listFromForm.subList(0, 3).stream().anyMatch(x -> x.equalsIgnoreCase(""))) {
                                JOptionPane.showMessageDialog(jDialog, "Date must be either empty or filled.", "Form Error.", JOptionPane.ERROR_MESSAGE);
                            } else {
                                mapToSend.put("add log", listFromForm);

                            }
                        }
                        notifyObservers(mapToSend);
                        mapToSend.clear();
                        //disposeJDialog();

                    } else {
                        JOptionPane.showMessageDialog(jDialog, "Form has empty fields.", "Form Error.", JOptionPane.ERROR_MESSAGE);
                    }
                });

                addToPanel(jpRadio, jrbCaloriesAndWeight, jrbFoodOrExercise, jButton);
                addToPanel(jpContainer, jpForm, jpRadio);
                jDialog.add(jpContainer);
                jDialog.pack();
                jDialog.setLocationRelativeTo(null);
                jDialog.setVisible(true);
                break;
            }

            //REMOVE LOG
            case "Remove Log": {
                if (jTable.getSelectedRow() != -1) {
                    List<String> rowData = getRowDataFromTable();
                    mapToSend.put("remove log", rowData);
                    setChanged();
                    notifyObservers(mapToSend);
                    mapToSend.clear();
                    rowData.clear();
                } else {
                    JOptionPane.showMessageDialog(jDialog, "Please select the row with the food to delete.", "PopUp Error.", JOptionPane.ERROR_MESSAGE);
                }
                break;
            }

            //ADD EXERCISE
            case "Add Exercise" : {
                jDialog = new JDialog(jFrame, "Add New Exercise");
                JPanel jpanel = new JPanel(new GridLayout(0,2,10,10));
                jpanel.setBorder(new EmptyBorder(10,10,10,10));
                jpanel.add(new JLabel("Exercise Name: "));
                jpanel.add(createJTextField(false));
                jpanel.add(new JLabel("Calories Expended: "));
                jpanel.add(createJTextField(true));

                JButton jbutton = new JButton("Add Exercise");
                jbutton.addActionListener(e1 -> {
                    List<String> list = iterateThroughPanel(jpanel);
                    setChanged();
                    if (list.stream().noneMatch(x->x.equals(""))) {
                        mapToSend.put("add exercise", list);
                        notifyObservers(mapToSend);
                        mapToSend.clear();
                    } else {
                        createErrorMessage("Form field(s) is empty.","Form Error.");
                    }
                });
                jpanel.add(jbutton);
                jDialog.add(jpanel);
                jDialog.pack();
                jDialog.setLocationRelativeTo(null);
                jDialog.setVisible(true);
                break;
            }

            // REMOVE EXERCISE
            case "Remove Exercise" : {
                mapToSend.put("Remove Exercise", new ArrayList<>());
                setChanged();
                notifyObservers(mapToSend);
                mapToSend.clear();

                break;
            }

            //CHANGE PREFERRED WEIGHT
            case "Change Weight": {
                createJDialogForUserData("Preferred Weight: ", "change desired weight");
                break;
            }

            //CHANGE PREFERRED CALORIES
            case "Change Calories": {
                createJDialogForUserData("Preferred Calories: ", "change desired calories");
                break;
            }
        }
    }

    public void createJDialogForUserData(String name, String sendToController) {
        jDialog = new JDialog();
        JPanel jPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        jPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        Map<JLabel, JTextField> map = createJlAndJtfMap(
                new String[]{"Year: ", "Month: ", "Day: ", name},
                new Boolean[]{true, true, true, true}
        );

        ArrayList<JTextField> jTextFields = new ArrayList<>();
        map.forEach((k, v) -> {
            addToPanel(jPanel, k, v);
            if (Boolean.parseBoolean(v.getName())) {
                jTextFields.add(v);
            }
        });

        JButton jButton = new JButton("Set Calories");
        jButton.addActionListener(el -> {
            List<String> list = iterateThroughPanel(jPanel);
            mapToSend.put(sendToController, list);
            setChanged();
            notifyObservers(mapToSend);
            mapToSend.clear();
        });

        jPanel.add(jButton);
        jDialog.add(jPanel);
        jDialog.pack();
        jDialog.setLocationRelativeTo(null);
        jDialog.setVisible(true);
    }

    /**
     * Get data for each row in JTable.
     *
     * @return List<String>
     */
    private List<String> getRowDataFromTable() {
        int selectedRow = jTable.getSelectedRow();
        int colCount = jTable.getColumnCount();
        List<String> rowData = new ArrayList<>();
        for (int i = 0; i < colCount; i++) {
            try {
                rowData.add(jTable.getValueAt(selectedRow, i).toString().trim());

            } catch (NullPointerException ignored) {
            }
        }
        return rowData;
    }

    /**
     * Create JLabels and JTextFields for adding a log and append them to a JPanel.
     *
     * @param jpForm : JPanel
     */
    private void createAddLogFields(JPanel jpForm) {
        jpForm.removeAll();
        jpForm.add(new JLabel("Year: "));
        jpForm.add(createJTextField(true));
        jpForm.add(new JLabel("Month: "));
        jpForm.add(createJTextField(true));
        jpForm.add(new JLabel("Day: "));
        jpForm.add(createJTextField(true));
    }

    /**
     * Create black separators between JList cells.
     *
     * @return ListCellRenderer<? super String>
     */
    private ListCellRenderer<? super String> getRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent
                        (list, value, index, isSelected, cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder
                        (0, 0, 1, 0, Color.BLACK));
                return listCellRendererComponent;
            }
        };
    }

    /**
     * Creates a dialog for adding a recipe.
     *
     * @param foodsAndRecipes : List<FoodComponent>
     */
    public void createAddRecipeDialog(List<FoodComponent> foodsAndRecipes) {
        // Create an add recipe dialog
        jDialog = new JDialog(jFrame, "Add new Recipe");
        JPanel jPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        jPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 10));

        // Recipe name input
        jPanel.add(new JLabel("Recipe Name:"));

        JTextField nameField = createJTextField(false);
        jPanel.add(nameField);

        jPanel.add(new JLabel(""));

        // Display column labels
        jPanel.add(new JLabel("Used in recipe?"));
        jPanel.add(new JLabel("Ingredient"));
        jPanel.add(new JLabel("Servings"));

        List<JCheckBox> checks = new ArrayList<>();
        List<JTextField> servings = new ArrayList<>();
        // Display foods and recipes
        foodsAndRecipes.forEach(k -> {
            JCheckBox newCheck = new JCheckBox();
            checks.add(newCheck);
            jPanel.add(newCheck);

            jPanel.add(new JLabel(k.getName()));

            JTextField newServing = createJTextField(true);
            servings.add(newServing);
            jPanel.add(newServing);
        });

        // Create button
        jPanel.add(new JLabel(""));
        JButton jbAddFood = createJButton("Add Recipe");
        jbAddFood.addActionListener(eButton -> {
            List<String> newRecipeData = new ArrayList<>();
            newRecipeData.add("r");

            // Make sure a recipe name was entered
            if (nameField.getText().length() > 0) {
                newRecipeData.add(nameField.getText());
            } else {
                JOptionPane.showMessageDialog(jDialog, "Recipe name cannot be empty.", "Form Error.", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check for selected ingredients
            for (int i = 0; i < checks.size(); i++) {
                if (checks.get(i).isSelected()) {
                    // Make sure user entered servings
                    if (servings.get(i).getText().length() > 0) {
                        newRecipeData.add(foodsAndRecipes.get(i).getName());
                        double s = Double.parseDouble(servings.get(i).getText());
                        newRecipeData.add(Double.toString(s));
                    } else {
                        JOptionPane.showMessageDialog(jDialog, "Servings cannot be empty.", "Form Error.", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            if (newRecipeData.size() <= 2) {
                JOptionPane.showMessageDialog(jDialog, "Please select some ingredients.", "Form Error.", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Send entered data to the controller to add to the CSV
            mapToSend.put("Add Recipe-READY", newRecipeData);
            setChanged();
            notifyObservers(mapToSend);
            mapToSend.clear();

            // Success
            jDialog.dispose();
            JOptionPane.showMessageDialog(jDialog, "Recipe Added!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Update the GUI recipe table
            setChanged();
            mapToSend.put("Recipe Table", new ArrayList<>());
            notifyObservers(mapToSend);
            mapToSend.clear();
        });
        jPanel.add(jbAddFood);

        // Show the dialog
        jDialog.add(jPanel);
        jDialog.setVisible(true);
        jDialog.setLocationRelativeTo(null);
        jDialog.pack();
    }

    /**
     * Creates a dialog for removing a recipe.
     *
     * @param recipes : List<FoodComponent>
     */
    public void createRemoveRecipeDialog(List<FoodComponent> recipes) {
        // Create an add recipe dialog
        jDialog = new JDialog(jFrame, "Remove a Recipe");
        JPanel jPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        jPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 10));

        // Display column labels
        jPanel.add(new JLabel("Remove?"));
        jPanel.add(new JLabel("Recipe Name"));

        List<JCheckBox> checks = new ArrayList<>();
        // Display recipes
        recipes.forEach(k -> {
            JCheckBox newCheck = new JCheckBox();
            checks.add(newCheck);
            jPanel.add(newCheck);

            jPanel.add(new JLabel(k.getName()));
        });

        // Create button
        jPanel.add(new JLabel(""));
        JButton jbAddFood = createJButton("Remove Recipe");
        jbAddFood.addActionListener(eButton -> {
            List<String> recipesToRemove = new ArrayList<>();
            // Check for selected recipes
            for (int i = 0; i < checks.size(); i++) {
                if (checks.get(i).isSelected()) {
                    recipesToRemove.add(recipes.get(i).getName());
                }
            }

            // Send selected recipes to the controller to remove from the CSV
            mapToSend.put("Remove Recipe-READY", recipesToRemove);
            setChanged();
            notifyObservers(mapToSend);
            mapToSend.clear();

            // Success
            jDialog.dispose();
            JOptionPane.showMessageDialog(jDialog, "Recipe(s) Removed!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Update the GUI recipe table
            setChanged();
            mapToSend.put("Recipe Table", new ArrayList<>());
            notifyObservers(mapToSend);
            mapToSend.clear();
        });
        jPanel.add(jbAddFood);

        // Show the dialog
        jDialog.add(jPanel);
        jDialog.setVisible(true);
        jDialog.setLocationRelativeTo(null);
        jDialog.pack();
    }

    /**
     * Creates a dialog for removing an Exercise.
     *
     * @param exercises : List<Exercise>
     */
    public void createRemoveExerciseDialog(List<Exercise> exercises) {
        // Create an add exercise dialog
        jDialog = new JDialog(jFrame, "Remove an Exercise");
        JPanel jPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        jPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Display column labels
        jPanel.add(new JLabel("Remove?"));
        jPanel.add(new JLabel("Exercise Name"));

        List<JCheckBox> checks = new ArrayList<>();
        // Display exercises
        exercises.forEach(k -> {
            JCheckBox newCheck = new JCheckBox();
            checks.add(newCheck);
            jPanel.add(newCheck);

            jPanel.add(new JLabel(k.getName()));
        });

        // Create button
        jPanel.add(new JLabel(""));
        JButton jbRemoveExercise = createJButton("Remove Exercise");
        jbRemoveExercise.addActionListener(eButton -> {
            List<String> exercisesToRemove = new ArrayList<>();
            // Check for selected recipes
            for (int i = 0; i < checks.size(); i++) {
                if (checks.get(i).isSelected()) {
                    exercisesToRemove.add(exercises.get(i).getName());
                }
            }

            // Send selected exercises to the controller to remove from the CSV
            mapToSend.put("Remove Exercise-READY", exercisesToRemove);
            setChanged();
            notifyObservers(mapToSend);
            mapToSend.clear();

            // Success
            jDialog.dispose();
            JOptionPane.showMessageDialog(jDialog, "Exercise(s) Removed!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Update the GUI recipe table
            setChanged();
            mapToSend.put("Exercise Table", new ArrayList<>());
            notifyObservers(mapToSend);
            mapToSend.clear();
        });
        jPanel.add(jbRemoveExercise);

        // Show the dialog
        jDialog.add(jPanel);
        jDialog.setVisible(true);
        jDialog.setLocationRelativeTo(null);
        jDialog.pack();
    }

    /**
     * Generate a new MessageDialog.
     *
     * @param message : String
     * @param title   : String
     */
    public void createErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(jDialog, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Append JPanel containing JButton and JMenuItem to a JDialog.
     *
     * @param jPanel    : JPanel
     * @param button    : JButton
     * @param jMenuItem : JMenuItem
     */
    public void appendToDialog(JPanel jPanel, JButton button, JMenuItem jMenuItem) {
        addToPanel(jPanel, button);

        jDialog.add(jPanel);
        jDialog.setVisible(true);
        jDialog.pack();

        // if JDialog is closed, enable JMenuItem
        jDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                jMenuItem.setEnabled(true);
            }
        });
    }

    /**
     * Create JTextField. if boolean is true, restring input to numbers and period (".").
     *
     * @param isDouble : boolean
     * @return JTextField
     */
    public JTextField createJTextField(boolean isDouble) {
        JTextField jTextField = new JTextField(10);
        jTextField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        if (isDouble) {
            jTextField.setName(true + "");
            jTextField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char key = e.getKeyChar();
                    if (!((key >= '0') && (key <= '9')
                            || (key == KeyEvent.VK_BACK_SPACE)
                            || (key == KeyEvent.VK_DELETE)
                            || (key == KeyEvent.VK_PERIOD))) {
                        e.consume();
                    }
                }
            });
        } else {
            jTextField.setName(false + "");
        }

        return jTextField;
    }

    /**
     * Dynamically generate JLabels and JTextField
     *
     * @param names    : String[]
     * @param isDouble : Boolean[]
     * @return Map<JLabel, JTextField>
     */
    public Map<JLabel, JTextField> createJlAndJtfMap(String[] names, Boolean[] isDouble) {

        return new LinkedHashMap<>() {{
            if (names.length == isDouble.length) {
                for (int i = 0; i < names.length; i++) {
                    put(createJLabel(names[i]), createJTextField(isDouble[i]));
                }
            }
        }};
    }

    /**
     * Create JLabel.
     *
     * @param name : String
     * @return JLabel
     */
    public JLabel createJLabel(String name) {
        return new JLabel(name);
    }

    /**
     * Create JButton.
     *
     * @param name   : String
     * @param bounds : int[]
     * @return JButton
     */
    public JButton createJButton(String name, int... bounds) {
        JButton jButton = new JButton(name);
        if (bounds.length >= 4) {
            jButton.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
        }
        return jButton;
    }

    /**
     * Get all JTextFields from a JPanel and add values to a List<String>.
     *
     * @param container : Container
     * @return List<String>
     */
    public List<String> iterateThroughPanel(Container container) {
        List<String> list = new ArrayList<>();
        for (Component component : container.getComponents()) {
            if (component instanceof JTextField) {
                list.add(((JTextField) component).getText());
            }
        }
        return list;
    }

    /**
     * Add swing components to JPanel.
     *
     * @param panel      : JPanel
     * @param components : Component[]
     */
    public void addToPanel(JPanel panel, Component... components) {
        for (Component component : components) {
            panel.add(component);
        }
    }

    /**
     * Class that formats date for calendar
     */
    static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

        private final String datePattern = "yyyy-MM-dd";
        private final SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormat.parseObject(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormat.format((cal.getTime()));
            }

            return "";
        }
    }


}
