package com.ExpenseTracker;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import com.ExpenseTracker.gui.ExpenseTrackerAPPGUI;
import com.ExpenseTracker.util.DatabaseConnection;
public class Main {
    public static void main(String args [])
    {
        DatabaseConnection conn=new DatabaseConnection();
        try{
            Connection cn=conn.getDBConnection();
            System.out.println("Database Connection Succesful");
        }
        catch(SQLException e)
        {
            System.err.println("Database connection failed");
        }
    
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e){
            System.err.println("Could not set look and feel "+ e.getMessage());
        }
        SwingUtilities.invokeLater(
            ()->{
                try
                {
                new ExpenseTrackerAPPGUI().setVisible(true);
                }
                catch(Exception e)
                {
                    System.err.println("Error starting the application" +e.getMessage());
                }
            });

        }

}
