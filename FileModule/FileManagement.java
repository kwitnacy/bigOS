package FileModule;


import java.util.LinkedList;

/**
 *
 * @author Weronika Kowalska
 */
public class FileManagement {
    private static Disk disk;

    static {
        disk = new Disk();
    }
    
    //pomocnicze funkcje
    
    //sprawdza czy wprowadzona nazwa pliku jest poprawna
    private static boolean properFileName(String name){
        if(name.isEmpty() || name.length()>2 || name.length()<0) return false;
        else return true;
    }
    
    //wyświetla wektor bitowy
    public static void displayFreeBlocks(){
           for(int i = 0; i < 32; i++){
               if(disk.fileSystem.freeBlocks.get(i))    //jeżeli zajety wyświetla 1
                    System.out.print("1 ");
               else if (!disk.fileSystem.freeBlocks.get(i))   //jeżeli wolny wyswietla 0
                    System.out.print("0 ");
           }
           System.out.println();
    }
    
    //wyświetla zawartość dysku
    public static void printDisk()
    {
        for(int j = 0; j < 32; j++){
            System.out.print(j +". block: ");
            for(int i = 0; i < 32; i++){
                System.out.print(disk.data[j*32+i]);
            }
            System.out.println();
        }
    }
    
    //wyświetla zawartość katalogu 
    public static void displayRoot()
    {
        disk.fileSystem.root.printRoot();
    }
    
    //metody do zarządzana plikami
    
    //wyswietla tablice FAT, wektor bitowy i dysk
    public static void printFileSystem(){
        System.out.println("[File Module]:");
        System.out.println("FAT: ");disk.fileSystem.displayFAT();
        System.out.println("Bit vecotr: ");
        displayFreeBlocks();
        System.out.println("Disk: ");
        printDisk();
    }
    
    //wyswietla FCB pliku
    public static boolean displayFCB(String fileName)
    {
        if(!disk.fileSystem.root.checkExistance(fileName)){
            System.out.println("[File Module]: File not found in the root.");
            return false;
        } //nie ma pliku w katalogu
        //if(properFileName(fileName) == false) {System.out.println("[File Module]: Incorrect file name."); return false;}   //niepoprawna nazwa pliku
        System.out.println("[File Module]:");
        File ftemp = disk.fileSystem.root.getFileByName(fileName);
        System.out.println("File name: " + ftemp.getName());
        System.out.println("Username: " + ftemp.getUserName());
        System.out.println("FAT index: " + ftemp.getIndex());
        System.out.println("Size of file (number of blocks): " + ftemp.getSize());
        System.out.println("Semaphore value: " + ftemp.s.getValue());
        return true;
    }
    
    //metody do zarządzania semaforami w plikach
    
    public static boolean signalFile(String file_name)
    {
        if(!disk.fileSystem.root.checkExistance(file_name)){
            System.out.println("[File Module]: File not found in the root.");
            return false;
        } //nie ma pliku w katalogu
        File ftemp = disk.fileSystem.root.getFileByName(file_name);
        ftemp.s.signal_s();
        disk.fileSystem.root.replacebyName(ftemp);
        return true;
    }
    
    public static boolean waitFile(String file_name, int PID)
    {
        if(!disk.fileSystem.root.checkExistance(file_name)){
            System.out.println("[File Module]: File not found in the root.");
            return false;
        } //nie ma pliku w katalogu
        File ftemp = disk.fileSystem.root.getFileByName(file_name);
        ftemp.s.wait_s(PID);
        disk.fileSystem.root.replacebyName(ftemp);
        return true;
    }
    
    //wyswietla kolejkę semaforów do jednego pliku
    public static boolean printSem(String file_name){
        if(!disk.fileSystem.root.checkExistance(file_name)){
            System.out.println("[File Module]: File not found in the root.");
            return false;
        } //nie ma pliku w katalogu
        File ftemp = disk.fileSystem.root.getFileByName(file_name);
        ftemp.s.print_queue();
        return true;
    }
    
