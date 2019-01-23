package RAM;
import Processor.Scheduler;
import Procesy.Process_container;
import Semaphore.Semaphore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class Memory {

    public static char[] memory = new char[256];
    public static Semaphore sem = new Semaphore(0);

    private static Map<Integer,Integer> allocatedPartitions = new HashMap<>();
    private static Map<Integer,Integer> freePartitions =  new HashMap<>();

    public static void memoryInit(){
        Arrays.fill(memory,' ');
        freePartitions.put(0,256);
    }

    public static Boolean loadProgram(String fileName,Integer PID){
        String temp="src/Interpreter/" + fileName + ".txt";
        if(temp!=fileName)
            fileName= "src/Interpreter/" + fileName + ".txt";
        //System.out.println("sciezka: " + fileName);
        Integer size=0,value=0;
        File file = new File(fileName);
        String program="";
        try{
            Scanner skaner= new Scanner(file);
            while(skaner.hasNextLine()){
                program+=skaner.nextLine() + " ";
            }
            program= program.substring(0,program.length()-1);
            size = program.length();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        int tmp=Process_container.get_by_PID(PID).get_base(),licznik = 0;
        String check="";
        for(int i=tmp;i<tmp+size;i++){
            check+=memory[i];
        }
        //System.out.println(program);
        //System.out.println(check);
        if(program.equals(check)){
          //  System.out.println("rowne");
            return true;
        }

        for(int i=0;i<256;i++) {
            if(freePartitions.get(i)!=null)
                value = freePartitions.get(i);
            if(value>=size+10){
                Process_container.get_by_PID(PID).ser_base(i);
                writeMemory(program,PID);
                return true;
            }
        }
        Integer free=0;
        for(int j=0;j<256;j++){
            if(freePartitions.get(j)!=null){
                free+=freePartitions.get(j);
            }
        }
        if(free>=size+10) {
            move();
            loadProgram(fileName,PID);
        }
        System.out.println("[RAM]: there is not enough space to load the program");
        return false;
    }
    public static void writeMemory(char value,Integer address){
        Integer base = Scheduler.running.get_base();
        memory[base+address]=value;
    }
    private static void writeMemory(String fileName,Integer PID){
        /*File file = new File(fileName);

        Integer next=0;
        try{
            Scanner skaner= new Scanner(file);
            while(skaner.hasNextLine()){
                String line= skaner.nextLine();

                int index=0;
                for(int i=base + next ;i< base +next + line.length();i++){
                    memory[i]=line.charAt(index);
                    index++;
                }next+=line.length();
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }*/
        Integer next=0;
        int index=0;
        Integer base = Process_container.get_by_PID(PID).get_base();
        for(int i=base ;i< base + fileName.length() ;i++){
            memory[i]=fileName.charAt(index);
            index++;
        }

        allocatedPartitions.put(base,fileName.length()+10);
       // Scheduler.running.ser_limit(fileName.length()+10);
        Process_container.get_by_PID(PID).ser_limit(fileName.length()+10);
        System.out.println("[RAM]: Program has been put in RAM at [" + base + "," + (base+fileName.length()+9) + "]");
        Integer tmp = freePartitions.get(base);
        freePartitions.remove(base);
        Integer limit = tmp;
        freePartitions.put(base+fileName.length()+10,tmp-fileName.length()-10);
    }
    public static Character readMemory(Integer address){
        if(address<0||address>256){
            System.out.println("[RAM]: Invalid address.");
            return null;
        }
        Integer base= Scheduler.running.get_base();
        return memory[base+address];
    }

    private static void mergeMaps(Map<Integer,Integer> map){
        Integer preBase=0,preLimit=0,
                base=0,limit;
        for(int i=0;i<256;i++){
            limit = map.get(i);
            if(limit!=null) {
                if(i==(preBase+preLimit)) {
                    freePartitions.remove(preBase);
                    freePartitions.remove(i);
                    freePartitions.put(preBase,i+limit-preBase);
                }
                preLimit=limit;
                preBase=i;
            }
        }
    }
    public static void removeProgram(int PID) {
        Integer base = Process_container.get_by_PID(PID).get_base();
//        Integer base= Scheduler.running.get_base();
        System.out.println(base);
        for(int i=base;i<allocatedPartitions.get(base) + base; i++){
            memory[i]=' ';
        }
        Integer tmp = allocatedPartitions.get(base);
        allocatedPartitions.remove(base);
        System.out.println("[RAM]: program has been deleted.");
        freePartitions.put(base,tmp);
        mergeMaps(freePartitions);
        sem.signal_s();
    }

    public static void printMemory(){
        for(int i=0;i<64;i++){
            System.out.println(i + ": " + memory[i] + "\t\t\t"
                    + (i+64) + ": " + memory[i+64]+ "\t\t\t"
                    + (i+128) + ": " + memory[i+128]+ "\t\t\t"
                    + (i+192) + ": " + memory[i+192]);
        }
        int zajete = allocatedPartitions.values().stream().reduce(0, Integer::sum);
        System.out.println("Free space: " + (256-zajete) + "\nAllocated space: " + zajete);
        System.out.println("Allocated partitions: " + allocatedPartitions.entrySet());
        System.out.println("Free partitions: " + freePartitions.entrySet());
    }

    public static void move(){
        System.out.println("[RAM]: Doing compaction");
        Integer tmp=0,limit=0,thisLimit=0,j=0,k=0;
        Boolean flag=true;
        for(int i=0;i<256;i++){
            if(allocatedPartitions.get(i)!=null){
                while(flag){
                    limit=allocatedPartitions.get(i)+i;
                    i+=allocatedPartitions.get(i);
                    if(allocatedPartitions.get(i)==null)
                        flag=false;
                }

                thisLimit=allocatedPartitions.get(i);
                for(j=i;j>limit;j--){
                    for(k=0;k<thisLimit;k++){
                        memory[j+k-1]=memory[j+k];
                        if(k==thisLimit-1){
                            memory[j+k]=' ';
                        }
                    }
                }
                allocatedPartitions.remove(i);
                allocatedPartitions.put(j,k);
                tmp=freePartitions.get(j);
                freePartitions.remove(j);
                freePartitions.put(j+k,tmp);
                mergeMaps(freePartitions);
                limit=j+k;
            }
        }
    }

    public static boolean writeMessage(Procesy.Process proc, String message, int address){
        if(address+message.length()>proc.get_limit()-proc.get_base()||address<0){
            System.out.println("[RAM]: Attempt of writing outside process memory.");
            return false;
        }

        for(int i=0; i<message.length(); i++){
            memory[proc.get_base()+address+i]=message.charAt(i);
        }
        return true;
    }
}
