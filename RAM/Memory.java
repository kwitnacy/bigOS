package RAM;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import static java.lang.Math.toIntExact;

/*TODO:     1: wiadomosci (dodawanie)
            2: poprawki kosmetyczno-bamboszkowe
*/
public class Memory {

    public static char[] memory = new char[256];

    private static Map<Integer,Integer> filledSpace = new HashMap<>();
    private static Map<Integer,Integer> freeSpace =  new HashMap<>();

    public static Boolean loadProgram(String fileName){
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
            if(freeSpace.get(i)!=null)
                value = freeSpace.get(i);
            if(value>=size+10){
                writeMemory(fileName,i);
                return true;
            }
        }
        Integer free=0;
        for(int j=0;j<256;j++){
            if(freeSpace.get(j)!=null){
                free+=freeSpace.get(j);
            }
        }
        if(free>=size+10) {
            move();
            loadProgram(fileName);
        }
        System.out.println("[RAM]: za malo miejsca dla programu");
        return false;
    }
    public static void writeMemory(char value,Integer index){

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
        filledSpace.put(base,next+10);
        System.out.println("[RAM]: Program zostal umieszczony w pamieci w adresach " + base + "-" + (base+next-1));
        Integer tmp = freeSpace.get(base);
        freeSpace.remove(base);
        Integer limit = tmp;
        freeSpace.put(base+next+10,tmp-next-10);
    }
    public static Character readMemory(Integer adress){
        if(adress<0||adress>256){
            System.out.println("[RAM] Invalid addres.");
            return null;
        }
        return memory[adress];
    }

    private static void mergeMaps(Map<Integer,Integer> map){
        Integer preBase=0,preLimit=0,
                base=0,limit;
        for(int i=0;i<256;i++){
            limit = map.get(i);
            if(limit!=null) {
                if(i==(preBase+preLimit)) {
                    freeSpace.remove(preBase);
                    freeSpace.remove(i);
                    freeSpace.put(preBase,i+limit-preBase);
                }
                preLimit=limit;
                preBase=i;
            }
        }
    }
    public static void removeProgram(Integer base) {
//        int limit = filledSpace.get(base);
        for(int i=base;i<filledSpace.get(base) + base; i++){
            memory[i]=' ';
        }
        Integer tmp = filledSpace.get(base);
        filledSpace.remove(base);
        System.out.println("[RAM]: usunieto program rozpoczynajacy sie w adresie " + base);
        freeSpace.put(base,tmp);
        mergeMaps(freeSpace);
    }

    public static void printMemory(){
        for(int i=0;i<64;i++){
            //if(!(memory[i]==' ' && memory[i+1]==' '))
            System.out.println(i + ": " + memory[i] + "\t\t\t"
                    + (i+64) + ": " + memory[i+64]+ "\t\t\t"
                    + (i+128) + ": " + memory[i+128]+ "\t\t\t"
                    + (i+192) + ": " + memory[i+192]);
        }
        int zajete = filledSpace.values().stream().reduce(0, Integer::sum);
        System.out.println("Ilosc wolnego miejsca: " + (256-zajete) + "\nIlosc zajetego miejsca: " + zajete);
        System.out.println("Zajete obszary: " + filledSpace.entrySet());
        System.out.println("Wolne obszary: " + freeSpace.entrySet());
    }

    public static void move(){
        System.out.println("[RAM]: Przesuwanie obszarow");
        Integer tmp=0,limit=0,thisLimit=0,j=0,k=0;
        Boolean flag=true;
        for(int i=0;i<256;i++){
            if(filledSpace.get(i)!=null){
                while(flag){
                    limit=filledSpace.get(i)+i;
                    i+=filledSpace.get(i);
                    if(filledSpace.get(i)==null)
                        flag=false;
                }

                thisLimit=filledSpace.get(i);
                for(j=i;j>limit;j--){
                    for(k=0;k<thisLimit;k++){
                        memory[j+k-1]=memory[j+k];
                        if(k==thisLimit-1){
                            memory[j+k]=' ';
                        }
                    }
                }
                filledSpace.remove(i);
                filledSpace.put(j,k);
                tmp=freeSpace.get(j);
                freeSpace.remove(j);
                freeSpace.put(j+k,tmp);
                mergeMaps(freeSpace);
                limit=j+k;
            }
        }
    }

    public static boolean writeMessage(Procesy.Process proc, String message, int addres){
        if(addres+message.length()>proc.get_limit()-proc.get_base()||addres<0){
            System.out.println("[RAM] Attempt of writing outside process memory.");
            return false;
        }

        for(int i=0; i<message.length(); i++){
            memory[proc.get_base()+addres+i]=message.charAt(i);
        }
        return true;
    }
    public static void main(String[] args){
        Arrays.fill(memory,' ');
        freeSpace.put(0,256);
        loadProgram("src/p0.txt");
        loadProgram("src/p1.txt");
        loadProgram("src/p0.txt");
        removeProgram(14);
        move();
        printMemory();
    }
}
