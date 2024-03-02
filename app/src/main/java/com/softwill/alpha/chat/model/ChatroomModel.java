package com.softwill.alpha.chat.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class ChatroomModel implements Serializable {
    String latestMessage;
    List<String>  participants;
    HashMap<String, String> receiver;
    HashMap<String, String> sender;
    String timestamp;
    String fileName;
    Boolean isRead;
    String attachment;


    public ChatroomModel() {
    }

    public ChatroomModel(String latestMessage,String fileName, List<String> userIds, HashMap<String, String> receiver, HashMap<String, String> sender, String timestamp, String attachment) {
        this.latestMessage = latestMessage;
        this.fileName = fileName;
        this.participants = userIds;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.attachment = attachment;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean isRead() {
        return isRead;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public HashMap<String, String> getReceiver() {
        return receiver;
    }

    public void setReceiver(HashMap<String, String> receiver) {
        this.receiver = receiver;
    }

    public HashMap<String, String> getSender() {
        return sender;
    }

    public void setSender(HashMap<String, String> sender) {
        this.sender = sender;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
