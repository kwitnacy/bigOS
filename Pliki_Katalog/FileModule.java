package filemodule;

/**
 *
 * @author Weronika Kowalska
 */
public class FileModule {

    public static void main(String[] args) {
        
        String data = new String("kapcie");
        String data2 = new String("bamboszki kapcioszkizesxrdctwerctvyubiw4ed5vubyinaw4se5r6y8biw4se5tfguyi7x6e5zw5xecryvubvcxzsxrdtguvyctxdrzsexrdfyvgy");
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
        filemanagement.write("f1", "DUPAdupaDUPAdupaDUPAdupaDUPAdupa");
        filemanagement.printDisk();
        filemanagement.displayRoot();
        
        String output = new String();
        output = filemanagement.read("f2", 0, 7);
        System.out.println(output);
        
    }
    
}
