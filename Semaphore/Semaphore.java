import java.util.Queue;

public class Semaphore{
    Queue<Process> queue;
    int value;

    Semaphore(int val){
        this.value = val;
    }

    public void wait_s(){
        this.value--;
        if(this.value < 0){
            //add to this.process_queue
            //block();
        }
    }

    public void signal_s(){
        this.value++;
        if(this.value <= 0){
            Process P = process_queue.poll();
            //wakeup(P);
        }
    }
}
