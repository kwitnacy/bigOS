package Procesy;

import Processor.Scheduler;
import RAM.Memory;
import Semaphore.Semaphore;

import java.util.ArrayDeque;
import java.util.Queue;

public class Process {
    int PID;                                    //  Unikatowy idnetyfiaktor
    private int AX, BX, CX, DX;                 //  Rejestry
    private int base_priority, temp_priority;   //  Piorytet bazowy (stały) i tymczasowy (zmienny)
    private int base, limit;                    //  Adres procesu w pamieci RAM
    private int program_counter;                //  Licznik rozkazu programu przydzielonego do procesu
    private int waiting_counter;                //  Zmienna pomocna w rarzadzaniu procesorem
    State state;                                //  Stan procesu
    String name;                                //  Nazwa procesu

    //  Messages
    private Message last_message;               //  Ostatnia przeczytana wiadomosci
    private Queue<Message> messages_queue;      //  Kolejka odebranych wiadomości
    private Semaphore messages_semaphore;       //  Semafor kontrolujący odczytywanie wiadomości

    String file_name;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Process(String name, String file_name, int priority, int PID){
        this.state = State.New;
        this.name = name;
        this.base = 0;
        this.limit = 0;

        this.AX = this.BX = this.CX = this.DX = 0;
        this.base_priority = priority;
        this.temp_priority = priority;
        this.PID = PID;

        this.messages_queue = new ArrayDeque<Message>();
        this.messages_semaphore= new Semaphore(0);
        this.last_message = new Message(-1,-1,null);

        this.program_counter = 0;

        this.file_name = file_name;

        this.state = State.Ready;
    }

