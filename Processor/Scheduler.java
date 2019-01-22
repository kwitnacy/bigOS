package Processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import Procesy.Process;
import Procesy.State;
import RAM.Memory;

public class Scheduler
{
	private static final int waitingLimit = 2;						// maskymalny czas oczekiwania (po tylu rozkazach postarzamy proces)
	private static List<Queue<Process>> queuesPCB; 						// lista kolejek PCB w stanie ready
	private static Process dummy;											// proces dummy o priorytecie 0
	public static Process running;									// aktualnie wykonywany proces
	
	public Scheduler(Process dummy)
	{
		Scheduler.dummy = dummy;					// inicjalizacja pustego procesu dummy
		
		queuesPCB = new ArrayList<Queue<Process>>();				// inicjalizacja pustych kolejek dla kazdedgo priorytetu
		
		for (int i=0 ; i<15 ; i++)
		{
			queuesPCB.add(new LinkedList<Process>());
		}
		
		running = dummy; 											// przypisanie procesorowi procesu dummy
	}
	
	public Scheduler(){
		queuesPCB = new ArrayList<Queue<Process>>();				// inicjalizacja pustych kolejek dla kazdego priorytetu

		for (int i=0 ; i<15 ; i++){
			queuesPCB.add(new LinkedList<Process>());
		}
	}
	
	static {
		queuesPCB = new ArrayList<Queue<Process>>();				// inicjalizacja pustych kolejek dla ka�dedgo priorytetu

		for (int i=0 ; i<15 ; i++){
			queuesPCB.add(new LinkedList<Process>());
		}
	}
	
	public static void add(Process toAdd) 									// dodawanie procesu do odpowiedniej kolejki
	{
		if(toAdd.get_name().equals("dummy")){
			dummy = toAdd;
			dummy.change_state(State.Running);
            running = dummy;
            schedule();
			System.out.println("Dodanie dummy");
		}
		else
		{
			queuesPCB.get(toAdd.get_temp_priority()-1).add(toAdd);
			if(toAdd.get_base_priority() == toAdd.get_temp_priority())
			{
				System.out.println("Procesor: Dodalem do kolejki proces o nazwie: " + toAdd.get_name() + ", PID: " + toAdd.get_PID()
				+ ", priorytetach bazowym i tymczasowym: " + toAdd.get_base_priority() + " ; " + toAdd.get_temp_priority() + " bedacym w stanie: " + toAdd.get_state());
			}
			else 														// nie wiem czy pisac ze dodalem proces do kolejki, skoro to reorganizacja
			{
	//			System.out.println("Procesor: Dodalem do kolejki proces o nazwie: " + toAdd.get_name() + ", PID: " + toAdd.get_PID()
	//			+ ", priorytetach bazowym i tymczasowym: " + toAdd.get_base_priority() + " ; " + toAdd.get_temp_priority() + " bedacym w stanie: " + toAdd.get_state());
			}
		}

		if(toAdd.get_temp_priority() > running.get_temp_priority() || running.get_state() == State.Terminated)
		{
			schedule();
		}
		return;
	}
	
	public static void remove() 											// usuwanie z kolejek procesow ktore nie sa ready
	{
		for(Queue<Process> qq : queuesPCB)
			{
				Iterator<Process> iteratorkolejek = qq.iterator();
				while (iteratorkolejek.hasNext())
					{
						Process block = iteratorkolejek.next();
						if(block.get_state() != State.Ready)
						{
							System.out.println("Procesor: Usuwam z kolejki proces o nazwie: " + block.get_name() + ", PID: " + block.get_PID() 
							+ ", priorytetach bazowym i tymczasowym: " + block.get_base_priority() + " ; " + block.get_temp_priority() + " bedacym w stanie: " + block.get_state());
							iteratorkolejek.remove();
						}
					}
			}
	}

	public static void remove_running(int PID){
		if(running.get_PID() == PID){
			System.out.println("----------------------\"usuawanie\" running----------------------");
			Memory.removeProgram();
			running = dummy;
			running.change_state(State.Running);
			schedule();
		}
	}
	
