/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author c0665877
 */
public class Message {

    private int id;
    private String title;
    private String contents;
    private String author;
    private Date sentTime;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public Message() {
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public String getAuthor() {
        return author;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public SimpleDateFormat getSdf() {
        return sdf;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public void setSdf(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }

    public Message(JsonObject json) {
        id = json.getInt("id",0);
        title = json.getString("title"," ");
        contents=json.getString("contents"," ");
        author= json.getString("author"," ");
        String timeSetting =json.getString("sentTime");
        
        try {
            sentTime=sdf.parse(timeSetting);
        } catch (ParseException ex) {
            sentTime =new Date();
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, "Failed Parsing the date" +timeSetting);
        }
    }
    
    public JsonObject toJson(){
        String timeSetting = sdf.format(sentTime);
        return Json.createObjectBuilder().add("id", id).add("title", title).add("contents", contents).add("author", author)
                .add("sentTime", timeSetting).build();
    }

}
