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
    
    //metody do zarządzana plikami
    
    public void create(String name, String user) //tworzenie pliku, true - jest ok, false - nie powiodło się
    {
        if(name.isEmpty() || name.length()>2 || name.length()<0 || disk.fileSystem.root.checkExistance(name) == true)
            System.out.println("Wrong name of the file!\n");
        else{
            File file = new File();
            file.setName(name);
            file.setUserName(user);
            disk.fileSystem.root.addToRoot(file);
        }
    }
    
    public void displayFreeBlocks(){
           for(int i = 0; i < disk.fileSystem.freeBlocks.size(); i++){
               if(disk.fileSystem.freeBlocks.get(i) == true)    //jeżeli wolny (true) wyświetla 0
                    System.out.print("0 ");
               else if (disk.fileSystem.freeBlocks.get(i) == false)   //jeżeli zajęty (false) wyswietla 1
                    System.out.print("1 ");
           }
           System.out.println();
    }
    
    public void delete(String name) //usunięcie pliku
    {
        disk.fileSystem.root.deleteFromRoot(name);      //usunięcie pliku o podanej nazwie z katalogu
    }
    
    public void write(String name, String data) //zapis/dopisanie do pliku
    {
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
    }
    
    public void read() //odczytywanie danych  z dysku sekwencyjnie
    {}
    
    public void readall() //odczytywanie wszystkich danych z dysku
    {}
    
    
}
