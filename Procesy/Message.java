package Procesy;

public class Message{
    private int senderPID;      // PID nadawcy
    private int size;           // rozmiar
    private String text;        // maksymalnie 8B

    Message(int senderPID,int size,String text){
        this.senderPID=senderPID;
        this.size=size;
        this.text=text;
    }

    /// funkcje get
    public int get_sender_PID(){ 
        return this.senderPID; 
    }
    public String get_text(){
        return this.text;
    }
    public int get_size(){
        return this.size;
    }

    /// funkcje set
    public void set_sender_PID(int PID){
        this.senderPID=PID;
    }
    public void set_text(String text){
        this.text=text;
    }
    public void set_size(int size){
        this.size=size;
    }
}