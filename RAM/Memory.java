package RAM;
import Processor.Scheduler;
import Procesy.Process;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class Memory {

    public static char[] memory = new char[256];

    private static Map<Integer,Integer> allocatedPartitions = new HashMap<>();
    private static Map<Integer,Integer> freePartitions =  new HashMap<>();

    public static void memoryInit(){
        Arrays.fill(memory,' ');
        freePartitions.put(0,256);
    }

    public static Boolean loadProgram(){
        String fileName;
        fileName= "src/Interpreter/" + Scheduler.running.get_file_name();
        System.out.println("sciezka: " + fileName);
        Integer size=0,value=0;
        File file = new File(fileName);
        try{
            Scanner skaner= new Scanner(file);

            while(skaner.hasNextLine()){
                size+= skaner.nextLine().length();
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        for(int i=0;i<256;i++) {
            if(freePartitions.get(i)!=null)
                value = freePartitions.get(i);
            if(value>=size+10){
                writeMemory(fileName,i);
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
            loadProgram();
        }
        System.out.println("[RAM]: there is no space to load the program");
        return false;
    }
    public static void writeMemory(char value,Integer address){
        Integer base = Scheduler.running.get_base();
        memory[base+address]=value;
    }
    private static void writeMemory(String fileName,Integer base){
        File file = new File(fileName);
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
        }
        allocatedPartitions.put(base,next+10);
        Scheduler.running.ser_base(base);
        Scheduler.running.ser_limit(next+10);

        System.out.println("[RAM]: Program has been put in RAM at " + base + "-" + (base+next-1));
        Integer tmp = freePartitions.get(base);
        freePartitions.remove(base);
        Integer limit = tmp;
        freePartitions.put(base+next+10,tmp-next-10);
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
    public static void removeProgram() {
        Integer base= Scheduler.running.get_base();
        for(int i=base;i<allocatedPartitions.get(base) + base; i++){
            memory[i]=' ';
        }
        Integer tmp = allocatedPartitions.get(base);
        allocatedPartitions.remove(base);
        System.out.println("[RAM]: program has been deleted " + base);
        freePartitions.put(base,tmp);
        mergeMaps(freePartitions);
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
