package filemodule;

/**
 *
 * @author Weronika Kowalska
 */
public class FileModule {

    public static void main(String[] args) {
        
        FileManagement filemanagement = new FileManagement();
        filemanagement.create("f1", "marmolada");
        filemanagement.create("f2", "marmolada");
        filemanagement.create("f3", "marmolada");
        
//        filemanagement.displayFreeBlocks();
//        filemanagement.printDisk();
//        filemanagement.displayRoot();
        
        filemanagement.write("f1", "klapki babci GITARA SIEMA pfffff meh");
        filemanagement.write("f2", "elo tutaj spuderswinia");
        filemanagement.write("f1", "DUPAdupaDUPAdupaDUPAdupaDUPAdupaDUPAdupaDUPAdupa");
        filemanagement.write("f3", "wifi daje raka plodom!wifi daje raka plodom!wifi daje raka plodom!wifi daje raka plodom!");
        filemanagement.printDisk();
        filemanagement.displayRoot();
        
        String output = new String();
        output = filemanagement.readFile("f1");
        
        filemanagement.delete("f2");
        filemanagement.printDisk();
        filemanagement.displayRoot();
    }
    
}
