package filemodule;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Weronika Kowalska
 */
public class Directory {
    public String name;
    public List<File> root;

    public Directory() {
        this.name = "root";
        this.root = new LinkedList<>();
    }
    
    public boolean checkExistance(String fileName){
        for(File f : root){
            if(f.getName() == fileName) return true;    //w katalogu jest plik o podanej nazwie
        }
        return false;       //w katalogu nie ma pliku o podanej nazwie
    }
    
    public boolean addToRoot(File file){      //dodawanie pliku do katalogu
        for(File f : root){
            if(this.checkExistance(file.getName()) == true){ 
                return false;    //podany plik istnieje więc nie dodajemy
            }
        }
        this.root.add(file);
        System.out.println("File added to root.");
        return true;   //dodanie do katalogu
    }
    
    public boolean deleteFromRoot(String fileName){       //usuwanie pliku o podanej nazwie z katalogu
        for(File f : root){
            if(checkExistance(fileName) == true && f.getName().equals(fileName)){ 
                root.remove(f);
                return true;    //poprawna nazwa i usunięcie FCB z katalogu
            }
        }
        System.out.println("File not found.");
        return false;   //niepoprawna nazwa
    }
    
    public File getFileByName(String fName){
        if(checkExistance(fName) == true){
            for(File f : root){
                if(f.getName().equals(fName)) return f;  //zwraca żądany plik
            }
        }
        return null;
    }
    
    public void replacebyName (File newFile){
        for(int i = 0; i < root.size(); i++){
            if(newFile.getName() == root.get(i).getName()){
                root.set(i, newFile);   //podmienia plik na ten podnay w argumenice
            }
        }
    } 
    
    public void printRoot(){
        for(int i = 0; i < root.size(); i++){
            System.out.print(root.get(i).getName() + " ");
        }
        System.out.println();
    }
}
