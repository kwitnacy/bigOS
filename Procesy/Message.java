package Procesy;

public class Message{
    private int senderPID;      //PID nadawcy
    private int size;           // rozmiar
    private String text;        // maksymalnie 8B

    Message(int senderPID,int size,String text){
        this.senderPID=senderPID;
        this.size=size;
        this.text=text;
    }

    /// funkcje get
    public int get_send_PID(){
        return this.senderPID;
    }
    public String get_text(){
        return this.text;
    }
    public int get_size(){
        return this.size;
    }
    /// funkcje set
    public void set_send_PID(int PID){
        this.senderPID=PID;
    }
    public void set_text(String text){
        this.text=text;
    }
    public void set_size(int size){
        this.size=size;
    }

    public static void main(String[] arg){
        Message msg=new Message(69,5,"abcde");
        char ram_msg[]=msg.get_text().toCharArray();
        ///zapisywanie do RAM od adresu licznika rozkazów w formacie [ilość znaków +1][znaki]...
        /// przykład: [PID jako char][b][i][g][O][S]
//        for(int i=0; i<ram_msg.length();i++){
//            //write(++program_counter,ram_msg.charAt(i));
//        }
        //System.out.println((int) ram_msg.charAt(0));
        System.out.println("[READ] Sender: "+ram_msg[0]+" Text: "+ram_msg.length);
    }
}