package Procesy;

import Processor.Scheduler;
import RAM.Memory;

import java.io.File;
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
    static private int size;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Process_container(){
        size = counter = 1;
        processes = new ConcurrentHashMap<Integer, Process>();
        names = new ConcurrentHashMap<String, Integer>();

        taken_names = new Vector<>();
        taken_names.add("dummy");
        Process temp = new Process("dummy", "p0", 0, 0);
        processes.put(0, temp);
        Scheduler.add(temp);
    }

    static {
        size = counter = 1;
        processes = new ConcurrentHashMap<Integer, Process>();
        names = new ConcurrentHashMap<String, Integer>();

        taken_names = new Vector<>();
        taken_names.add("dummy");
        Process temp = new Process("dummy", "p0", 0, 0);
        processes.put(0, temp);
        Scheduler.add(temp);
    }

    public static void create_process(String name, String file_name, int priority){
        if (priority > 15 || priority < 1){
            System.out.println("[Process_Manager]: Couldn't create process " + name + ". Wrong priority.");
            return;
        }

        if (taken_names.contains(name)){
            System.out.println("[Process_Manager]: Couldn't create process " + name + ". Wrong name.");
            return;
        }

        String temp_file_name = "src/Interpreter/" + file_name + ".txt";
        File file = new File(temp_file_name);
        if(!file.exists()) {
            System.out.println("[Process_Manager]: Couldn't create process " + name + ". File doesn't exist.");
            return;
        }

        System.out.println("[Process_Manager]: Created process " + name + ".");
        Process temp = new Process(name, file_name, priority, counter);

        processes.put(counter, temp);
        names.put(name, counter);
        taken_names.add(name);
        counter = counter + 1;
        size = counter;

        Scheduler.add(temp);
    }

    public static void create_process(String name, String file_name, int priority, int limit){
        if (priority > 15 || priority < 1){
            System.out.println("[Process_Manager]: Couldn't create process " + name + ". Wrong priority.");
            return;
        }

        if (taken_names.contains(name)){
            System.out.println("[Process_Manager]: Couldn't create process " + name + ". Wrong name.");
            return;
        }

        file_name="src/Interpreter/" + file_name + ".txt";
        File file = new File(file_name);
        if(!file.exists()) {
            System.out.println("[Process_Manager]: Couldn't create process " + name + ". File doesn't exist.");
            return;
        }

        System.out.println("[Process_Manager]: Created process " + name + ".");
        Process temp = new Process(name, file_name, priority, counter, limit);
        processes.put(counter, temp);
        names.put(name, counter);
        taken_names.add(name);
        counter = counter + 1;
        size = counter;

        Scheduler.add(temp);
    }

    public static void show_all_processes(){
        System.out.println("Mam tyle procesow: " + processes.size());
        processes.forEach((Integer, Process) -> Process.display_process());
    }

    public void show_process_by_name(String name){
        try {
            get_by_name(name).display_process();
        }
        catch (NullPointerException e){
            System.out.println("[Process_Manager]: No process called " + name + " to display.");
        }
    }

    public void show_process_by_PID(int PID){
        try {
            get_by_PID(PID).display_process();
        }
        catch (NullPointerException e){
            System.out.println("[Process_Manager]: No process with PID " + PID + " to display.");
        }
    }

    public static Process get_by_PID(int idx){
        return processes.get(idx);
    }

    public static Process get_by_name(String name){
        if(taken_names.contains(name)) {
            System.out.println("hejo");
            return processes.get(names.get(name));
        }
        else
            System.out.println("[Process_Manager]: No process called: " + name + ".");

        return null;
    }

    public static void delete(int PID){
        System.out.println("[Process_Manager]: Trying to delete process with PID " + PID + ".");
        Process temp = get_by_PID(PID);
        if(temp != null) {
            Memory.removeProgram(PID);
            processes.remove(PID);
            taken_names.remove(temp.get_name());
            names.remove(temp.name);
            System.out.println("[Process_Manager]: Deleted process called " + temp.name + ".");
            Scheduler.remove();
            Scheduler.remove_running(PID);
        }
        else
            System.out.println("[Process_Manager]: Deleting cancelled NullPointer");
    }

    public static void delete(String name){
        System.out.println("[Process_Manager]: Trying to delete process called " + name + ".");
        Process temp = get_by_name(name);
        try{
            Memory.removeProgram(temp.PID);
            names.remove(temp.name);
            taken_names.remove(temp.name);
            processes.remove(temp.PID);
            System.out.println("[Process_Manager]: Deleted process called " + temp.name + ".");
            Scheduler.remove();
            Scheduler.remove_running(temp.PID);

        }
        catch (Exception e) {
            System.out.println("[Process_Manager]: Deleting cancelled NullPointer");
        }
    }

    public static void add_to_CPU(int PID){
        Scheduler.add(get_by_PID(PID));
    }

    public static void display_proces(int PID){
        processes.get(PID).display_process();
    }

    public static void display_proces(String name) {
        processes.get(names.get(name)).display_process();
    }

    public static int size(){
        for(String s : taken_names) {
            System.out.println(s);
        }
        return 0;
    }

    public int get_size(){
        return size;
    }
}
