package com.example.justchat;

public class UpdatePost {
    private String postname;
    private String postdescription;
    private String senderpicture;
    private String post;
    private String postid;

    public UpdatePost() {
    }

    public String getPostname() {
        return postname;
    }

    public void setPostname(String postname) {
        this.postname = postname;
    }

    public String getPostdescription() {
        return postdescription;
    }

    public void setPostdescription(String postdescription) {
        this.postdescription = postdescription;
    }

    public String getSenderpicture() {
        return senderpicture;
    }

    public void setSenderpicture(String senderpicture) {
        this.senderpicture = senderpicture;
    }

    public String getPost() {
        return post;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public UpdatePost(String postname, String postdescription, String senderpicture, String post,String postid) {
        this.postname = postname;
        this.postdescription = postdescription;
        this.senderpicture = senderpicture;
        this.postid=postid;
        this.post = post;
    }
}
