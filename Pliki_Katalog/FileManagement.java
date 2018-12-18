package filemodule;

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

    public int create(String name, String user) //tworzenie pliku
    {
        if(properFileName(name) == false) return 1;   //niepoprawna nazwa pliku
        if(disk.fileSystem.root.checkExistance(name) == true) return 5; //jest już taki plik w katalogu
        else{
            File file = new File();
            file.setName(name);
            file.setUserName(user);
            disk.fileSystem.root.addToRoot(file);
            System.out.println("User " + user + " created a file named " + name);
            return 0;
        }
    }
    
//    public void displayFreeBlocks(){
//           for(int i = 0; i < disk.fileSystem.freeBlocks.size(); i++){
//               if(disk.fileSystem.freeBlocks.get(i) == true)    //jeżeli wolny (true) wyświetla 0
//                    System.out.print("0 ");
//               else if (disk.fileSystem.freeBlocks.get(i) == false)   //jeżeli zajęty (false) wyswietla 1
//                    System.out.print("1 ");
//           }
//           System.out.println();
//    }
    
    public int delete(String name) //usunięcie pliku
    {
        if(properFileName(name) == false){
            return 1;   //niepoprawna nazwa pliku
        }
        if(disk.fileSystem.root.deleteFromRoot(name)==false){
            System.out.println("File not found in the root.");
            return 2;   //pliku o podanej nazwie nie ma w katalogu
        }
        disk.fileSystem.root.deleteFromRoot(name);      //usunięcie pliku o podanej nazwie z katalogu
        return 0;   //poprawne usunięcie
        
    }
    
    public int write(String name, String data) //zapis/dopisanie do pliku
    {
        int size = data.length();   //tyle bajtów zajmuje
        int blocks = (int) Math.ceil(size / 100.0); //tyle bloków zajmie
        System.out.println("Number of blocks data will need " + blocks);
        
        if(blocks > disk.fileSystem.checkFreeBlocks()) {
            System.out.println("Not enough space on the disk");
            return 3;
        }
        if(properFileName(name) == false) return 1;     //błedna nazwa
        if(disk.fileSystem.root.checkExistance(name) == true) return 5; //jest już taki plik w katalogu
        if(properFileName(name) == false && size > disk.getDataSize()) return 4;    //bbłędna nazwa i za duże dane
        
        File ftemp = disk.fileSystem.root.getFileByName(name);
        ftemp.setSize(blocks);
        int temp = blocks;
        //szukanie pierwszego wolnego bloku
        for(int i = 0; i < 32; i++){
            if(disk.fileSystem.freeBlocks.get(i) == false){ //jeśli dany blok jest wolny
                if(ftemp.getIndex() == -2){     //jeśli plik nie ma jeszcze początkowego indeksu w FAT
                    ftemp.setIndex(i);     //ustawia w FCB indeks bloku
                }
                if(blocks == 1){                //jeśli plik zajmuje tyko jeden blok w pamięci
                    disk.fileSystem.FAT[i] = -1;    
                }
                if(ftemp.getIndex() > -1){
                    
                }
            }
        }
        
        
//        //ustalanie rozmiaru pliku
//        disk.fileSystem.root.fileMap.get(name).setSize(data.length()*4);    //liczba znaków pomnożona przez rozmiar 1 bajta
//        //przypisywanie do tymczasowej zmiennej
//        int temp_size = disk.fileSystem.root.fileMap.get(name).getSize();
//        for(int i = 0; i < disk.fileSystem.freeBlocks.length(); i++){
//            if(disk.fileSystem.freeBlocks.get(i) == true){      //jeśli jest blok wolny
//                disk.fileSystem.freeBlocks.set(i, false);       //w freeBlocks pod indeksem i zapisujemy 1 (false)
//                
//                disk.fileSystem.root.fileMap.get(name).setIndex(i);
//            }
//        
 //       }
        return 0;
    }
    
    public void read() //odczytywanie danych  z dysku sekwencyjnie
    {}
    
    public void readall() //odczytywanie wszystkich danych z dysku
    {}
    
    
}
