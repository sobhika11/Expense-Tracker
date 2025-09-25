package com.ExpenseTracker.dao;

import com.ExpenseTracker.util.DatabaseConnection;
import com.ExpenseTracker.model.Expense;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;

public class ExpenseDAO {
    private static final String SELECT_ALL = "SELECT * FROM expense order by eid ";
    private static final String INSERT_EXPENSE="Insert INTO expense(description,amount,date,category,cid) VALUES(?,?,?,?,?)";
    private static final String SELECT_TODO_BY_ID = "SELECT * FROM expense WHERE eid = ?";
    private static final String UPDATE_EXPENSE = "UPDATE expense SET description = ?, amount = ?, date = ? WHERE eid = ?";
    private static final String DELETE_EXPENSE = "DELETE FROM expense WHERE eid = ?";
    private static final String FILTER_EXPENSE = "SELECT *FROM expense WHERE category = ? ";
    private static final String CATEGORY_ID="SELECT id FROM category WHERE name=?";

    public static Expense getById(int id) throws SQLException{
        try (
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_TODO_BY_ID)
        ) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return getByRow(rs);
                }
            }
        }
        return null; 
    
    }
    public static Expense getByRow(ResultSet res)throws SQLException
    {
        int id=res.getInt("eid");
        String description=res.getString("description");
        String date=res.getString("date");
        double amt=res.getDouble("amount");
        Expense exp=new Expense(id,description,date,amt); 
        return exp;

    }
    public static int add(Expense exp) throws SQLException
    {
        try(
            Connection conn=DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_EXPENSE, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement stmt2 = conn.prepareStatement(CATEGORY_ID);
            )
            {
            stmt2.setString(1, exp.getCategory());
            ResultSet cat_id = stmt2.executeQuery();
            int catid = 0;
            if(cat_id.next()) catid = cat_id.getInt("id");
            cat_id.close();
            stmt.setString(1, exp.getDescription());
            stmt.setDouble(2, exp.getAmount());
            stmt.setString(3, exp.getDate());
            stmt.setString(4, exp.getCategory()); 
            stmt.setInt(5, catid);
            int rowAffected=stmt.executeUpdate();

                        if(rowAffected==0){
               throw new SQLException("Creating expense is failed ,no row is Insertes");
            }
            try(ResultSet generatedKeys = stmt.getGeneratedKeys()){
                if(generatedKeys.next()){
                    return generatedKeys.getInt(1);
                }
                else{
                    throw new SQLException("Creating todo failed,No id obtained");
                    
                }

            }
        }
    }
    public static boolean updateTodo(Expense exp) throws SQLException {
        try (
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_EXPENSE)
        ) {
            stmt.setString(1, exp.getDescription());
            stmt.setString(3, exp.getDate());
            stmt.setDouble(2, exp.getAmount());
            stmt.setInt(4, exp.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
        
    }
    // public static int delete(int id) throws SQLException
    // {

    // }
    public static List<Expense> getAll() throws SQLException{
        List<Expense> exp=new ArrayList<>();
        try(
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
            ResultSet res=stmt.executeQuery();)
            {
            while(res.next())
            {
                exp.add(getByRow(res));
            }
        } catch(SQLException e) {
            System.err.println("Error fetching expense: "+e.getMessage());
            throw e;

        }
        return exp;
        

    }


}
