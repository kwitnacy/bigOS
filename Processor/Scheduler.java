package Processor;

import java.util.*;
//import java.util.PriorityQueue;

import Procesy.Process;
import Procesy.State;

public class Scheduler{
	private static final int waitingLimit = 2;						// maskymalny czas oczekiwania (po tylu rozkazach postarzamy procses
	private static List<Queue<Process>> queuesPCB; 						// lista kolejek PCB w stanie ready
	private static Process dummy;											// proces dummy o priorytecie 0
	public static Process running;									// aktualnie wykonywany proces

    public static int counter;

	public Scheduler(Process dummy){
		Scheduler.dummy = dummy;					// inicjalizacja pustego procesu dummy

		queuesPCB = new ArrayList<Queue<Process>>();				// inicjalizacja pustych kolejek dla ka�dedgo priorytetu

		for (int i=0 ; i<15 ; i++)
		{
			queuesPCB.add(new LinkedList<Process>());
		}

		running = dummy;

		counter = 0;
	}

	public Scheduler(){
		queuesPCB = new ArrayList<Queue<Process>>();				// inicjalizacja pustych kolejek dla ka�dedgo priorytetu

		for (int i=0 ; i<15 ; i++){
			queuesPCB.add(new LinkedList<Process>());
		}

		counter = 0;
	}
	
	public static void add(Process toAdd){
		if(toAdd.get_name().equals("dummy")){
			dummy = toAdd;
			dummy.change_state(State.Running);
            running = dummy;
            schedule();
			System.out.println("Dodanie dummy");
		}
		else {
            queuesPCB.get(toAdd.get_temp_priority() - 1).add(toAdd);
            counter++;
            if (toAdd.get_base_priority() == toAdd.get_temp_priority()) {
                System.out.println("Procesor: Dodalem do kolejki proces o nazwie: " + toAdd.get_name() + ", PID: " + toAdd.get_PID()
                        + ", priorytetach bazowym i tymczasowym: " + toAdd.get_base_priority() + " ; " + toAdd.get_temp_priority() + " bedacym w stanie: " + toAdd.get_state());
            }

            if (toAdd.get_temp_priority() > running.get_temp_priority() || running.get_state() == State.Terminated) {
                schedule();
            }
        }
	}
	
	public static void remove(){											// usuwanie z kolejek proces�w kt�re nie s� ready
		for(Queue<Process> qq : queuesPCB){
			Iterator<Process> iteratorkolejek = qq.iterator();
			while (iteratorkolejek.hasNext()){
				Process block = iteratorkolejek.next();
				if(block.get_state() != State.Ready){
/***/				System.out.println("usuwanie");
					System.out.println("Procesor: Usuwam z kolejki proces o nazwie: " + block.get_name() + ", PID: " + block.get_PID()
							+ ", priorytetach bazowym i tymczasowym: " + block.get_base_priority() + " ; " + block.get_temp_priority() + " bedacym w stanie: " + block.get_state());
					iteratorkolejek.remove();
				}
			}
		}
/***/	System.out.println("usuwanie");
	}
	
	public static void makeOlder(){ // postarzanie proces�w, oraz inkrementacja licznika czekania proces�w
		for (Queue<Process> qq : queuesPCB){
			for (Process block : qq){
				if(block.get_waiting_counter() > waitingLimit && block.get_temp_priority() < 15){
					System.out.println("Procesor: Postarzylem proces o nazwie: " + block.get_name() + ", PID: " + block.get_PID()
					+ ", oraz z priorytetami bazowym i tymczasowym (po zmianie): " + block.get_base_priority() + " ; " + block.get_temp_priority()+1);
					block.inc_temp_priority(); // chyba trzeba zmieni� na settera
					block.set_waiting_counter(0); // jak wy�ej
					qq.remove(block);
					add(block);
				}
				else if(block.get_waiting_counter() <= waitingLimit && block.get_temp_priority() < 15){
					block.inc_waiting_counter(); //setter od Piotrka
				}
			}
		}
		schedule();
	}

    public static void schedule() 											// planista
    {
        if(running.get_state() == State.Terminated || running.get_temp_priority() == 0) //zwykły przydział procesora
        {
            for (int i = 14 ; i > -1 ; i--)
            {
                for (Process block : queuesPCB.get(i))
                {
                    running = block;
                    running.change_state(State.Running);
                    queuesPCB.get(i).remove(block);
                    System.out.println("Procesor: Przydzielilem procesor do procesu o nazwie: " + block.get_name() + ", PID: " + block.get_PID()
                            + ", oraz z priorytetami bazowym i tymczasowym: " + block.get_base_priority() + " ; " + block.get_temp_priority());
                    break; // dodać ifa żeby proces który się nie skończył wrócił
                }
            }
        }
        else //wywłaszczanie
        {
            for (int i = 14 ; i > running.get_temp_priority() ; i--)
            {
                for (Process block : queuesPCB.get(i))
                {
                    if(block.get_temp_priority() > running.get_temp_priority())
                    {
                        System.out.println("Procesor: Wywlaszczylem procesor dla procesu o nazwie: " + block.get_name() + ", PID: " + block.get_PID()
                                + ", oraz z priorytetami bazowym i tymczasowym: " + block.get_base_priority() + " ; " + block.get_temp_priority());
						 running.change_state(State.Ready);
                        if(running.get_temp_priority() != 0)
                        {
                            add(running); //skoro dodajemy running do kolejki procesów gotowych to czy jej liczniki zostały zapisane?
                        }
                        running = block;
                        running.change_state(State.Running);
                        queuesPCB.get(i).remove(block);
                        break;
                    }
                }
            }
        }
        if(running.get_state() == State.Terminated) // sprawdzamy czy po przeleceniu kolejek jakiś został przydzielony
        {
            running = dummy;
            running.change_state(State.Running);
            System.out.println("Procesor: Przydzielilem procesor do procesu Dummy");
        }
        return;
    }

	public void showReadyProcesses(){
		System.out.println("Procesor: Procesy w stanie gotowosci to:");
		System.out.println("Nazwa ; PID ; Priorytet bazowy ; Priorytet Tymczasowy");
		for(Queue<Process> qq : queuesPCB)
		{
			for(Process block : qq)
			{
				System.out.println(block.get_name() + " ; " + block.get_PID() + " ; " + block.get_base_priority() + " ; " + block.get_temp_priority());
			}
		}
	}

	public void showRunning(){
		System.out.println("Procesor: Aktualnie wykonywany proces to: (Nazwa ; PID ; Priorytet bazowy ; Priorytet tymczasowy)");
		System.out.println(running.get_name() + " ; " + running.get_PID() + " ; " + running.get_base_priority() + " ; " + running.get_temp_priority());
	}
}
