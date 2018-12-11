package Procesy;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import nie_procesy.Cos;

public class Process_container {
    static private Map<Integer, Process> processes;                                 //  Wszystkie procesy (Ready,
                                                                                    //Running, Waitting)

    static private int counter;                                                     //  Licznik - zlicza ile powstalo
                                                                                    //procesow; nadawanie PID

    static private Vector<String> taken_names;                                      //  wektor przechowuje wykorzystane
                                                                                    //nazwy procesow

    static private Vector<Process> procesor;

    public Process_container(Vector<Process> procesor_x){
        counter = 1;
        processes = new ConcurrentHashMap<Integer, Process>();
        taken_names = new Vector<>();
        procesor = procesor_x;

        taken_names.add("dumpy");
        processes.put(0, new Process("dumpy", "", 0, 0));
    }

    public Process_container(){
        counter = 1;
        processes = new ConcurrentHashMap<Integer, Process>();
        procesor = new Vector<>(); 
        taken_names = new Vector<>();

        taken_names.add("dumpy");
        processes.put(0, new Process("dumpy", "", 0, 0));
    }

    public static void create_process(String name, String file_name, int priority){
        try {
            if (priority > 15 || priority < 1)
                throw new Exception("zly piorytet");

            if (taken_names.contains(name))
                System.out.println("Nie można utworzyć procesu - zajeta nazwa");
            else {
                System.out.println("Stworzenie nowe procesu o nazwie: " + name);
                Process temp = new Process(name, file_name, priority, counter);
                processes.put(counter, temp);
                counter = counter + 1;

                procesor.add(temp);
                /*

                        Dodanie do procesora

                 */
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
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

    public static Process get_by_PID(int idx){
        return processes.get(idx);
    }

    public Process get_by_name(String name){
        for(Process temp : this.processes.values()){
            if(temp.name.equals(name))
                return temp;
        }

        return null;
    }

    public static void delete(int PID){
        Cos.delete(PID);
        Process temp = get_by_PID(PID);
        System.out.println("usuwanie " + temp.get_name());
        processes.values().remove(temp);
    }

    void add_to_CPU(int PID){
        Process to_add = new Process();
        boolean flag = false;
    }


    int size(){
        return processes.size();
    }
}