    public Process(String name, String file_name, int priority, int PID, int limit) {
        this.state = State.New;
        this.name = name;
        this.base = 0;
        this.limit = limit;

        this.AX = this.BX = this.CX = this.DX = 0;
        this.base_priority = priority;
        this.temp_priority = priority;
        this.PID = PID;

        this.messages_queue = new ArrayDeque<Message>();
        this.messages_semaphore= new Semaphore(0);
        this.last_message = new Message(-1,-1,null);

        this.program_counter = 0;

        this.file_name = file_name;

        this.state = State.Ready;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

    public int get_waiting_counter(){
        return this.waiting_counter;
    }

    public String get_name(){
        return this.name;
    }

    public String get_file_name(){
        return this.file_name;
    }

    public State get_state() {
        return this.state;
    }

    public int get_AX() {
        return this.AX;
    }

    public int get_BX() {
        return this.BX;
    }

    public int get_CX() {
        return this.CX;
    }

    public int get_DX() {
        return this.DX;
    }

    public int get_program_counter(){
        return this.program_counter;
    }

    public void set_AX(int AX) {
        this.AX = AX;
    }

    public void set_BX(int BX) {
        this.BX = BX;
    }

    public void set_CX(int CX) {
        this.CX = CX;
    }

    public void set_DX(int DX) {
        this.DX = DX;
    }

    public  void ser_base(int base){
        this.base = base;
    }

    public  void ser_limit(int limit){
        this.limit = limit;
    }

    public void set_program_counter(int program_counter){
        this.program_counter = program_counter;
    }

    public void set_waiting_counter(int waiting_counter){
        this.waiting_counter = waiting_counter;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void display_process(){
        System.out.println("+-------------------------------------------------------+");
        System.out.println("|Name: " + this.name);
        System.out.println("|State: " + this.state);
        System.out.println("|Memory_base: " + this.base);
        System.out.println("|Memory_limit: " + this.limit);
        System.out.println("|AX, BX, CX, DX: " + this.AX + " " + this.BX + " " + this.CX + " " + this.DX);
        System.out.println("|Piority (base): " + this.base_priority);
        System.out.println("|Piority (temp): " + this.temp_priority);
        System.out.println("|Message: " + this.last_message.get_text());
        System.out.println("+-------------------------------------------------------+");
    }

    public void change_state(State state){
        this.state = state;

        if(this.state == State.Running && !this.name.equals("dummy"))
            System.out.println("[Process_Manager]: Changed state of process: " + this.name + " to Running");
		
		if(this.state == State.Ready && !this.name.equals("dummy"))
            System.out.println("[Process_Manager]: Changed state of process: " + this.name + " to Ready");

        if(this.state == State.Terminated){
            Process_container.delete(this.PID);
            System.out.println("[Process_Manager]: Deleted process " + this.name);
            Scheduler.schedule();       // tego nie jestem pewny ale chyba powinno byc jak sie usuwa proces zeby odrazu
                                        // zastapic go nowym.
        }

        if(this.state == State.Waiting){
            System.out.println("[Process_Manager]: Changed state of process: " + this.name + " to Waiting");
            Scheduler.schedule();
        }
    }

    public static void make_porocess(String name, String file, int priority){
        Process_container.create_process(name, file, priority);
    }

    public static void make_porocess(String name, String file, int priority, int limit){
        Process_container.create_process(name, file, priority, limit);
    }

    public void inc_temp_priority(){
        this.temp_priority++;
    }

    public void dec_temp_priority(){
        this.temp_priority--;
    }

    public void inc_waiting_counter(){
        this.waiting_counter++;
    }

    public void dec_waiting_counter(){
        this.waiting_counter--;
    }

///Messages/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Message get_last_message(){
        return this.last_message;
    }
    public Queue<Message> get_messages_queue(){
        return this.messages_queue;
    }
    public Semaphore get_messages_semaphore(){
        return this.messages_semaphore;
    }
    public void display_messages_queue(){
        Queue<Message> display=this.messages_queue;
        System.out.println("Process "+this.get_PID()+" received messages:");
        if(display.isEmpty()){
            System.out.println("Messages queue is empty.");
        }
        else{
            int counter=1;
            while(!display.isEmpty()){
                Message m=display.poll();
                System.out.println("Message "+counter+": Sender PID:"+m.get_sender_PID()+" Size:"+m.get_size()+" Text:"+m.get_text());
            counter++;
            }
        }
    }
	public void display_messages_semaphore(){
        System.out.println("Process "+this.get_PID()+" messages semaphore value is: "+messages_semaphore.getValue());
    }


    public boolean send_message(String receiverName, int addres){
        if(Process_container.get_by_name(receiverName)!=null){
            return send_message(Process_container.get_by_name(receiverName).get_PID(),addres);
        }
        else{
            System.out.println("[IPC] Sending failure, invalid receiver name.");
            return false;
        }
    }
    public boolean send_message(String receiverName, int size, int addres){
        if(Process_container.get_by_name(receiverName)!=null){
            return send_message(Process_container.get_by_name(receiverName).get_PID(),size,addres);
        }
        else{
            System.out.println("[IPC] Sending failure, invalid receiver name.");
            return false;
        }
    }
    public boolean send_message(String receiverName, String text){
        if(Process_container.get_by_name(receiverName)!=null){
            return send_message(Process_container.get_by_name(receiverName).get_PID(),text);
        }
        else{
            System.out.println("[IPC] Sending failure, invalid receiver name.");
            return false;
        }
    }


    public boolean send_message(int receiverPID, int addres){
        String text="";
        int counter=0;

        while(Memory.readMemory(addres+counter+1)!='$'){

            text+=Memory.readMemory(addres+counter+1);

            counter++;

            if(Memory.readMemory(addres+counter+1)==null){
                System.out.println("[IPC] Sending failure, memory error.");
                return false;
            }
        }

        return send_message(receiverPID,text);
    }
    public boolean send_message(int receiverPID, int size, int addres){
        String text="";

        for(int i=0; i<size; i++){
            if(Memory.readMemory(addres+i)!=null){
                text+=Memory.readMemory(addres+i);
            }
            else{
                System.out.println("[IPC] Sending failure, memory error.");
                return false;
            }
        }
        return send_message(receiverPID,text);
    }
    public boolean send_message(int receiverPID, String text){
        if(Process_container.get_by_PID(receiverPID)==null){
            System.out.println("[IPC] Sending failure, invalid receiver PID.");
            return false;
        }
        else if(text.contains("$")){
            System.out.println("[IPC] Sending failure, forbidden character in text.");
            return false;
        }
        else if(text.length()>8||text.length()<=0){
            System.out.println("[IPC] Sending failure, size og message must be in <1,8>.");
            return false;
        }

        Process p = Process_container.get_by_PID(receiverPID);
        Message msg=new Message(this.PID,text.length(),text);

        p.messages_queue.add(msg);
        p.get_messages_semaphore().signal_s();

        System.out.println("[IPC] Succesfully sent message!");
        System.out.println("[IPC] PID:"+msg.get_sender_PID()+" Size:"+msg.get_size()+" Text:"+msg.get_text());
        return true;
    }


    public boolean read_message(int size, int addres){
        this.messages_semaphore.wait_s(this.PID);

        if(this.state==State.Waiting) {
            System.out.println("[IPC] Reading failure, there are no messages to read.");
            return false;
        }
        else if(size<=0||size>this.messages_queue.peek().get_size()){
            System.out.println("[IPC] Reading failure, message size : "+this.messages_queue.peek().get_size());
            return false;
        }

        this.last_message=this.messages_queue.peek();
        String ram_msg = (char)last_message.get_sender_PID()+last_message.get_text().substring(0,size)+'$';

        if(!Memory.writeMessage(this,ram_msg,addres)){
            System.out.println("[IPC] Reading failure, memory error.");
            return false;
        }

        this.messages_queue.poll();
        int written_pid=ram_msg.charAt(0);

        System.out.println("[IPC] Succesfully read message!");
        System.out.println("[IPC] Sender: "+written_pid+" Text: "+ram_msg.substring(1,ram_msg.length()-1));
        return true;
    }
    public boolean read_message(int addres){
        this.messages_semaphore.wait_s(this.PID);

        if(this.state==State.Waiting) {
            System.out.println("[IPC] Reading failure, there are no messages to read.");
            return false;
        }

        this.last_message=this.messages_queue.peek();
        String ram_msg = (char)last_message.get_sender_PID()+last_message.get_text()+'$';

        if(!Memory.writeMessage(this,ram_msg,addres)){
            System.out.println("[IPC] Reading failure, memory error.");
            return false;
        }

        this.messages_queue.poll();
        int written_pid=ram_msg.charAt(0);

        System.out.println("[IPC] Succesfully read message!");
        System.out.println("[IPC] Sender: "+written_pid+" Text: "+ram_msg.substring(1,ram_msg.length()-1));
        return true;
    }

}