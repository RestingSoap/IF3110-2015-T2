/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnswerModel;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import java.util.ArrayList;
import java.sql.Connection;
import DatabaseAdapter.database;
import java.sql.*;
import java.util.logging.*;

/**
 *
 * @author user
 */
@WebService(serviceName = "AnswerWS")
public class AnswerWS {
    
    database DB = new database();
    
    Connection conn = DB.connect();
    
    @WebMethod(operationName = "getAnswerByQID")
    @WebResult(name="Answer")
    public ArrayList<Answer> getAnswerByQID(@WebParam(name = "qid") int qid) {
        
        ArrayList<Answer> answers = new ArrayList<>();
        
        try {
            Statement stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM answers where question_id = ?";
            
            PreparedStatement dbStatement = conn.prepareStatement(sql);
            dbStatement.setInt(1, qid);
            
            ResultSet rs = dbStatement.executeQuery();
            
            int i = 0;
            while (rs.next()) {
                answers.add(new Answer(rs.getInt("answer_id"), 
                                        rs.getInt("question_id"), 
                                        rs.getInt("user_id"), 
                                        rs.getString("content"),
                                        rs.getInt("vote"),
                                        rs.getString("time")
                                        ));
                ++i;
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
           Logger.getLogger(AnswerWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return answers;
    }

    @WebMethod(operationName = "createAnswer")    
    @WebResult(name="AnswerID")
    public int createAnswer(@WebParam(name = "uid") int uid, @WebParam(name = "qid") int qid, @WebParam(name = "content") String content) {
        int aid = 0;
        // Call Identity Service
        
        try {
            Statement stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO answers(question_id,user_id,content,vote) VALUES (?,?,?,0)";
            
            PreparedStatement dbStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            dbStatement.setInt(1, qid);
            dbStatement.setInt(2, uid);
            dbStatement.setString(3, content);
            dbStatement.executeUpdate();
            ResultSet rs = dbStatement.getGeneratedKeys();
            while (rs.next()) {
		aid = rs.getInt(1);
            }            
            
            String updateAnsCount;
            updateAnsCount = "UPDATE questions SET answer_count = answer_count + 1 WHERE question_id = ?";
            dbStatement = conn.prepareStatement(updateAnsCount);
            dbStatement.setInt(1, qid);            
            dbStatement.executeUpdate();
            
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
           Logger.getLogger(AnswerWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return aid;
    }
    
    @WebMethod(operationName = "voteAnswer")    
    @WebResult(name="AnswerID")
    public int voteAnswer(@WebParam(name = "aid") int aid, @WebParam(name = "vote") int vote) {            
        try {
            Statement stmt = conn.createStatement();
                                    
            String sql;
            sql = "UPDATE answers SET vote = vote + ? WHERE answer_id = ?";
                    
            PreparedStatement dbStatement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            dbStatement.setInt(1, vote);
            dbStatement.setInt(2, aid);
                    
            dbStatement.executeUpdate();
            ResultSet rs = dbStatement.getGeneratedKeys();
            while (rs.next()) {
                aid = rs.getInt(1);
            }    
                    
            rs.close();
            stmt.close();
                    
        } catch (SQLException ex) {
            Logger.getLogger(AnswerWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return aid;
    }
}
