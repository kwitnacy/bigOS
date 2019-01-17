package Procesy;

import Processor.Scheduler;
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
    }

    public void change_state(State state){
        this.state = state;

        if(this.state == State.Running)
            System.out.println("Biegnie");

        if(this.state == State.Terminated){
            Process_container.delete(this.PID);
            System.out.println("Usuniecie procesu: " + this.name);
        }

        if(this.state == State.Waiting){
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
        this.temp_priority++;
    }

    public void dec_waiting_counter(){
        this.temp_priority--;
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

    public boolean send_message(String receiverName,int size, int addres){

        if(Process_container.get_by_name(receiverName)!=null){
            return send_message(Process_container.get_by_name(receiverName).get_PID(),size,addres);
            /// odnaleziono proces, przekazano do odpowieniej funkcji
        }
        else{
            System.out.println("[SM] Sending failure!");
            System.out.println("[SM] Invalid receiver name!");
            return false;
            /// nie ma takiego procesu
        }
    }
    public boolean send_message(int receiverPID,int size, int addres){
        String text="";
        int counter=0;

//        while(Message.read_ram(addres+1+counter)!='$'){
//            text+=Message.read_ram(addres+1+counter);
//            counter++;
//            if(addres+1+counter>=128){
//                System.out.println("[SM] Sending failure!");
//                System.out.println("[SM] Invalid initial addres!");
//                return false;
//            }
//        }
        /// wczytaj tekst wiadomości

        return send_message(receiverPID,size,text);
        /// podaj do odpowiedniej funkcji
    }

    public boolean send_message(String receiverName,int size, String text){

        if(Process_container.get_by_name(receiverName)!=null){
            return send_message(Process_container.get_by_name(receiverName).get_PID(),size,text);
            /// odnaleziono proces, podaj dalej do odpowiednej funkcji
        }
        else{
            System.out.println("[SM] Sending failure!");
            System.out.println("[SM] Invalid receiver name!");
            return false;
            /// nie ma takiego procesu
        }
    }
    public boolean send_message(int receiverPID,int size, String text){
        Process p;
        if(Process_container.get_by_PID(receiverPID)!=null){
            p = Process_container.get_by_PID(receiverPID);
            /// odnaleziono proces
        }
        else{
            System.out.println("[SM] Sending failure!");
            System.out.println("[SM] Invalid receiver PID!");
            return false;
            /// nie ma takiego procesu
        }
        Message msg=new Message(this.PID,size,text);

        if(text.contains("$")){
            System.out.println("[SM] Sending failure!");
            System.out.println("[SM] Invalid character in text!");
            return false;
        }

        p.messages_queue.add(msg);
        /// dodaj wiadomość do kolejki procesu odbiorcy

        p.get_messages_semaphore().signal_s();
        /// wykonaj signal na semaforze odbiotcy aby pokazać że jest wiadomość do przeczytania

        System.out.println("[SM] Succesfully sent message!");
        System.out.println("[SM] PID:"+msg.get_sender_PID()+" Size:"+msg.get_size()+" Text:"+msg.get_text());
        /// wyswietlanie budowy wysyłanej wiadomości
        return true;
    }

    public boolean read_message(int size, int addres){

        this.messages_semaphore.wait_s(this.PID);
        /// operacja na semaforze (sprawdza czy są wiadomości do odebrania, jeżeli nie ma to robi czekanko)

        if(this.state==State.Waiting) {
            System.out.println("[RM] Reading failure!");
            System.out.println("[RM] There are no messages to read!");
            return false;
        }
        this.last_message=this.messages_queue.poll();
        /// wyciąganie wiadomości z kolejki z usunięciem do zmiennej PCB last_message

        String ram_msg = (char)last_message.get_sender_PID()+last_message.get_text()+'$';
        /// przykładowy zapis wiadomości o treści "bigos" pochodzącej od procesu o pid 12
        /// [char 12][b][i][g][o][s][$]

        for(int i=0; i<ram_msg.length();i++){
            if(addres+i>=128){
                System.out.println("[RM] Reading failure!");
                System.out.println("[RM] Reached end of memory!");
                return false;
            }
            else{
                //Message.write_to_ram(addres+i,ram_msg.charAt(i));
            }
        }
        /// pętla zapisująca wiadomość

        int written_pid=ram_msg.charAt(0);
        /// trzeba zrobić taką zmienną żeby sie dobrze wyświetlało

        System.out.println("[RM] Succesfully read message!");
        System.out.println("[RM] Sender: "+written_pid+" Text: "+ram_msg.substring(1,ram_msg.length()-1));
        ///wyświetlanie budowy wiadomości, identycznie powinna wyglądać w RAM'ie
        return true;
    }
}
