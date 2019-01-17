package RAM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Memory {
    public class Pair {
        private Integer base;
        private Integer limit;

        public Pair(Integer base, Integer limit) {
            this.base = base;
            this.limit = limit;
        }

        public Integer getBase() {
            return base;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setBase(Integer base) {
            this.base = base;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }
    }

    //tablica ram11
    public static char[] memory = new char[128];


    List<Pair> filledSpace = new ArrayList<>();
    List<Pair> freeSpace = new ArrayList<>();
    Pair tmp = new Pair(5,4);


    public static Integer check(Integer size){   //czy jest miejsce na proces

        Integer licznik=0,index=-1;

        for(int i=0;i<128;i++) {
            if (memory[i] == ' ') {
                licznik++;
            } else {////do poprawy
                if (licznik >= size + 10) {
                    index = i - licznik;
                    return index;
                }
            }
        }

        return index;
    }

    //zapis do pamieci
    public static void writeMemory(String value) {
        Integer check = check(value.length());
        //System.out.println(check);
        //System.out.println(value.length());
        if (check >= 0) {
            int index=0;
            for (int i = check; i < value.length() + check; i++) {
                memory[i] = value.charAt(index);
                index++;
            }
            //filed.add ?luyi!?kuk!?jyt!?!??fafasdsd!?!?@?1#?2E?A?Fsdnfuizdsbfokad

        } else {
            move();
            if (check > 0) {
                for (int i = check; i <= value.length(); i++) {
                    memory[i] = value.charAt(i);
                }
            } else System.out.println("Error"); //semafor wait
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
		int free,filled;
	  for(int i=0;i<32;i++){
            //if(!(memory[i]==' ' && memory[i+1]==' '))
            System.out.println(i + ": " + memory[i] + "\t\t"
                    + (i+32) + ": " + memory[i+32]+ "\t\t"
                    + (i+64) + ": " + memory[i+64]+ "\t\t"
                    + (i+96) + ": " + memory[i+96]);
					
        }
    }

    //przesuwanie blokow
    public static void move(){
    }
	
    public static void main(String[] args){
                 Arrays.fill(memory,' ');
                 memory[127]='s';
                 writeMemory("bamboszek");
                 writeMemory("JM 144");
                 writeMemory("MO 15 26");

                 printMemory();
    }

}