    //tworzenie pliku
    
    public static boolean create(String name, String user) 
    {
        if(!properFileName(name)){
            System.out.println("[File Module]: Incorrect file name.");
            return false;
        }   //niepoprawna nazwa pliku
        if(disk.fileSystem.root.checkExistance(name)){
            System.out.println("[File Module]: File already exists.");
            return false;
        } //jest już taki plik w katalogu
        else{
            File file = new File();
            file.setName(name);
            file.setUserName(user);
            System.out.println("[File Module]: User " + user + " created a file named " + name + ".");
            disk.fileSystem.root.addToRoot(file);
            System.out.println("[File Module]: File " + name + " created by " + user + " added to the root.");
            return true;
        }
    }
    
    //zapis/dopisywanie do pliku
    
    public static boolean write(String name, String data)
    {
        if(!disk.fileSystem.root.checkExistance(name)){
            System.out.println("[File Module]: File not found in the root.");
            return false;
        } //nie ma pliku w katalogu

        File ftemp = disk.fileSystem.root.getFileByName(name);

        int sizeBytes = data.length();   //tyle bajtów zajmuje
        int lastBlockBytes = (32-(ftemp.getSize()%32))%32; //tyle bajtów zostanie w ostatnim zajętym bloku
        int blocks = (int) Math.ceil((sizeBytes-lastBlockBytes) / 32.0); //tyle bloków zajmie
        System.out.println("[File Module]: Number of blocks needed for data: " + blocks);

        if(!properFileName(name) && blocks > disk.fileSystem.checkFreeBlocks()){
            System.out.println("[File Module]: Incorrect file name and not enough space on the disk.");
            return false;
        }   //błędna nazwa i za duże dane
        if(blocks > disk.fileSystem.checkFreeBlocks()){
            System.out.println("[File Module]: Not enough space on the disk.");
            return false;
        } //nie ma wystarczająco wolnych bloków


        ftemp.setSize(sizeBytes);

        char[] dat = data.toCharArray();    //dane zapisane w tablicy char[]
        
        LinkedList<Integer> freeB = new LinkedList();      //lista zawierająca wolne bloki
        
        for(int j = 0; j < 32; j++){    //sprawdzanie wolnych bloków
            if(!disk.fileSystem.freeBlocks.get(j)){     //jeśli dany blok jest wolny
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
        if(freeB.size()<0) System.out.println("[File Module]: Free blocks list capacity is less than 0!");
		System.out.println("[File Module]: Writing to file " + name + " data " + data);
        if(lastBlockBytes != 0){
            for(int i=32-lastBlockBytes;i<32;i++){
                disk.data[index*32+i] = dat[dataPos];
                dataPos++;
                if(dataPos >= dat.length) { break; }
            }
        }
        for(int i = 0; i < freeB.size(); i++){
            disk.fileSystem.FAT[index] = freeB.get(i);  //następny wolny indeks
            index = freeB.get(i);
            if(index == -1) { break; }
            for(int j = 0; j < 32; j++){    //alokacja bloku dyskowego
                disk.data[freeB.get(i)*32+j] = dat[dataPos];
                dataPos++;
                if(dataPos >= dat.length) { break; }
            }
            disk.fileSystem.freeBlocks.set(freeB.get(i));   //blok zajęty
        }
        disk.fileSystem.root.replacebyName(ftemp);   //podmieniamy plik na ten ze zmodyfikowanym FCB
        return true;
    }
    
    //zczytywanie określonej liczby bajtów (nazwa pliku z którego zczytujemy, od którego bajtu zaczynamy zczytywanie, ile bajtów zczytujemy)
    //zwraca String zawierający zczytane bajty
    
    public static String read(String fileName, int from, int howMany) 
    {
        if(!disk.fileSystem.root.checkExistance(fileName)){
            System.out.println("[File Module]: File not found in the root.");
            return null;
        } //nie ma pliku w katalogu
        File ftemp = disk.fileSystem.root.getFileByName(fileName);
        if(ftemp.getSize() == 0){
            System.out.println("[File Module]: File doesn't contain any data on the disk.");
            return null;
        }  //plik nie ma danych zapisanych na dysku
        
		System.out.println("[File Module]: Reading " + howMany + " bytes from " + from + " byte, from " + fileName + " file.");
		
        String output = new String();  //tu będą zapisane zczytane dane
        int tempindex = ftemp.getIndex();
        LinkedList<Integer> blocks = new LinkedList();
        
        //Ściąganie indeksów bloków pliku z FAT
        blocks.add(tempindex);
        while(tempindex != -1){
            tempindex = disk.fileSystem.FAT[tempindex];
            blocks.add(tempindex);
        }
        blocks.removeLast();
        
        System.out.println("[File Module]: Blocks that will be read: " + blocks);

        //Pozycja czytania
        int readPosition = 0;
        int stop = from+howMany;
        
        if(stop > ftemp.getSize()) {
            System.out.println("[File Module]: Wrong value.");
            return null;
        }

        //Pętla czytania
        while(from < stop){
            readPosition = blocks.get(from/32)*32 + from%32;
            output = output.concat(Character.toString(disk.getByte(readPosition)));
            from++;
        }

        return output;
    }
    
    //czytanie zawartości całego pliku
    
    public static String readFile(String fileName) //odczytywanie wszystkich danych z dysku
    {
        if(!disk.fileSystem.root.checkExistance(fileName)){
            System.out.println("[File Module]: File not found in the root.");
            return null;
        } //nie ma pliku w katalogu
        File ftemp = disk.fileSystem.root.getFileByName(fileName);
        if(ftemp.getSize() == 0){
            System.out.println("[File Module]: File doesn't contain any data on the disk.");
            return null;
        }  //plik nie ma danych zapisanych na dysku
        
		System.out.println("[File Module]: File reading entire " + fileName + " file.");
		
        String output = new String();  //tu będą zapisane zczytane dane
        int tempindex = ftemp.getIndex();
        LinkedList<Integer> blocks = new LinkedList();
        
        //Ściąganie indeksów bloków pliku z FAT
        blocks.add(tempindex);
        while(tempindex != -1){
            tempindex = disk.fileSystem.FAT[tempindex];
            blocks.add(tempindex);
        }
        blocks.removeLast();
        
        System.out.println("[File Module]: Blocks that will be read: " + blocks);
        String buffer;
        int blocknum=0;
        for(int i : blocks){
            if(disk.getBlockOccup(i) == 32) {
                buffer = new String(disk.getBlock(i));
            }
            else{
                buffer = read(fileName,blocknum*32,disk.getBlockOccup(i));
            }
            output = output.concat(buffer);
            blocknum++;
        }
        return output;
    }
    
    //usunięcie pliku
    
    public static boolean delete(String name) 
    {
        if(!disk.fileSystem.root.checkExistance(name)){
            System.out.println("[File Module]: File " + name + " not found in the root.");
            return false;
        }  //pliku o podanej nazwie nie ma w katalogu
        
		System.out.println("[File Module]: Deleting " + name + " file from file system.");
		
        File ftemp = disk.fileSystem.root.getFileByName(name);
        int tempindex = ftemp.getIndex();
        LinkedList<Integer> blocks = new LinkedList();
        
        //Ściąganie indeksów bloków pliku z FAT
        blocks.add(tempindex);
        while(tempindex != -1){
            tempindex = disk.fileSystem.FAT[tempindex];
            blocks.add(tempindex);
        }
        blocks.removeLast();
        
        System.out.println("[File Module]: Blocks that will be deleated: " + blocks);
        for(int i : blocks){
           disk.clearBlock(i);
           disk.fileSystem.freeBlocks.set(i, false);
           disk.fileSystem.FAT[i] = -1;
            //System.out.println("[File Module]: Block number " + i + " deleated.");
        }
        disk.fileSystem.root.deleteFromRoot(name);      //usunięcie pliku o podanej nazwie z katalogu
        return true;
    }

}
