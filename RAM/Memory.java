import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import static java.lang.Math.toIntExact;

/*TODO: 1: dodawanie względem map
        2: usuwanie   --chyba jest
        3: przesuwanie
        4: wiadomosci
*/
public class Memory {

    public static char[] memory = new char[256];

    private static Map<Integer,Integer> filledSpace = new HashMap<>();
    private static Map<Integer,Integer> freeSpace = new HashMap<>();

    private static Integer check(Integer size){
        Integer licznik=0,index=-1;

        for(int i=0;i<256;i++) {
            if (memory[i] == ' ') {
                licznik++;
              //  System.out.println(licznik);
            }else licznik=0;
            if(licznik==size+10){
                index= i - licznik + 1;
                //filledSpace.put(index,size+10);
                return index;
            }
        }
        return index;
    }

    public static void loadProgram(String fileName){
        File file = new File(fileName);
        long length = file.length();
        int check = check(toIntExact(length)+10);
        Integer next=0;
        if (check >= 0) {
            try{
                Scanner skaner= new Scanner(file);

                while(skaner.hasNextLine()){
                    String line= skaner.nextLine();

                    int index=0;
                    for(int i=check + next ;i< check +next + line.length();i++){
                        memory[i]=line.charAt(index);
                        index++;
                    }next+=line.length();
                }
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }filledSpace.put(check,next);
        System.out.println(filledSpace.entrySet());
    }
    public static char readMemory(Integer adress){
        return memory[adress];
    }

    public static void removeProgram(Integer base) {
        int limit = filledSpace.get(base);
        for(int i=base;i<base + limit; i++){
            memory[i]=' ';
        }
        filledSpace.remove(base);
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
        System.out.println("Wolne miejsce: " + (256-zajete) + "\nZajete miejsce: " + zajete);
    }

    public static void move(){
    }

    public static void main(String[] args){
                 Arrays.fill(memory,' ');

                    loadProgram("src/p1.txt");
                    loadProgram("src/p0.txt");
                    loadProgram("src/p2.txt");
                    loadProgram("src/p3.txt");
                    //removeProgram(0);
                 printMemory();
    }

}
