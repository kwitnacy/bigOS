package filemodule;

import java.util.LinkedList;

/**
 *
 * @author Weronika Kowalska
 */
public class FileManagement {
    private Disk disk;

    public FileManagement() {
        this.disk = new Disk();
    }
    
    //kody błędów: 0 - wszystko poprawne, 1 - błędna nazwa, 2 - nie odnaleziono w katalogu
    //3 - za duży rozmiar danych, 4 - błędna nazwa i  za duży rozmiar danych
    //5 - plik o podanej nazwie już istnieje
    
    //pomocnicze
    
    private boolean properFileName(String name){
        if(name.isEmpty() || name.length()>2 || name.length()<0){
            System.out.println("Wrong name of the file."); 
            return false;
        }
        return true;
    }
    
    //metody do zarządzana plikami
    
    public void printFAT(){
        disk.fileSystem.displayFAT();
    }
    
    public void signalFile(String file_name)
    {
        File ftemp = disk.fileSystem.root.getFileByName(file_name);
        //ftemp.s.signal();    ???
        disk.fileSystem.root.replacebyName(ftemp);
    }
    
    public void waitFile(String file_name)
    {
        File ftemp = disk.fileSystem.root.getFileByName(file_name);
        //ftemp.s.wait();    ???
        disk.fileSystem.root.replacebyName(ftemp);
    }
    
    public int create(String name, String user) //tworzenie pliku
    {
        if(properFileName(name) == false) return 1;   //niepoprawna nazwa pliku
        if(disk.fileSystem.root.checkExistance(name) == true) return 5; //jest już taki plik w katalogu
        else{
            File file = new File();
            file.setName(name);
            file.setUserName(user);
            System.out.println("User " + user + " created a file named " + name);
            disk.fileSystem.root.addToRoot(file);
            return 0;
        }
    }
    
    public void displayFreeBlocks(){
           for(int i = 0; i < 32; i++){
               if(disk.fileSystem.freeBlocks.get(i) == true)    //jeżeli wolny (false) wyświetla 1
                    System.out.print("1 ");
               else if (disk.fileSystem.freeBlocks.get(i) == false)   //jeżeli zajęty (true) wyswietla 0
                    System.out.print("0 ");
           }
           System.out.println();
    }
    
    public int write(String name, String data) //zapis/dopisanie do pliku
    {
        int sizeBytes = data.length();   //tyle bajtów zajmuje
        int blocks = (int) Math.ceil(sizeBytes / 32.0); //tyle bloków zajmie
        System.out.println("Number of blocks needed for data: " + blocks);

        if(blocks > disk.fileSystem.checkFreeBlocks()) {
            System.out.println("Not enough space on the disk");
            return 3;
        }
        if(properFileName(name) == false) return 1;     //błedna nazwa
        if(properFileName(name) == false && blocks > disk.fileSystem.checkFreeBlocks()) return 4;    //błędna nazwa i za duże dane
        
        File ftemp = disk.fileSystem.root.getFileByName(name);
        
        ftemp.setSize(blocks);

        char[] dat = data.toCharArray();    //dane zapisane w tablicy char[]
        
        LinkedList<Integer> freeB = new LinkedList();      //lista zawierająca wolne bloki
        
        for(int j = 0; j < 32; j++){    //sprawdzanie wolnych bloków
            if(disk.fileSystem.freeBlocks.get(j) == false){     //jeśli dany blok jest wolny
                freeB.add(j);    //dodajemy nr bloku do wektora wolnych bloków
            }
            if(freeB.size()==blocks) break;     //dzięki temu wektor będzie zawierał odpowiednią liczbę bloków potrzebnych dla danego pliku
        }
        
        freeB.add(-1);
        int index;
        if(ftemp.getIndex() == -1) { 
            ftemp.setIndex(freeB.getFirst());
            index = freeB.getFirst();
        }
        else{
            index = ftemp.getIndex();
            while(disk.fileSystem.FAT[index]!=-1){
                index = disk.fileSystem.FAT[index];
            }
        }
        
        //zapis na bloki dyskowe
        int dataPos = 0;
        if(freeB.size()<0) System.out.println("DUPA");
        for(int i = 0; i < freeB.size(); i++){
            disk.fileSystem.FAT[index] = freeB.get(i);  //następny wolny indeks
            index = freeB.get(i);
            if(index == -1) { break; }
            for(int j = 0; j < 32; j++){    //alokacja bloku dyskowego
                disk.data[freeB.get(i)*32+j] = dat[dataPos];
                dataPos++;
                if(dataPos >= dat.length) { break; }
            }
            
                    System.out.println("do set"+freeB.get(i));
                    disk.fileSystem.freeBlocks.set(freeB.get(i));   //blok zajęty
            this.displayFreeBlocks();
        }
        
        disk.fileSystem.root.replacebyName(ftemp);   //podmieniamy plik na ten ze zmodyfikowanym FCB
        
        return 0;   //wszystko zczytane poprawnie
    }
    
