package com.ExpenseTracker.gui;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import com.ExpenseTracker.dao.ExpenseDAO;
import com.ExpenseTracker.model.Expense;
import com.ExpenseTracker.model.category;
import com.ExpenseTracker.dao.categoryDAO;
import com.ExpenseTracker.*;

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
    private JComboBox<String> comboBox;

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
        deleteExpense.addActionListener((e)->{delete();});
        refresh.addActionListener((e)->{refresh();});
        comboBox.addActionListener((e)->{filterCat();});
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
    g.anchor=GridBagConstraints.WEST;
    inputPanel.add(new JLabel("Description:"), g);

    g.gridx = 1;
    description = new JTextArea(2, 15);
    description.setLineWrap(true);
    description.setWrapStyleWord(true);
    JScrollPane scroll = new JScrollPane(description);
    g.fill=GridBagConstraints.HORIZONTAL;
    inputPanel.add(scroll, g);

    g.gridx = 0;
    g.gridy = 1;
    g.anchor=GridBagConstraints.WEST;
    inputPanel.add(new JLabel("Amount:"), g);

    g.gridx = 1;
    g.anchor=GridBagConstraints.WEST;
    amount = new JTextField(10);
    g.fill=GridBagConstraints.HORIZONTAL;
    inputPanel.add(amount, g);


    g.gridx = 0;
    g.gridy = 2;
    g.anchor=GridBagConstraints.WEST;
    inputPanel.add(new JLabel("Date (yyyy-MM-dd):"), g);

    g.gridx = 1;
    g.anchor=GridBagConstraints.WEST;
    dateField = new JTextField(10);
    g.fill=GridBagConstraints.HORIZONTAL;
    inputPanel.add(dateField, g);
    
    g.gridx=0;
    g.gridy=3;
    g.anchor=GridBagConstraints.WEST;
    inputPanel.add(new JLabel("Category"),g);

    g.gridx=1;
    g.gridy=3;
    g.anchor=GridBagConstraints.WEST;
    category=new JTextField(7);
    g.anchor=GridBagConstraints.WEST;
    g.fill=GridBagConstraints.HORIZONTAL;
    inputPanel.add(category,g);

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

    add(new JScrollPane(table), BorderLayout.CENTER); 
    String []cat=categoryDAO.getAllCat();
    comboBox=new JComboBox<>(cat);
    JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    comboPanel.add(comboBox);
    topPanel.add(comboPanel, BorderLayout.WEST);
    add(topPanel, BorderLayout.NORTH); 
    loadexpense();
    }
    private void clearSection(){
        description.setText("");
        amount.setText("");
        dateField.setText("");
        category.setText("");
    }
     private void filterCat(){
        String cat=(String)comboBox.getSelectedItem();
        try{

            List<Expense>li=ExpenseDAO.filter(cat);
            updateTable(li);
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(this,"Error in filtering ", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
        
        
    }
    private void add(){
        String des = description.getText().trim();
        if(des.equals("")){
            JOptionPane.showMessageDialog(this, "Description cannot be empty","error",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String dateStr = dateField.getText().trim();
        String cat=category.getText().trim();
        if(cat.equals("")){
            JOptionPane.showMessageDialog(this, "Category cannot be empty","error",JOptionPane.INFORMATION_MESSAGE);
             return;
        }
        try{
            double amt = Double.parseDouble(amount.getText().trim());
            Expense exp = new Expense(des,dateStr,amt,cat);
            int newid=ExpenseDAO.add(exp);
            exp.setId(newid);
            JOptionPane.showMessageDialog(this,  "Expense added Succesfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadexpense();
            clearSection();

        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error adding expense", "Failure",JOptionPane.ERROR_MESSAGE);

        }
    }

    private void delete(){
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this expense?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) table.getValueAt(row, 0);
            try {
                boolean deleted = ExpenseDAO.delete(id);
            if (deleted) {
                    JOptionPane.showMessageDialog(this,
                            "expense deleted successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadexpense(); // refresh table
                    clearSection();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete todo",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting expense: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        
    }
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
                if (ExpenseDAO.update(exp)) {
                    JOptionPane.showMessageDialog(this, "expense updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadexpense();
                    clearSection();
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
            loadexpense();
            clearSection();
        }
        
        private void loadexpense(){
            try{
                List<Expense> exp = ExpenseDAO.getAll();
                updateTable(exp);
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this, "Error loading expense : "+e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
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
    private JButton deletecategory;
    private JButton upadatecategory;
    private JButton refreshcategory;
    private JTextField category;
    private DefaultTableModel tablemodel;
    private JTable table;

    public CategoryGUI(){
        initializeComponents();
        setUpLayout();
        setupEventListeners();
        loadcat();
        
    }
    private void initializeComponents(){
        setTitle("Expense-Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
       
        

    }
    
    private void setUpLayout(){
        setLayout(new BorderLayout());
        JPanel buttonpanel=new JPanel(new FlowLayout());
        addcategory=new JButton("Add");
        upadatecategory=new JButton("Update");
        refreshcategory=new JButton("Refresh");
        deletecategory = new JButton("Delete");
        buttonpanel.add(addcategory);
        buttonpanel.add(upadatecategory);
        buttonpanel.add(deletecategory);
        buttonpanel.add(refreshcategory);

        JPanel inputpanel=new JPanel(new GridBagLayout());
        GridBagConstraints g=new GridBagConstraints();
        g.insets=new Insets(10,10,10,10);
        g.gridx=0;
        g.gridy=0;
        inputpanel.add(new JLabel("Category"));
        
        g.gridx=1;
        category=new JTextField();
        category.setPreferredSize(new Dimension(50,25));
        inputpanel.add(category,g);

        JPanel mainp=new JPanel(new BorderLayout());
        mainp.add(inputpanel,BorderLayout.CENTER);
        mainp.add(buttonpanel,BorderLayout.SOUTH);

        String [] filterbox={"Id","categories"};
        tablemodel =new DefaultTableModel(filterbox,0)
        {
            @Override
            public boolean isCellEditable(int row,int column)
            {
                return false;
            }
        };
        table=new JTable(tablemodel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            loadSelectedcat();
            }
        });
        JScrollPane scroll=new JScrollPane(table);
        add(scroll,BorderLayout.CENTER);
        add(mainp,BorderLayout.NORTH);
        
    }
   
    private void setupEventListeners(){
        addcategory.addActionListener((e)->{addc();});
        upadatecategory.addActionListener((e)->{updatec();});
        deletecategory.addActionListener((e)->{delete();});
        refreshcategory.addActionListener((e)->{refreshc();});
        }
    private void loadSelectedcat(){
            int row = table.getSelectedRow();
            if(row != -1){
            category.setText((String)table.getValueAt(row, 1));
            }
        }

    private void loadcat() {
        try {
            List<category> list = categoryDAO.getAll();
            updateTable(list);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<category> list) {
        tablemodel.setRowCount(0);
        for (category c : list) {
            Object[] row = {c.getId(), c.getName()};
            tablemodel.addRow(row);
        }
    }
    private void addc(){
        String cate=category.getText().trim();
        if(cate.equals(""))
            {
                JOptionPane.showMessageDialog(this, "Category cannot be empty", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
        }
        try{
            category cat=new category(cate);
            categoryDAO.add(cat);
            JOptionPane.showMessageDialog(this, "Category added successfully", "sucess", JOptionPane.INFORMATION_MESSAGE);
            loadcat();
            clearSection();
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(this, "Error adding category ", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void delete(){
        int row=table.getSelectedRow();
        if(row==-1){
            JOptionPane.showMessageDialog(this,"Please select a row to delete","Error",JOptionPane.INFORMATION_MESSAGE);
            return;}
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this category?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if(confirm==JOptionPane.YES_OPTION)
        {
            try{
                int id = (int) table.getValueAt(row, 0);
                boolean del=categoryDAO.delete(id);
                if(del){
                    JOptionPane.showMessageDialog(this, "Deleted Category Successfully","ErrSuccessor",JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(this, "Error in deleting category","Error",JOptionPane.INFORMATION_MESSAGE);
                }
                loadcat();
                clearSection();
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error deleting category: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
        
    }
    private void updatec(){
        int row = table.getSelectedRow();
        if (row==-1) {
            JOptionPane.showMessageDialog(this, "Please select a category to update", "Validation Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cate=category.getText().trim();
        try{
            int id=(int) table.getValueAt(row,0);
            com.ExpenseTracker.model.category cat=new com.ExpenseTracker.model.category(id,cate);
            categoryDAO.update(cat);
            JOptionPane.showMessageDialog(this, "Updated succesfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadcat();
            clearSection();
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(this, "Error updating category ", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void refreshc(){
        loadcat();
    }
    private void clearSection(){
        category.setText("");
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
