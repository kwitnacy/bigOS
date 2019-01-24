package Security;

public class User {


    public SaID getSaid() {
        return said;
    }


    private SaID said;
    private boolean active;

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    public void setPassword(String password) {
        this.password = password;
    }

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
        return  "----------------\n"+"USERNAME: " + username +"\nSAID: "+ said.name()+
                "\n" + (active? "STATUS: ACTIVE": "STATUS: INACTIVE")+"\n----------------";
    }
    public void user_toggle()
    {
        this.active=!this.active;
        System.out.println("[Security]: User "+this.username+" is now"+ ((this.active)? " active.": " inactive."));
    }



}