    //zczytywanie określonej liczby bajtów (nazwa pliku z którego zczytujemy, od którego bajtu zaczynamy zczytywanie, ile bajtów zczytujemy)
    //zwraca String zawierający zczytane bajty
    public String read(String fileName, int from, int howMany) 
    {
        File ftemp = disk.fileSystem.root.getFileByName(fileName);
        String output = new String();  //tu będą zapisane zczytane dane
        //int size = ftemp.getSize();
        int tempindex = ftemp.getIndex();
        LinkedList<Integer> blocks = new LinkedList();
        
        //Ściąganie indeksów bloków pliku z FAT
        blocks.add(tempindex);
        while(tempindex != -1){
            tempindex = disk.fileSystem.FAT[tempindex];
            blocks.add(tempindex);
        }
        blocks.removeLast();
        
        System.out.println("Pobrane nr blokow: " + blocks);
        
        //Numer indeksu w pliku od którego mamy czytać
        Double fromIndex = Math.floor(howMany/32);
        
        //Końcowy indeks w pliku do którego mamy czytać
        Double toIndex = Math.floor((from+howMany)/32);
        
        //Pozycja czytania
        int readPosition = fromIndex.intValue() + from;
        
        String buffer = new String();
        Double bufferIndex = new Double(-1);
        
        //Pętla czytania
        while(readPosition < from + howMany){
            //Aktualizacja bufora
            if(bufferIndex != Math.floor(readPosition/32)) {
                bufferIndex = Math.floor(readPosition/32);
                buffer = new String(disk.getBlock(bufferIndex));
            }
            
            output = output.concat(Character.toString(buffer.charAt(readPosition%32)));
            readPosition++;
        }

        return output;
    }
    
    //czytanie zawartości całego pliku o podanej nazwie
    public String readFile(String fileName) //odczytywanie wszystkich danych z dysku
    {
        File ftemp = disk.fileSystem.root.getFileByName(fileName);
        String output = new String();  //tu będą zapisane zczytane dane
        //int size = ftemp.getSize();
        int tempindex = ftemp.getIndex();
        LinkedList<Integer> blocks = new LinkedList();
        
        //Ściąganie indeksów bloków pliku z FAT
        blocks.add(tempindex);
        while(tempindex != -1){
            tempindex = disk.fileSystem.FAT[tempindex];
            blocks.add(tempindex);
        }
        blocks.removeLast();
        
        System.out.println("Pobrane nr blokow: " + blocks);
        String buffer;
        
        for(int i : blocks){
           Double d = new Double(i);
           buffer = new String(disk.getBlock(d));
           output = output.concat(buffer);
        }
        System.out.println("Oto przeczytany plik: " + output);
        return output;
    }
    
    public int delete(String name) //usunięcie pliku
    {
        if(properFileName(name) == false){
            return 1;   //niepoprawna nazwa pliku
        }
        if(disk.fileSystem.root.checkExistance(name)==false){
            System.out.println("File not found in the root.");
            return 2;   //pliku o podanej nazwie nie ma w katalogu
        }
        
        disk.fileSystem.root.printRoot();               
        File ftemp = disk.fileSystem.root.getFileByName(name);
        //int size = ftemp.getSize();
        int tempindex = ftemp.getIndex();
        LinkedList<Integer> blocks = new LinkedList();
        
        //Ściąganie indeksów bloków pliku z FAT
        blocks.add(tempindex);
        while(tempindex != -1){
            tempindex = disk.fileSystem.FAT[tempindex];
            blocks.add(tempindex);
        }
        blocks.removeLast();
        
        System.out.println("Pobrane nr blokow: " + blocks);
        
        for(int i : blocks){
           Double d = new Double(i);
           disk.clearBlock(d);
            System.out.println("Opróżniono " + i + "ty blok.");
        }
        
        disk.fileSystem.root.deleteFromRoot(name);      //usunięcie pliku o podanej nazwie z katalogu

        
        return 0;   //poprawne usunięcie
        
    }
    
    public void readAll() //odczytywanie wszystkich danych z dysku
    {}
    
    public void printDisk()
    {
        for(int j = 0; j < 32; j++){
            System.out.print(j +". block: ");
            for(int i = 0; i < 32; i++){
                System.out.print(disk.data[j*32+i]);
            }
            System.out.println();
        }
    }
    
    public void displayRoot()
    {
        disk.fileSystem.root.printRoot();
    }
    
}
