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
        for(Integer s : this.process_queue) {
            System.out.println(s);
        }
    }

    public void wait_s(int pid){
        this.value--;
        if(this.value < 0){
            this.process_queue.offer(pid);
            Process_container.get_by_PID(pid).change_state(State.Waiting);
        }
    }

    public void signal_s(){
        this.value++;
        if(this.value <= 0){
            int pid = this.process_queue.poll();
            Process_container.get_by_PID(pid).change_state(State.Ready);
        }
    }
}
