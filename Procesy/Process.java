package Procesy;

import RAM.Memory;
import Semaphore.Semaphore;

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
    private int messages_addr;                  //  Adres logiczny wiadomości
    private Queue<String> messages_queue;       //  Referencja do kolejki wiadomosci
    private Semaphore messages_semaphore;       //  Semafor kontrolujący odbieranie wiadomości

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

        this.messages_queue = new ArrayDeque<String>();
        this.messages_semaphore= new Semaphore(0);
        this.last_message = "";
        this. messages_addr=0;

        this.program_counter = 0;

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

        this.messages_queue = new ArrayDeque<String>();
        this.messages_semaphore= new Semaphore(0);
        this.last_message = "";
        this. messages_addr=0;

        this.program_counter = 0;

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
    public void set_AX(int AX) {
        this.AX=AX;
    }
    public void set_BX(int BX) {
        this.BX=BX;
    }
    public void set_CX(int CX) {
        this.CX=CX;
    }
    public void set_DX(int DX) {
        this.DX=DX;
    }


    public void set_waiting_counter(int waiting_counter){
        this.waiting_counter = waiting_counter;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

    public void change_state(State state){
        this.state = state;

        if(this.state == State.Running)
            System.out.println("Biegnie");

        if(this.state == State.Terminated){
            Process_container.delete(this.PID);
            System.out.println("Usuniecie procesu: " + this.name);
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
        this.temp_priority++;
    }

    public void dec_waiting_counter(){
        this.temp_priority--;
    }

///Messages/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String get_last_message(){
        return this.last_message;
    }
    public Queue<String> get_messages_queue(){
        return this.messages_queue;
    }
    public Semaphore get_messages_semaphore(){
        return this.messages_semaphore;
    }

    public boolean send_message(String receiverName,int size, String text){
        Process p = Process_container.get_by_name(receiverName);
        return send_message(p.get_PID(),size,text);
    }
    public boolean send_message(int receiverPID,int size, String text){

        Process p= Process_container.get_by_PID(receiverPID);
        String message=(++size)+text;

        if(p!=null){
            p.messages_queue.add(message);
            p.get_messages_semaphore().signal_s();
            System.out.println("[SEND] PID: "+p.get_PID()+" String in RAM: "+message);
            return true;
        }
        else{
            ///błąd, nie znaleziono procesu
            return false;
        }
    }

    boolean send_message(String receiverName,int size, int addres){
        Process p = Process_container.get_by_name(receiverName);
        return send_message(p.get_PID(),size,addres);
    }
    public boolean send_message(int receiverPID,int size, int addres){
        String text="";

        for(int i=addres; i<addres+size; i++){
            text+= Memory.readMemory(i);
        }

        Process p= Process_container.get_by_PID(receiverPID);
        String message=(++size)+text;

        if(p!=null){
            p.messages_queue.add(message);
            p.get_messages_semaphore().signal_s();
            System.out.println("[SEND] PID: "+p.get_PID()+" String in RAM: "+message);
            return true;
        }
        else{
            ///błąd, nie znaleziono procesu
            return false;
        }
    }

    public String read_message(int size){   /// proces_PID jako argument?

        String message=this.messages_queue.peek();
        this.messages_semaphore.wait_s();
        ///zapisywanie do RAM od adresu licznika rozkazów w formacie [ilość znaków +1][znaki]...
        /// przykład: [6][b][i][g][O][S]
        System.out.println("[READ] String: "+message+" Text: "+message.substring(1));
        return this.messages_queue.poll();
    }
}
