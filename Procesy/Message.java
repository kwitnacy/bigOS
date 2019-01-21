package Procesy;

public class Message{
    private int senderPID;      //PID nadawcy
    private int size;           // rozmiar
    private String text;        // maksymalnie 8B

    public Message(int senderPID,int size,String text){
        this.senderPID=senderPID;
        this.size=size;
        this.text=text;
    }

    /// funkcje get
    public int get_sender_PID(){ return this.senderPID; }
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

    public static void main(String[] arg){
        for (int i=0; i<128; i++) {
            write_to_ram(i,'.');
        }
        /// $ ma numer 36
        Process_container cont= new Process_container();
        cont.create_process("p1","proc1_txt",5);
        cont.create_process("p2","proc2_txt",5);


        display_ram();
//        cont.get_by_PID(1).send_message("p2",2,0);
//        cont.get_by_PID(2).read_message(0,100);

//        cont.get_by_PID(1).send_message("p2",0);
//        cont.get_by_PID(2).read_message(10);
//        cont.get_by_PID(1).send_message("p2",3,11);
//        cont.get_by_PID(2).read_message(20);
//        cont.get_by_PID(1).send_message(2,"qutaz");
//        cont.get_by_PID(2).read_message(30);
//        cont.get_by_PID(1).send_message(2,30);
//        cont.get_by_PID(2).read_message(40);
//        cont.get_by_PID(1).send_message(2,3,41);
//        cont.get_by_PID(2).read_message(50);
        display_ram();

        if(cont.get_by_PID(2).state==State.Ready){ System.out.println("ready"); }
        else{ System.out.println("non ready"); }
        cont.get_by_PID(2).read_message(30);
        if(cont.get_by_PID(2).state==State.Waiting){ System.out.println("waiting"); }
        else{ System.out.println("non waiting"); }
        cont.get_by_PID(1).send_message(2,"qutaz");
        if(cont.get_by_PID(2).state==State.Ready){ System.out.println("ready"); }
        else{ System.out.println("non ready"); }
        display_ram();
    }
    public static char[] ram = new char [128];
    public static boolean write_to_ram(int addres,char c){
        if(addres<0||addres>128){
            return false;
        }
        else{
            Message.ram[addres]=c;
            return true;
        }

    }
    static Character read_ram(int addres){
        if(addres<0||addres>=128){
            return null;
        }
        else {
            return ram[addres];
        }
    }
    static void  display_ram(){
        System.out.println(ram);
    }
}