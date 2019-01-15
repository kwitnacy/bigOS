package Procesy;

import Processor.Scheduler;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Process_container {
    static private Map<Integer, Process> processes;                                 //  Wszystkie procesy (Ready,
                                                                                    //Running, Waitting)

    static private Map<String, Integer> names;                                      //  Jaki PID przypisany jest do nazwy

    static public int counter;                                                      //  Licznik - zlicza ile powstalo
                                                                                    //procesow; nadawanie PID

    static private Vector<String> taken_names;                                      //  wektor przechowuje wykorzystane
                                                                                    //nazwy procesow

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Process_container(){
        counter = 1;
        processes = new ConcurrentHashMap<Integer, Process>();
        names = new ConcurrentHashMap<String, Integer>();

        taken_names = new Vector<>();
        taken_names.add("dummy");
        Process temp = new Process("dummy", "d1", 0, 0);
        processes.put(0, temp);
        Scheduler.add(temp);
    }

    public static void create_process(String name, String file_name, int priority){
        if (priority > 15 || priority < 1){
            System.out.println("Process Manager: Wrong priority");
            return;
        }

        if (taken_names.contains(name)){
            System.out.println("Process Manager: Name taken");
            return;
        }

        System.out.println("Stworzenie nowe procesu o nazwie: " + name);
        Process temp = new Process(name, file_name, priority, counter);
        processes.put(counter, temp);
        names.put(name, counter);
        taken_names.add(name);
        counter = counter + 1;

        Scheduler.add(temp);
    }

    public static void create_process(String name, String file_name, int priority, int limit){
        if (priority > 15 || priority < 1){
            System.out.println("Process Manager: Wrong priority");
            return;
        }

        if (taken_names.contains(name)){
            System.out.println("Process Manager: Name taken");
            return;
        }

        System.out.println("Stworzenie nowe procesu o nazwie: " + name);
        Process temp = new Process(name, file_name, priority, counter, limit);
        processes.put(counter, temp);
        names.put(name, counter);
        taken_names.add(name);
        counter = counter + 1;

        Scheduler.add(temp);
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

    public static Process get_by_PID(int idx){
        return processes.get(idx);
    }

    public static Process get_by_name(String name){
        if(names.containsValue(name))
            return get_by_PID(names.get(name));
        else
            System.out.println("Process: ERROR no process named: " + name);

        return null;
    }

    public static void delete(int PID){
        Process temp = get_by_PID(PID);
        System.out.println("usuwanie " + temp.get_name());
        processes.values().remove(temp);
    }

    public static void add_to_CPU(int PID){
        Scheduler.add(get_by_PID(PID));
    }

    int size(){
        return processes.size();
    }
}
