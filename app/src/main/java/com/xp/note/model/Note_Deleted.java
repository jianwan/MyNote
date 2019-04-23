package com.xp.note.model;

import cn.bmob.v3.BmobObject;

/**
 * Note_Deleted 的 bean 类，同 note
 */
public class Note_Deleted extends BmobObject {

    private String title;
    private String content;
    private String priority;
    private String user;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


}
