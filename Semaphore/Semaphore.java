package Semaphore;

import Procesy.Process_container;
import Procesy.State;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class Semaphore{
    private Queue<Integer> process_queue;
    private int value;

    public Semaphore(int val){
        this.value = val;
        this.process_queue = new Queue<Integer>() {
            @Override
            public boolean add(Integer integer) {
                return false;
            }

            @Override
            public boolean offer(Integer integer) {
                return false;
            }

            @Override
            public Integer remove() {
                return null;
            }

            @Override
            public Integer poll() {
                return null;
            }

            @Override
            public Integer element() {
                return null;
            }

            @Override
            public Integer peek() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<Integer> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends Integer> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }
        };
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
