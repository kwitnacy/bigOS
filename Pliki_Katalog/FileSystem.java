package filemodule;

import java.util.BitSet;

/**
 *
 * @author Weronika Kowalska
 */
public class FileSystem {
    public BitSet freeBlocks;   //pokazuje które bloki wolne, 0 - wolny, 1 - zajęty, wielkość 32
    public Integer[] FAT;       //tabica FAT, wielkość 32
    public Directory root;      //katalog główny
    
    public FileSystem() {
        this.freeBlocks = new BitSet(32);
        this.FAT = new Integer[32];
        
        for(int i = 0; i < 32; i++) 
            FAT[i] = 0;         //wypełnia FAT zerami    
        
        for(int i = 0; i < 32; i++) 
            this.freeBlocks.set(i, false); //wszystkie wolne
        
        this.root = new Directory(); //tworzenie głównego katalogu
    }
    
    public int checkFreeBlocks(){   //zwraca liczbę wolnych bloków
        int freeB = 0;
        for(int i = 0; i < freeBlocks.size(); i++){
            if(freeBlocks.get(i) == false) freeB++; //jeżeli jest wolny
        }
        return freeB;
    }
    
    public void displayFAT(){
        for(int i : FAT){
            System.out.print(i + " ");
        }System.out.println("");
    }
}
