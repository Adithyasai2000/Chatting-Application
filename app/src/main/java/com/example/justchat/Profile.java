package com.example.justchat;

class Profile {
   private String groupname;
   private String groupdescp;
   private String groupkey;

    public Profile() {
    }

    public Profile(String groupname, String groupdescp, String groupkey) {
        this.groupname = groupname;
        this.groupdescp = groupdescp;
        this.groupkey = groupkey;
    }

    public String getGroupkey() {
        return groupkey;
    }

    public void setGroupkey(String groupkey) {
        this.groupkey = groupkey;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getGroupdescp() {
        return groupdescp;
    }

    public void setGroupdescp(String groupdescp) {
        this.groupdescp = groupdescp;
    }

}
