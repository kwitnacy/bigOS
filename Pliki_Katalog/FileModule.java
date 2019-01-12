package filemodule;

/**
 *
 * @author Weronika Kowalska
 */
public class FileModule {

    public static void main(String[] args) {
        
        String data = new String("kapcie");
        String data2 = new String("bamboszki kapcioszkizesxrdcttxdrzsexrdfyvgy");
        FileManagement filemanagement = new FileManagement();
        filemanagement.create("f1", "marmolada");
        filemanagement.create("f2", "marmolada");
        filemanagement.create("f3", "marmolada");
        
        filemanagement.displayFreeBlocks();

        filemanagement.write("f1", data);
        filemanagement.write("f1", "batiBat");
        filemanagement.printDisk();
        filemanagement.displayFreeBlocks();
        filemanagement.write("f2", data2);
        filemanagement.write("f3", "DUPAdupaDUPAdupaDUPAdupaDUPAdupa");
        filemanagement.printDisk();
        filemanagement.displayRoot();
        
        String output = new String();
        output = filemanagement.readFile("f3");
        
    }
    
}
