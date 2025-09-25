package com.ExpenseTracker.dao;


import com.ExpenseTracker.util.DatabaseConnection;
import java.sql.ResultSet;
import com.ExpenseTracker.model.Expense;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.awt.Taskbar.State;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import com.ExpenseTracker.model.category;

public class categoryDAO{
    private static final String SELECT_ALL = "SELECT * FROM category order by id ";
    private static final String INSERT_CAT="Insert INTO category(name) VALUES(?)";
    private static final String SELECT_CAT_BY_ID = "SELECT * FROM category WHERE id = ?";
    private static final String UPDATE_CAT = "UPDATE category SET name = ? WHERE id=?";
    private static final String FILTER_CAT="SELECT * FROM category WHERE name=?";

    public static int add(category cat) throws SQLException
    {
        try(Connection conn=DatabaseConnection.getDBConnection();
        PreparedStatement stmt=conn.prepareStatement(INSERT_CAT,Statement.RETURN_GENERATED_KEYS);
        )
        {
            stmt.setString(1, cat.getName());
            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected==0)
                throw new SQLException("No rows affected error in creating category");
            try(ResultSet rs=stmt.getGeneratedKeys()){
                if(rs.next()){
                    return rs.getInt(1);
                    }
                else{
                    throw new SQLException("Error in creating category");
                }
            }

        }


    }
    public static category getByRow(ResultSet res)throws SQLException
    {
        String name=res.getString("name");
        int id = res.getInt("id");
        category cat=new category(id,name); 
        return cat;

    }
    public static List<category> filter(String st) throws SQLException
    {
        try(
            Connection conn=DatabaseConnection.getDBConnection();
            PreparedStatement stmt=conn.prepareStatement(FILTER_CAT);
        )
        {
            stmt.setString(1, st);
            ArrayList<category> list = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()){
                    list.add(new category(rs.getInt("id"),rs.getString("name")));
                }
            }
            
        return list;
        }
        

    }
    public static boolean update(category cat) throws SQLException
    {
        try(
            Connection conn=DatabaseConnection.getDBConnection();
            PreparedStatement stmt=conn.prepareStatement(UPDATE_CAT);
        ){
            stmt.setString(1, cat.getName());
            stmt.setInt(2, cat.getId());
            int rows=stmt.executeUpdate();
            return rows>0;
        }
        
    }
    public static List<category> getAll() throws SQLException{
        List<category> cat=new ArrayList<>();
        try(
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
            ResultSet res=stmt.executeQuery();)
            {
            while(res.next())
            {
                cat.add(getByRow(res));
            }
        } catch(SQLException e) {
            System.err.println("Error fetching category: "+e.getMessage());
            throw e;

        }
        return cat;
    }
    public static category getById(int id)throws SQLException
    {
        try(
            Connection conn=DatabaseConnection.getDBConnection();
            PreparedStatement stmt=conn.prepareStatement(SELECT_CAT_BY_ID);
        ){
            stmt.setInt(1, id);
            ResultSet res=stmt.executeQuery();
            if (res.next()) {
                    return new category(res.getInt("id"), res.getString("name"));
            }
            return null;
        }
    }

    
}
