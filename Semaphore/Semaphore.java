package Semaphore;

import Interpreter.Interpreter;
import Procesy.Process_container;
import Procesy.State;
import RAM.Memory;

import java.util.Queue;
import java.util.ArrayDeque;


public class Semaphore{
    private Queue<Integer> process_queue;

    private int value;

    public Semaphore(int val){
        this.value = val;
        this.process_queue = new ArrayDeque<>();
    }

    public int getValue() {
        return value;
    }

    public void print_queue(){
        System.out.print("[Semaphore] Semaphore queue: ");
        for(Integer s : this.process_queue) {
            System.out.print(s);
            System.out.print(" ");
        }
        System.out.println();
    }

    public void wait_s(int pid){
        System.out.println("WYKONUJE WEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEJT");
        this.value--;
        System.out.println(value);
        if(this.value < 0){
            this.process_queue.offer(pid);
            System.out.println("[Semaphore] Process " + Process_container.get_by_PID(pid).get_name() + " added to semaphore queue");

            System.out.println(Process_container.get_by_PID(pid).get_program_counter() + " -------------------");
            Process_container.get_by_PID(pid).set_program_counter(Process_container.get_by_PID(pid).get_program_counter() - Interpreter.get_size_rozkaz() - 1);

            Process_container.get_by_PID(pid).change_state(State.Waiting);
        }
    }

    public void signal_s(){
        System.out.println("JASDKSDVASDJNVLKSDVLKASNVDLKNlknflkewdmlkDNSVLNDVLKNLJNK");
        this.value++;
        System.out.println(this.value);
        if(this.value <= 0) {
            if (process_queue.peek() != null) {
                int pid = this.process_queue.poll();
                System.out.println("[Semaphore] Process " + Process_container.get_by_PID(pid).get_name() + " removed from semaphore queue");
                Process_container.get_by_PID(pid).change_state(State.Ready);
            }
        }
    }
}
