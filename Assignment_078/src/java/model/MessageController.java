/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.sql.*;

/**
 *
 * @author c0665877
 */
public class MessageController {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private List<Message> messages;

    public MessageController() {

    }

    public void retrieveAllMessages() {
        try {
            if (messages == null) {
                messages = new ArrayList<>();
            }

            messages.clear();

            Connection conn = DBUtils.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM messages");
            while (rs.next()) {
                Message msg = new Message();
                msg.setId(rs.getInt("id"));
                msg.setTitle(rs.getString("title"));
                msg.setContents(rs.getString("contents"));
                msg.setAuthor(rs.getString("author"));
                msg.setSentTime(rs.getDate("sentTimr"));
                messages.add(msg);
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void persistToDB(Message msg) {
        try {
            String sql = "";
            Connection conn = DBUtils.getConnection();
            if (msg.getId() <= 0) {
                sql = "INSERT INTO messages (title,contents,author,sentTime) VALUES(?,?,?,?)";
            } else {
                sql = "UPDATE messages SET title=?,contents=?,author=?,sentTime=? WHERE id=?";
            }
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, msg.getTitle());
            pstmt.setString(2, msg.getContents());
            pstmt.setString(3, msg.getAuthor());
            pstmt.setDate(4, new java.sql.Date(msg.getSentTime().getTime()));
            if (msg.getId() > 0) {
                pstmt.setInt(5, msg.getId());
            }
            pstmt.executeUpdate();
            if (msg.getId() <= 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                rs.next();
                int id = rs.getInt(1);
                msg.setId(id);
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeFromDb(Message msg) {

        try {
            Connection conn = DBUtils.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM messages WHERE id=?");
            pstmt.setInt(1, msg.getId());
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public JsonArray getJsonAll(){
        JsonArrayBuilder json = Json.createArrayBuilder();
        for (Message msg : messages){
            json.add(msg.toJson());
        }
        return json.build();
    }
    
    public Message getById(int id){
        for(Message msg : messages){
            if(msg.getId()==id){
                return msg;
            }
        }
        return  null;
    }
    
    public JsonObject getByIdJson(int id){
       Message msg=getById(id);
       
            if(msg!=null){
                return getById(id).toJson();
            }
            else{
                return null;
            }
        }
    
    public JsonArray getByDateJson(Date from,Date to){
        JsonArrayBuilder json =Json.createArrayBuilder();
        for (Message msg : messages){
            if((msg.getSentTime().after(from)&&msg.getSentTime().before(to)) 
                    ||msg.getSentTime().equals(from) || msg.getSentTime().equals(to)
                    ){
                json.add(msg.toJson());
            }
        }
        return json.build();
    }
    
    public  JsonObject addingJson(JsonObject json){
        Message msg= new Message(json);
        persistToDB(msg);
        messages.add(msg);
        return msg.toJson();
    }
    
    public JsonObject editJson(int id, JsonObject json){
        Message msg = getById(id);
        msg.setTitle(json.getString("title",""));
        msg.setContents(json.getString("contents",""));
        msg.setAuthor(json.getString("author",""));
        String timeSetting =json.getString("sentTime","");
        try {
            msg.setSentTime(sdf.parse(timeSetting));
        } catch (ParseException ex) {
           msg.setSentTime(new Date());
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        persistToDB(msg);
        return msg.toJson();
    }
    
    public  boolean deleteById(int id){
         Message msg= new Message();
         if(msg!= null){
             removeFromDb(msg);
             messages.remove(id);
             return true;
         }
         else{
         return false;
         }
         
    }
      

}
