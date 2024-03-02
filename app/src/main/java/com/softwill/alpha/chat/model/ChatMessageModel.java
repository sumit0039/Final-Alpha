package com.softwill.alpha.chat.model;

import com.google.firebase.Timestamp;

public class ChatMessageModel {


    private String avtarUrl;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;
    private String isDeleted;
    private Boolean isRead;
    private String message;
    private String name;
    private String senderId;
    private String timestamp;
    private String attachment;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String avtarUrl,String fileName,String isDeleted,Boolean isRead,String message, String name, String senderId, String timestamp, String attachment) {
        this.fileName = fileName;
        this.avtarUrl = avtarUrl;
        this.isDeleted = isDeleted;
        this.isRead = isRead;
        this.message = message;
        this.name = name;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.attachment = attachment;
    }

    public String getAvtarUrl() {
        return avtarUrl;
    }

    public void setAvtarUrl(String avtarUrl) {
        this.avtarUrl = avtarUrl;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean aFalse) {
        isRead = aFalse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
  public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