	public static void makeOlder() 										// postarzanie procesow, oraz inkrementacja licznika czekania procesow
	{
		for (Queue<Process> qq : queuesPCB)
		{
			Iterator<Process> iteratorkolejek = qq.iterator();
			while(iteratorkolejek.hasNext())
			{
				Process block = iteratorkolejek.next();
				if(block.get_waiting_counter() > waitingLimit && block.get_temp_priority() < 15) 
				{
					System.out.println("Procesor: Postarzylem proces o nazwie: " + block.get_name() + ", PID: " + block.get_PID() 
					+ ", oraz z priorytetami bazowym i tymczasowym (po zmianie): " + block.get_base_priority() + " ; " + (block.get_temp_priority()+1));
					block.inc_temp_priority();
					block.set_waiting_counter(0);
					qq.remove(block);
					add(block);
				}
				else if(block.get_waiting_counter() <= waitingLimit && block.get_temp_priority() < 15)
				{
					block.inc_waiting_counter();
				}

			}
		}
		schedule();
		return;
	}
	
	public static void schedule() 											// planista
	{
		Process temp = running;
		if(running.get_state() == State.Terminated || running.get_temp_priority() == 0) //zwykly przydzial procesora
		{
			if(running.get_state() == State.Terminated)
			{
				Memory.removeProgram();
			}
			for (int i = 14 ; i > -1 ; i--)
			{
				for (Process block : queuesPCB.get(i))
				{
					running = block;
						if(Memory.loadProgram())
						{
							running.change_state(State.Running);
							queuesPCB.get(i).remove(block);
							System.out.println("Procesor: Przydzielilem procesor do procesu o nazwie: " + block.get_name() + ", PID: " + block.get_PID() 
							+ ", oraz z priorytetami bazowym i tymczasowym: " + block.get_base_priority() + " ; " + block.get_temp_priority());

							if(dummy.get_state() != State.Ready)
								dummy.change_state(State.Ready);
							
							break;
						}
						else
						{
							//ZMIENIC NA WAITING I USUNAC OD SIEBIE - CHYBA
						}
				}
				if(running.get_state() == State.Running && running.get_base_priority() != 0)
					break;
			}			
		}
		else //wywlaszczanie
		{
			for (int i = 14 ; i >= running.get_temp_priority() ; i--)
			{
				for (Process block : queuesPCB.get(i))
				{
					if(block.get_temp_priority() > running.get_temp_priority())
					{
						running = block;
						if(Memory.loadProgram())
						{
							System.out.println("Procesor: Wywlaszczylem procesor dla procesu o nazwie: " + block.get_name() + ", PID: " + block.get_PID() 
							+ ", oraz z priorytetami bazowym i tymczasowym: " + block.get_base_priority() + " ; " + block.get_temp_priority());
							temp.change_state(State.Ready);
							if(running.get_temp_priority() != 0){
								add(temp); //skoro dodajemy running do kolejki procesow gotowych to czy jej liczniki zostaly zapisane?
							}
							running.change_state(State.Running);
							queuesPCB.get(i).remove(block);

							if(dummy.get_state() != State.Ready)
								dummy.change_state(State.Ready);

							break;
						}
						else
						{
							//ZMIENIC NA WAITING I USUNAC OD SIEBIE - CHYBA
						}
					}
				}
			}
		}
		if(running.get_state() == State.Waiting || running.get_name().equals("dummy")) // sprawdzamy czy po przeleceniu kolejek jakis zostal� przydzielony
		{
			if(temp.get_state() != State.Terminated)
			{
				running = temp;
			}
			else
			{
				running = dummy;
				running.change_state(State.Running);
				System.out.println("Procesor: Przydzielilem procesor do procesu Dummy");
			}
		}

	}
	
	public static void showReadyProcesses() // wyswietlanie ready
	{
		System.out.println("Procesor: Procesy w stanie gotowosci to:");
		System.out.println("Nazwa ; PID ; Priorytet bazowy ; Priorytet Tymczasowy");
		int c=1;
		for(Queue<Process> qq : queuesPCB)
		{
			System.out.println("Kolejka nr. "+c+":");
			c++;
			for(Process block : qq)
			{
				System.out.println(block.get_name() + " ; " + block.get_PID() + " ; " + block.get_base_priority() + " ; " + block.get_temp_priority());
			}
		}
	}

	public static void showRunning()										// wyswietlanie running
	{
		System.out.println("Procesor: Aktualnie wykonywany proces to: (Nazwa ; PID ; Priorytet bazowy ; Priorytet tymczasowy)");
		System.out.println(running.get_name() + " ; " + running.get_PID() + " ; " + running.get_base_priority() + " ; " + running.get_temp_priority());
	}
}
