package Procesy;

import java.util.ArrayDeque;
import java.util.Queue;

public class Process {
    int PID;                                    //  Unikatowy idnetyfiaktor
    private int AX, BX, CX, DX;                 //  Rejestry
    private int base_priority, temp_priority;     //  Piorytet bazowy (stały) i tymczasowy (zmienny)
    private int base, limit;                    //  Adres procesu w pamieci RAM
    private int program_counter;                //  Licznik rozkazu programu przydzielonego do procesu
    private int waiting_counter;                //  Zmienna pomocna w rarzadzaniu procesorem
    State state;                                //  Stan procesu
    String name;                                //  Nazwa procesu

    //  Messages
    private String last_message;                //  Ostatnio przeczytana wiadomosci
    private last_message_addres;                //  Adres logiczny wiadomości
    private Queue<String> messages_queue;       //  Referencja do kolejki wiadomosci

    String file_name;

////////////////////////////////////////////////////////////////////////////////////
    public Process(String name, String file_name, int priority, int PID){
        this.state = State.New;
        this.name = name;
        this.base = 0;
        this.limit = 0;

        this.AX = this.BX = this.CX = this.DX = 0;
        this.base_priority = priority;
        this.temp_priority = priority;
        this.PID = PID;

        this.messages = new ArrayDeque<String>();
        this.last_message = "";

        this.program_counter = 0;

        this.state = State.Ready;
    }

    public Process(){

    }

    public void display_process(){
        System.out.println("Name: " + this.name);
        System.out.println("State: " + this.state);
        System.out.println("Memory_base: " + this.base);
        System.out.println("Memory_limit: " + this.limit);
        System.out.println("AX, BX, CX, DX: " + this.AX + " " + this.BX + " " + this.CX + " " + this.DX);
        System.out.println("Piority (base): " + this.base_priority);
        System.out.println("Piority (temp): " + this.temp_priority);
        System.out.println("Message: " + this.last_message);
/*
        if(this.message_byte.equals(""))
            System.out.println("None");
        else
            System.out.println(this.message_byte);
*/
    }

    public void run_process(){
        this.state = State.Running;
        this.program_counter = this.program_counter + 1;
        /*
        *
        *       Dzialanie processu
        *
        */
    }

    public void change_state(State state){
        this.state = state;

        if(this.state == State.Running)
            System.out.println("Biegnie");

        if(this.state == State.Terminated){
            Process_container.delete(this.PID);
            System.out.println("Usuniecie procesu: " + this.name);
        }
    }

////////////////////////////////////////////////////////////////////////////////////
    public int get_PID(){
        return this.PID;
    }

    public int get_base(){
        return this.base;
    }

    public int get_limit(){
        return this.limit;
    }

    public int get_base_priority(){
        return this.base_priority;
    }

    public int get_temp_priority(){
        return this.temp_priority;
    }

    public String get_name(){
        return this.name;
    }

    public String get_last_mess(){
        return this.last_message;
    }

    public Queue<String> get_all_mess(){
        return this.messages;
    }

    public State get_state() {
        return this.state;
    }

    public void make_porocess(String name, String file, int priority){
        Process_container.create_process(name, file, priority);
    }
////Messages//////////////////////////////////////////////////////////////////////
public void send_message(String receiverName,int size, String text){
    Process p = Process_container.get_by_name(receiverName);
    send_message(p.get_PID(),size,text);
}
public void send_message(int receiverPID,int size, String text){
        Process p= Process_container.get_by_PID(receiverPID);
        String message=(++size)+text;
        if(p!=null){
            p.message_queue.add(message);
            System.out.println("[SEND] String in RAM: "+message+" PID: "+p.get_PID());
        }
        else{
            ///błąd, nie znaleziono procesu
        }
    }
    
public void send_message(String receiverName,int size, int addres){
        Process p = Process_container.get_by_name(receiverName);
        send_message(p.get_PID(),size,addres);
    }
public void send_message(int receiverPID,int size, int addres){
        ///kopiowanie komórek od addres do addres+size
        String text="abcd";
        Process p= Process_container.get_by_PID(receiverPID);
        String message=(++size)+text;
        if(p!=null){
            p.message_queue.add(message);
            System.out.println("[SEND] PID: "+p.get_PID()+" String in RAM: "+message);
        }
        else{
            ///błąd, nie znaleziono procesu
        }
}

public String read_message(int size){   /// proces_PID jako argument?
        if(!this.message_queue.isEmpty()){
            String message=this.message_queue.peek();
            System.out.println("[READ] String: "+message+" Text: "+message.substring(1));
            ///zapisywanie do RAM od adresu licznika rozkazów w formacie [ilość znaków +1][znaki]...
            /// przykład: [6][b][i][g][O][S]
            return this.message_queue.poll();
        }
        else{
            ///przenoszenie procesu do kolejki wait
            return null;
        }
}
}
