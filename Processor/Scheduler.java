package Processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import Procesy.Process;
import Procesy.Process_container;
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
			Memory.loadProgram(dummy.get_file_name(), dummy.get_PID());
			running = dummy;
			running.change_state(State.Running);
			schedule();
			System.out.println("[Processor]: Added dummy process.");
		}
		else
		{
			queuesPCB.get(toAdd.get_temp_priority()-1).add(toAdd);
			if(toAdd.get_base_priority() == toAdd.get_temp_priority())
			{
				System.out.println("[Processor]: Added to queue process with name: " + toAdd.get_name() + ", PID: " + toAdd.get_PID()
						+ ", priorities base and temporary: " + toAdd.get_base_priority() + " ; " + toAdd.get_temp_priority() + " and state: " + toAdd.get_state());
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

	public static void remove_by_id(int PID){
		if(running.get_PID() == PID){
			System.out.println("----------------------\"deleting\" running----------------------");
			running = dummy;
			running.change_state(State.Running);
			schedule();
		}
		else
		{
			for(Queue<Process> qq : queuesPCB)
			{
				Iterator<Process> iteratorkolejek = qq.iterator();
				while (iteratorkolejek.hasNext())
				{
					Process block = iteratorkolejek.next();
					if(block.get_PID() == PID)
					{
						System.out.println("[Processor]: Deleted from queue process with name: " + block.get_name() + ", PID: " + block.get_PID()
								+ ", priorities base and temporary: " + block.get_base_priority() + " ; " + block.get_temp_priority() + " and state: " + block.get_state());
						iteratorkolejek.remove();
					}
				}
			}
		}
	}

	public static void makeOlder() 										// postarzanie procesow, oraz inkrementacja licznika czekania procesow
	{
		if(running.get_base_priority() < running.get_temp_priority())
		{
			if(running.get_waiting_counter() > 0)
			{
				running.dec_waiting_counter();

			}
			else
			{
				System.out.println("[Processor]: Rejuvenated process Running with name: " + running.get_name() + ", PID: " + running.get_PID()
						+ ", and priorities base and temporary (after change): " + running.get_base_priority() + " ; " + (running.get_temp_priority()+1));
				running.dec_temp_priority();
				running.set_waiting_counter(3);

			}
		}
		for (Queue<Process> qq : queuesPCB)
		{
			Iterator<Process> iteratorkolejek = qq.iterator();
			while(iteratorkolejek.hasNext())
			{
				Process block = iteratorkolejek.next();
				if(block.get_waiting_counter() >= waitingLimit && block.get_temp_priority() < 15)
				{
					System.out.println("[Processor]: Oldened process with name: " + block.get_name() + ", PID: " + block.get_PID()
							+ ", and priorities base and temporary (after change): " + block.get_base_priority() + " ; " + (block.get_temp_priority()+1));
					block.inc_temp_priority();
					block.set_waiting_counter(-1);
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
		if(running.get_state() == State.Terminated || running.get_temp_priority() == 0) //zwykly przydzial procesora
		{
			for (int i = 14 ; i > -1 ; i--)
			{
				for (Process block : queuesPCB.get(i))
				{
					if(Memory.loadProgram(block.get_file_name(), block.get_PID()))
					{
						running = block;
						running.change_state(State.Running);
						queuesPCB.get(i).remove(block);
						System.out.println("[Processor]: Assigned processor to process with name: " + block.get_name() + ", PID: " + block.get_PID()
								+ ", and priorities (base and temporary): " + block.get_base_priority() + " ; " + block.get_temp_priority());

						if(dummy.get_state() != State.Ready)
							dummy.change_state(State.Ready);

						break;
					}
					else
					{
						System.out.println("[Procesor]: Deleted from queue process with name: "+block.get_name()+ " o PID: "+block.get_PID()+" which changed his state to Waiting.");
						int tmp_pid = block.get_PID();

						queuesPCB.get(i).remove(block);
						Memory.sem.wait_s(tmp_pid);
						Memory.sem.print_queue();
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
						if(Memory.loadProgram(block.get_file_name(), block.get_PID()))
						{
							System.out.println("[Procesor]: Process preemptioned for a process with name: " + block.get_name() + ", PID: " + block.get_PID()
									+ ", and priorities (base and temporary): " + block.get_base_priority() + " ; " + block.get_temp_priority());
							running.change_state(State.Ready);
							if(running.get_temp_priority() != 0){
								add(running);
							}
							running = block;
							running.change_state(State.Running);
							running.set_waiting_counter(2);
							queuesPCB.get(i).remove(block);

							if(dummy.get_state() != State.Ready)
								dummy.change_state(State.Ready);

							break;
						}
						else
						{
							System.out.println("[Procesor]: Deleted from queue process with name: "+block.get_name()+ " o PID: "+block.get_PID()+" which changed his state to Waiting.");
							int tmp_pid = block.get_PID();

							queuesPCB.get(i).remove(block);
							Memory.sem.wait_s(tmp_pid);
							Memory.sem.print_queue();
						}
					}
				}
			}
		}
		if(running.get_state() != State.Running || running.get_name().equals("dummy")) // sprawdzamy czy po przeleceniu kolejek jakis zostal� przydzielony
		{
			running = dummy;
			running.change_state(State.Running);
			System.out.println("[Processor]: Assigned processor to a Dummy process.");
		}

	}

	public static void showReadyProcesses() // wyswietlanie ready
	{
		System.out.println("[Processor]: Showing processes in ready state:");
		System.out.println("Name ; PID ; Base priority ; Temporary priority");
		int c=1;
		for(Queue<Process> qq : queuesPCB)
		{
			System.out.println("Queue "+c+":");
			c++;
			for(Process block : qq)
			{
				System.out.println(block.get_name() + " ; " + block.get_PID() + " ; " + block.get_base_priority() + " ; " + block.get_temp_priority());
			}
		}
	}

	public static void showRunning()										// wyswietlanie running
	{
		System.out.println("[Processor]: Currently running process is: (Name ; PID ; Base priority ; Temporary priority)");
		System.out.println(running.get_name() + " ; " + running.get_PID() + " ; " + running.get_base_priority() + " ; " + running.get_temp_priority());
	}

	public static void updateBase()
	{
		for(Queue<Process> qq : queuesPCB)
		{
			for(Process block : qq)
			{
				if(block.get_base() != Process_container.get_by_PID(block.get_PID()).get_base())
				{
					block.ser_base(Process_container.get_by_PID(block.get_PID()).get_base());
				}
			}
		}
	}
}
