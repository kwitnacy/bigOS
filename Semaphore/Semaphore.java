package Semaphore;

import Procesy.Process_container;
import Procesy.State;

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
        System.out.print("Kolejka semafora: ");
        for(Integer s : this.process_queue) {
            System.out.print(s);
            System.out.print(" ");
        }
        System.out.println();
    }

    public void wait_s(int pid){
        this.value--;
        System.out.println(value);
        if(this.value < 0){
            this.process_queue.offer(pid);
            System.out.println("Do kolejki semafora dodano proces o PID: " + pid);
            Process_container.get_by_PID(pid).change_state(State.Waiting);
        }
    }

    public void signal_s(){
        this.value++;
        if(this.value <= 0) {
            if (process_queue.peek() != null) {
                int pid = this.process_queue.poll();
                System.out.println("Z kolejki semafora usunieto proces o PID: " + pid);
                Process_container.get_by_PID(pid).change_state(State.Ready);
            }
        }
    }
}
