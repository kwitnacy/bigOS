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
}
