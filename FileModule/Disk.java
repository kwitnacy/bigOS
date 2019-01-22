package filemodule;

/**
 *
 * @author Weronika Kowalska
 */
public class Disk {
    public FileSystem fileSystem;
    public char[] data;    //właściwy dysk, 32 bloki, każdy po 32 bajty

    public Disk() {
        this.fileSystem = new FileSystem();
        this.data = new char[32*32]; //32 bloki po 32 bajty
    }

    public int getDataSize() {
        return 32*32;
    }
    
    public char getByte(int index){
        return data[index];
    } 
    
    public char[] getBlock(Double blockNr){
        int blocknr = blockNr.intValue();
        char[] block = new char[32];
        int j = 0;
        for(int i = blocknr*32; i < (blockNr+1)*32 - 1; i++){
            block[j] = data[i]; j++;
        }
        return block;
    }
    
    public void clearBlock(Double blockNr){
        int blocknr = blockNr.intValue();
        for(int i = blocknr*32; i <= (blockNr+1)*32 - 1; i++){
            data[i] = ' ';
        }
    }
        
}
