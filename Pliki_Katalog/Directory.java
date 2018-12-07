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
    
    public void addToRoot(File file){      //dodawanie pliku do katalogu
        this.root.add(file);
    }
    
    public void deleteFromRoot(String fileName){       //usuwanie pliku o podanej nazwie z katalogu
        for(File f : root){
            if(this.checkExistance(fileName) == true) root.remove(f);
            else System.out.println("File not found.");
        }
    }
}
