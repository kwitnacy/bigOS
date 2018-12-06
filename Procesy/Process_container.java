package Procesy;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Process_container {
    static private Map<Integer, Process> processes;
    static private Vector<Process> waiting_processes;
    static private int counter;

    static private Vector<String> taken_names;

    Process_container(){
        counter = 1;
        processes = new ConcurrentHashMap<Integer, Process>();
        taken_names = new Vector<>();
        waiting_processes = new Vector<>();

        taken_names.add("dumpy");
        processes.put(0, new Process("dumpy", "", 0, 0));
    }

    void create_process(String name, String file_name, int priority){
        if(taken_names.contains(name))
            System.out.println("Nie można utworzyć procesu - zajeta nazwa");
        else{
            System.out.println("Stworzenie nowe procesu o nazwie: " + name);
            Process temp = new Process(name, file_name, priority, counter);
            processes.put(counter, temp);
            counter = counter + 1;
            /*

                    Dodanie do procesora

             */
        }
    }

    public void show_all_processes(){
        System.out.println("+-----------------------------------------+");
        for(int i = 0 ; i < processes.size() ; i++) {
            Process temp = processes.get(i);
            if(temp.state != State.Terminated){
                temp.display_process();
                System.out.println("+-----------------------------------------+");
            }
        }
    }

    public void show_process_by_name(String name){
        for(int i = 0 ; i < processes.size() ; i++) {
            Process temp = processes.get(i);
            if(temp.state == State.Terminated) {
                System.out.println("Process: " + temp.name + " is terminated");
                break;
            }
            else if (temp.name.equals(name)) {
                System.out.println("+-----------------------------------------+");
                temp.display_process();
                System.out.println("+-----------------------------------------+");
                break;
            }
        }
    }

    public void show_process_by_PID(int PID){
        for(int i = 0 ; i < processes.size() ; i++) {
            Process temp = processes.get(i);

            if(temp.state == State.Terminated){
                System.out.println("Process: " + temp.name + " is terminated");
                break;
            }
            else if (temp.PID == PID) {
                System.out.println("+-----------------------------------------+");
                temp.display_process();
                System.out.println("+-----------------------------------------+");
                break;
            }
        }
    }

    public void set_mess(int idx, String s){
        /*processes.get(idx).get_mess(s);*/
    }

    static Process get_by_PID(int idx){
        return processes.get(idx);
    }

    public Process get_by_name(String name){
        for(Process temp : this.processes.values()){
            if(temp.name.equals(name))
                return temp;
        }

        return null;
    }

    static void delete(int PID){
        Process temp = get_by_PID(PID);
        System.out.println("usuwanie " + temp.get_name());
        processes.values().remove(temp);
    }

    static void add_to_waitting(int PID){
        Process temp = get_by_PID(PID);
        if(temp.state != State.Waiting)
            temp.state = State.Waiting;

        waiting_processes.add(temp);
    }

    void add_to_CPU(int PID){
        Process to_add = new Process();
        boolean flag = false;

        for (Process temp : waiting_processes){
            if(temp.PID == PID) {
                to_add = temp;
                to_add.state = State.Ready;
                flag = true;
                break;
            }
        }
        if(flag)
            System.out.println("temp PID, " + to_add.PID);
        else
            System.out.println("brak takeigo procesu czekajcego");

        /*

                Dodaj do CPU to add

         */

    }

    void disp_waiting(){
        System.out.println("Czekajace:");
        for (Process temp: waiting_processes) {
            temp.display_process();
        }
        System.out.println("---------------------------------------------------------");
    }

    int size(){
        return processes.size();
    }
}
