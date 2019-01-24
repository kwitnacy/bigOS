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
        String temp="";
        if(fileName.length()<7) {
            temp = "src/Interpreter/" + fileName + ".txt";
        }else temp=fileName;

        if(temp!=fileName)
            fileName= "src/Interpreter/" + fileName + ".txt";
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
        int tmp=Process_container.get_by_PID(PID).get_base();
        String check="";
        for(int i=tmp;i<tmp+size;i++){
            check+=memory[i];
        }
        if(program.equals(check) && tmp!=0){
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
            return true;
        }
        System.out.println("[RAM]: there is not enough space to load the program");
        return false;
    }
    public static void writeMemory(char value,Integer address){
        Integer base = Scheduler.running.get_base();
        memory[base+address]=value;
    }
    private static void writeMemory(String fileName,Integer PID){
        int index=0;
        Integer base = Process_container.get_by_PID(PID).get_base();
        for(int i=base ;i< base + fileName.length() ;i++){
            memory[i]=fileName.charAt(index);
            index++;
        }

        allocatedPartitions.put(base,fileName.length()+10);
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
    public static void printMemory(Integer address){
        System.out.println("[RAM]: Memory at index "+ address + " : " + memory[address]);
    }
    public static void printMemory(Integer address1, Integer address2){
        for(int i=address1;i<=address2;i++){
            System.out.println(i+". " + memory[i]);
        }
    }
    public static void move(){
        System.out.println("[RAM]: Doing compaction");
        Integer tmp,limit=0,thisLimit,j,k=0;
        Boolean flag=true;
        Map<Integer,Integer> zmiana = new HashMap<>();
        Map<Integer,Integer> temp = new HashMap<>();
        for(Integer i=0;i<256;i++){
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
                zmiana.put(i,j);
            }

            for(int p=1;p<Process_container.get_size();p++){
                try{
                    temp.put(p,Process_container.get_by_PID(p).get_base());
                }
                catch (Exception e){}
            }
        }
        System.out.println("[RAM]: Updating processes base");
        for(Integer i=1;i<Process_container.get_size();i++){
            try{
                Process_container.get_by_PID(i).ser_base(zmiana.get(temp.get(i)));
            }
            catch (Exception e){
            }
        }
    }

    public static boolean writeMessage(Procesy.Process proc, String message, int address){
        if(address+message.length()>proc.get_limit()||address<0){
            System.out.println("[RAM]: Attempt of writing outside process memory.");
            return false;
        }

        for(int i=0; i<message.length(); i++){
            memory[proc.get_base()+address+i]=message.charAt(i);
        }
        return true;
    }
}
