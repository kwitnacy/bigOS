package FileModule;

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
    
    public char[] getBlock(int blockNr){
        char[] block = new char[32];
        int j = 0;
        for(int i = blockNr*32; i < (blockNr+1)*32; i++){
            block[j] = data[i]; j++;
        }
        return block;
    }
    
    public int getBlockOccup(int blockNum){
        int value=0;
        for(int i=0;i<32;i++){
            if(data[blockNum*32+i] != 0) value++;
        }
        return value;
    }
    
    public void clearBlock(int blockNr){
        for(int i = blockNr*32; i <= (blockNr+1)*32; i++){
            data[i] = 0;
        }
    }
}
