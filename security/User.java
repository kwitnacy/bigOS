import sun.rmi.server.UnicastServerRef;

import javax.swing.*;

public class User {


    private SaID said;
    private boolean active;
    private String username;
    private String password;

    public String getUsername(){return username;}
    protected String getPassword() {return password; }
    public int getSaIDValue() { return said.getValue(); }
    public boolean isActive() { return active; }



    public User(int said_, boolean active, String username, String password) {
        said = SaID.fromInt(said_);
        this.active = active;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString()
    {
        return  "USERNAME: " + username +"\nSAID: "+ said.name()+
                "\n" + (active? "STATUS: ACTIVE\n": "STATUS: INACTIVE\n");
    }
    public void user_toggle()
    {
        this.active=!this.active;
        System.out.println("User "+this.username+" is now"+ ((this.active)? " active.": " inactive."));
    }



}
