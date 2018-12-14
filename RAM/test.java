import java.util.Arrays;

public class Memory {
    //tablica ram
    public static char[] memory = new char[128];

    private static Integer base,limit;

    //czy jest miejsce na proces

    public static Integer check(Integer size){
        Integer licznik=0,index=-1;

        for(int i=0;i<128;i++) {
            if (memory[i] == ' ') {
                licznik++;
            } else {
                if (licznik >= size) {
                    index = i - licznik;
                    break;
                }
            }
        }
        return index;
    }

    //zapis do pamieci
    public static void writeMemory(String value,Integer size){
        for(int i=check(size);i<size;i++){
            memory[i]=value.charAt(i);
        }
    }
    //odczyt z pamieci
    public static char readMemory(Integer adress){
        return memory[adress];
    }
    //przesuwanie blokow
    public static void move(){}


    public static void main(String[] args){
        Arrays.fill(memory,' ');
        //memory[10] = 'a';
        //memory[5] = 's';
                    System.out.println(memory);
    }

}
