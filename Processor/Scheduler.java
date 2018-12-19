package projekt_so;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Scheduler
{
	private static final int waitingLimit = 2;						// maskymalny czas oczekiwania (po tylu rozkazach postarzamy procses
	private static List<Queue<Process>> queuesPCB; 						// lista kolejek PCB w stanie ready
	private static Process dummy;											// proces dummy o priorytecie 0
	public static Process running;									// aktualnie wykonywany proces
	
	public Scheduler(Process dummy) 												// sprawdziæ czy wszystko dzia³a
	{
		Scheduler.dummy = dummy;					// inicjalizacja pustego procesu dummy
		
		queuesPCB = new ArrayList<Queue<Process>>();				// inicjalizacja pustych kolejek dla ka¿dedgo priorytetu
		Queue<Process> temp = new LinkedList<Process>();
		
		for (int i=0 ; i<15 ; i++)
		{
			queuesPCB.add(temp);
		}
		
		running = dummy; 											// przypisanie procesorowi procesu dummy
	}
	
	public static void add(Process toAdd) 									// dodawanie procesu do odpowiedniej kolejki
	{
		queuesPCB.get(toAdd.get_temp_priority()-1).add(toAdd);
		if(toAdd.get_base_priority() == toAdd.get_temp_priority())
		{
			System.out.println("Procesor: Dodalem do kolejki proces o nazwie: " + toAdd.get_name() + ", PID: " + toAdd.get_PID() 
			+ ", priorytetach bazowym i tymczasowym: " + toAdd.get_base_priority() + " ; " + toAdd.get_temp_priority() + " bedacym w stanie: " + toAdd.get_state());
		}
		else 														// nie wiem czy pisaæ ¿e doda³em proces do kolejki, skoro to reorganizacja
		{
//			System.out.println("Procesor: Dodalem do kolejki proces o nazwie: " + toAdd.get_name() + ", PID: " + toAdd.get_PID() 
//			+ ", priorytetach bazowym i tymczasowym: " + toAdd.get_base_priority() + " ; " + toAdd.get_temp_priority() + " bedacym w stanie: " + toAdd.get_state());
		}
		if(toAdd.get_temp_priority() > running.get_temp_priority() || running.get_state() == State.Terminated)
		{
			schedule();
		}
		return;
	}
	
	public static void remove() 											// usuwanie z kolejek procesów które nie s¹ ready
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
	
	public static void makeOlder() 										// postarzanie procesów, oraz inkrementacja licznika czekania procesów
	{
		for (Queue<Process> qq : queuesPCB)
		{
			for (Process block : qq)
			{
				if(block.waiting_counter > waitingLimit && block.get_temp_priority() < 15) // zamieniæ potem na funkcje get_waiting_counter
				{
					System.out.println("Procesor: Postarzylem proces o nazwie: " + block.get_name() + ", PID: " + block.get_PID() 
					+ ", oraz z priorytetami bazowym i tymczasowym (po zmianie): " + block.get_base_priority() + " ; " + block.get_temp_priority()+1);
					block.temp_priority++; // chyba trzeba zmieniæ na settera
					block.waiting_counter = 0; // jak wy¿ej
					qq.remove(block);
					add(block);
				}
				else if(block.waiting_counter <= waitingLimit && block.get_temp_priority() < 15)
				{
					block.waiting_counter++; //setter od Piotrka
				}
			}
		}
		schedule();
		return;
	}
	
	public static void schedule() 											// planista
	{
		if(running.get_state() == State.Terminated || running.get_temp_priority() == 0) //zwyk³y przydzia³ procesora
		{
			for (int i = 14 ; i > -1 ; i--)
			{
				for (Process block : queuesPCB.get(i))
				{
						running = block;
						running.state = State.Running;
						queuesPCB.get(i).remove(block);
						System.out.println("Procesor: Przydzielilem procesor do procesu o nazwie: " + block.get_name() + ", PID: " + block.get_PID() 
						+ ", oraz z priorytetami bazowym i tymczasowym: " + block.get_base_priority() + " ; " + block.get_temp_priority());
						break;
				}
			}			
		}
		else //wyw³aszczanie
		{
			for (int i = 14 ; i > running.get_temp_priority() ; i--)
			{
				for (Process block : queuesPCB.get(i))
				{
					if(block.get_temp_priority() > running.get_temp_priority())
					{
						System.out.println("Procesor: Wywlaszczylem procesor dla procesu o nazwie: " + block.get_name() + ", PID: " + block.get_PID() 
						+ ", oraz z priorytetami bazowym i tymczasowym: " + block.get_base_priority() + " ; " + block.get_temp_priority());
						running.state = State.Ready;
						if(running.get_temp_priority() != 0)
						{
							add(running); //skoro dodajemy running do kolejki procesów gotowych to czy jej liczniki zosta³y zapisane?
						}
						running = block;
						running.state = State.Running;
						queuesPCB.get(i).remove(block);
						break;
					}
				}
			}
		}
		if(running.get_state() == State.Terminated) // sprawdzamy czy po przeleceniu kolejek jakiœ zosta³ przydzielony
		{
			running = dummy;
			running.state = State.Running;
			System.out.println("Procesor: Przydzielilem procesor do procesu Dummy");
		}
		return;
	}
	
	public void showReadyProcesses()								// wyswietlanie procesow gotowych - coœ tu siê ewidentnie popsu³o, musze naprawiæ :<
	{
		System.out.println("Procesor: Procesy w stanie gotowosci to:");
		System.out.println("Nazwa ; PID ; Priorytet bazowy ; Priorytet Tymczasowy");
		for(Queue<Process> qq : queuesPCB)
		{
			for(Process block : qq)
			{
				System.out.println(block.get_name() + " ; " + block.get_PID() + " ; " + block.get_base_priority() + " ; " + block.get_temp_priority());
			}
			break;
		}
	}

	public void showRunning()										// wyswietlanie running
	{
		System.out.println("Procesor: Aktualnie wykonywany proces to: (Nazwa ; PID ; Priorytet bazowy ; Priorytet tymczasowy)");
		System.out.println(running.get_name() + " ; " + running.get_PID() + " ; " + running.get_base_priority() + " ; " + running.get_temp_priority());
	}
}
