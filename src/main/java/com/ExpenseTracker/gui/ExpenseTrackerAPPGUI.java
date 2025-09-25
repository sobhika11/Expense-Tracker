package com.ExpenseTracker.gui;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import com.ExpenseTracker.dao.ExpenseDAO;
import com.ExpenseTracker.model.Expense;

import javax.swing.*;

import java.awt.*; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

 class ExpenseGUI extends JFrame{
    private JTable table;
    private DefaultTableModel tablemodel;
    private JTextArea description;
    private JTextField amount;
    private JButton addExpense;
    private JButton updateExpense;
    private JButton deleteExpense;
    private JButton refresh;
    private JTextField category;
    private JTextField dateField; 

    public ExpenseGUI(){
        initializeComponents();
        setUpLayout();
        setupEventListeners(); 
    }

    public void initializeComponents(){
        setSize(800,600);
        setTitle("Expense page");
        setLocationRelativeTo(null);

    }
    private void setupEventListeners(){
        addExpense.addActionListener((e)->{add();});
        updateExpense.addActionListener((e)->{update();});
        // deleteExpense.addActionListener((e)->{delete()});
        refresh.addActionListener((e)->{refresh();});
    }
    private void setUpLayout() {

    addExpense = new JButton("Add");
    updateExpense = new JButton("Update");
    deleteExpense = new JButton("Delete");
    refresh = new JButton("Refresh");

    setLayout(new BorderLayout());

    JPanel inputPanel = new JPanel(new GridBagLayout());
    GridBagConstraints g = new GridBagConstraints();
    g.insets = new Insets(10, 10, 10, 10);

    g.gridx = 0;
    g.gridy = 0;
    inputPanel.add(new JLabel("Description:"), g);

    g.gridx = 1;
    description = new JTextArea(2, 15);
    description.setLineWrap(true);
    description.setWrapStyleWord(true);
    JScrollPane scroll = new JScrollPane(description);
    scroll.setPreferredSize(new Dimension(150, 50));
    inputPanel.add(scroll, g);

    g.gridx = 0;
    g.gridy = 1;
    inputPanel.add(new JLabel("Amount:"), g);

    g.gridx = 1;
    amount = new JTextField(10);
    amount.setPreferredSize(new Dimension(100, 25));
    inputPanel.add(amount, g);

    g.gridx = 0;
    g.gridy = 2;
    inputPanel.add(new JLabel("Date (yyyy-MM-dd):"), g);

    g.gridx = 1;
    dateField = new JTextField(10);
    dateField.setPreferredSize(new Dimension(100, 25));
    inputPanel.add(dateField, g);
    
    g.gridx=0;
    g.gridy=3;
    inputPanel.add(new JLabel("Category"),g);

    g.gridx=1;
    g.gridy=3;
    category=new JTextField(7);
    category.setPreferredSize(new Dimension(100,25));
    inputPanel.add(category,g);
    // String [] cat={"Food","Rent","outing"};
    // JComboBox<String>comboBox=new JComboBox<>();


    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(addExpense);
    buttonPanel.add(updateExpense);
    buttonPanel.add(deleteExpense);
    buttonPanel.add(refresh);

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(inputPanel, BorderLayout.CENTER);
    topPanel.add(buttonPanel, BorderLayout.SOUTH);

    String[] columnNames = {"ID", "Description", "Amount", "Date","Category"};
    tablemodel = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    table = new JTable(tablemodel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            loadSelectedexpense();
        }
    });

    add(topPanel, BorderLayout.NORTH);                  
    add(new JScrollPane(table), BorderLayout.CENTER);  
    loadexpense();
}

    private void add(){
        String des = description.getText().trim();
        String dateStr = dateField.getText().trim();
        String cat=category.getText().trim();
        try{
            double amt = Double.parseDouble(amount.getText().trim());
            Expense exp = new Expense(des,dateStr,amt,cat);
            int newid=ExpenseDAO.add(exp);
            exp.setId(newid);
            JOptionPane.showMessageDialog(this,  "Expense added Succesfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadexpense();

        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error adding expense", "Failure",JOptionPane.ERROR_MESSAGE);

        }
    }

    // private void delete(){
    //     int row = table.getSelectedRow();
    //     if (row == -1) {
    //         JOptionPane.showMessageDialog(this, "Please select a row to delete",
    //                 "Validation Error", JOptionPane.WARNING_MESSAGE);
    //         return;
    //     }
        
    //     int confirm = JOptionPane.showConfirmDialog(this,
    //             "Are you sure you want to delete this expense?",
    //             "Confirm Delete", JOptionPane.YES_NO_OPTION);

    //     if (confirm == JOptionPane.YES_OPTION) {
    //         int id = (int) table.getValueAt(row, 0);
    //         try {
    //             boolean deleted = ExpenseDAO.delete(id);
    //         if (deleted) {
    //                 JOptionPane.showMessageDialog(this,
    //                         "expense deleted successfully",
    //                         "Success", JOptionPane.INFORMATION_MESSAGE);
    //                 // loadTodos(); // refresh table
    //             } else {
    //                 JOptionPane.showMessageDialog(this,
    //                         "Failed to delete todo",
    //                         "Error", JOptionPane.ERROR_MESSAGE);
    //             }
    //         } catch (SQLException e) {
    //             JOptionPane.showMessageDialog(this,
    //                     "Error deleting expense: " + e.getMessage(),
    //                     "Database Error", JOptionPane.ERROR_MESSAGE);
    //             e.printStackTrace();
    //         }
    //     }
        
    // }
    private void update(){
          int row = table.getSelectedRow();
    if (row==-1) {
        JOptionPane.showMessageDialog(this, "Please select a expense to update", "Validation Selection", JOptionPane.WARNING_MESSAGE);
        return;
        }
    String des = description.getText().trim();
    int id = (int) table.getValueAt(row, 0);
    try {
        Expense exp=ExpenseDAO.getById(id);
        if (exp != null) {
            exp.setAmount(Double.parseDouble(amount.getText().trim()));
            exp.setDescription(description.getText().trim());
              if (ExpenseDAO.updateTodo(exp)) {
                JOptionPane.showMessageDialog(this, "expense updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadexpense();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update expense", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } 
        catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error updating expense: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
            }
    }
    private void refresh(){

    }
    
    private void loadexpense(){
        try{
            List<Expense> exp = ExpenseDAO.getAll();
            updateTable(exp);
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error loading todos : "+e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
        }

    }
    private void loadSelectedexpense(){
        int row = table.getSelectedRow();
        if(row != -1){
        description.setText((String) table.getValueAt(row, 1));
        amount.setText(String.valueOf(table.getValueAt(row, 2))); // convert Integer to String
        dateField.setText(String.valueOf(table.getValueAt(row, 3)));
        category.setText((String)table.getValueAt(row, 4));
        
        }
    }

    private void updateTable(List<Expense> exp){
        tablemodel.setRowCount(0);
        for(Expense e : exp){
            Object[] row = {e.getId(),e.getDescription(),e.getAmount(),e.getDate(),e.getCategory()};
            tablemodel.addRow(row);
        }
    }
}

 class CategoryGUI extends JFrame{
    private JButton addcategory;
    private JButton upadatecategory;
    private JButton deletecategory;
    private JButton refreshcategory;

    private DefaultTableModel tablemodel;
    private JTable table;
    private JComboBox category;
    private JComboBox<String> filterComboBox;

    public CategoryGUI(){
        initializeComponents();
        
    }
    private void initializeComponents(){
        setTitle("Expense-Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        String [] filterbox={"Rent","Food"};
        tablemodel =new DefaultTableModel(filterbox,0)
        {
            @Override
            public boolean isCellEditable(int row,int column)
            {
                return false;
            }
        };

    }
}


public class ExpenseTrackerAPPGUI extends JFrame {
    private JButton expense;
    private JButton category;

    public ExpenseTrackerAPPGUI()
    {
        initializeComponents();
        setUpLayout();
        actionListeners();
    }
    private void initializeComponents(){
        setTitle("Home page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        expense=new JButton("Expense");
        category =new JButton("Category");
    }
    private void setUpLayout(){
        
        setLayout(new BorderLayout());
        JPanel btpanel=new JPanel(new GridBagLayout());
        GridBagConstraints g=new GridBagConstraints();
        g.insets=new Insets(10, 10, 10, 10);
        btpanel.add(expense,g);
        btpanel.add(category,g);
        expense.setPreferredSize(new Dimension(200,100));
        category.setPreferredSize(new Dimension(200,100));
        add(btpanel,BorderLayout.CENTER);

    }
    private void actionListeners(){
        expense.addActionListener((e)->{
            ExpenseGUI expengui=new ExpenseGUI();
            expengui.setVisible(true);
        });
        category.addActionListener((e)->{
            CategoryGUI catgui=new CategoryGUI();
            catgui.setVisible(true);
        });
    }
}
