import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import static java.lang.Math.toIntExact;

public class Memory {

    public static char[] memory = new char[256];

    public static Map<Integer,Integer> filledSpace = new HashMap<>();
    public static Map<Integer,Integer> freeSpace = new HashMap<>();

    public static Integer check(Integer size){   //czy jest miejsce na proces
        Integer licznik=0,index=-1;

        for(int i=0;i<256;i++) {
            if (memory[i] == ' ') {
                licznik++;
              //  System.out.println(licznik);
            }else licznik=0;
            if(licznik==size+10){
                index= i - licznik + 1;
                //filledSpace.put(index,size+10);
                //System.out.println(filledSpace.entrySet());
                return index;
            }
        }
        return index;
    }

    //zapis do pamieci
    public static void loadProgram(String fileName){

        File file = new File(fileName);
        long length = file.length();
        int check = check(toIntExact(length)+10);
        if (check >= 0) {
            try{
                Scanner skaner= new Scanner(file);
                int next=0;
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
        }

    }
    //odczyt z pamieci
    public static char readMemory(Integer adress){
        return memory[adress];
    }
    public static void removeMemory(Integer base,Integer limit) {
        for(int i=base;i<base+limit;i++){
            memory[i] = ' ';
        }
        // list freeSpace
    }

    public static void printMemory(){
        for(int i=0;i<64;i++){
            //if(!(memory[i]==' ' && memory[i+1]==' '))
            System.out.println(i + ": " + memory[i] + "\t\t\t"
                    + (i+64) + ": " + memory[i+64]+ "\t\t\t"
                    + (i+128) + ": " + memory[i+128]+ "\t\t\t"
                    + (i+192) + ": " + memory[i+192]);
        }
        for(int i=0;i<128;i++){
            int wolne=0,zajete=0;
            //if()

        }
    }

    //przesuwanie blokow
    public static void move(){
    }

    public static void main(String[] args){
                 Arrays.fill(memory,' ');

                    loadProgram("src/p1.txt");
                    loadProgram("src/p0.txt");
                 printMemory();
    }

}
