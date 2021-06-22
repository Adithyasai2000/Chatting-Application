package com.example.justchat;

public class UpdateVedioPost {

    private String vediopostname;
    private String vediopostdescription;
    private String vediosenderpicture;
    private String vediopost;
    private String vediopostid;

    public String getVediopostname() {
        return vediopostname;
    }

    public void setVediopostname(String vediopostname) {
        this.vediopostname = vediopostname;
    }

    public String getVediopostdescription() {
        return vediopostdescription;
    }

    public void setVediopostdescription(String vediopostdescription) {
        this.vediopostdescription = vediopostdescription;
    }

    public String getVediosenderpicture() {
        return vediosenderpicture;
    }

    public void setVediosenderpicture(String vediosenderpicture) {
        this.vediosenderpicture = vediosenderpicture;
    }

    public String getVediopost() {
        return vediopost;
    }

    public void setVediopost(String vediopost) {
        this.vediopost = vediopost;
    }

    public String getVediopostid() {
        return vediopostid;
    }

    public void setVediopostid(String vediopostid) {
        this.vediopostid = vediopostid;
    }

    public UpdateVedioPost(String vediopostname, String vediopostdescription, String vediosenderpicture, String vediopost, String vediopostid) {
        this.vediopostname = vediopostname;
        this.vediopostdescription = vediopostdescription;
        this.vediosenderpicture = vediosenderpicture;
        this.vediopost = vediopost;
        this.vediopostid = vediopostid;
    }

    public UpdateVedioPost() {
    }
}
