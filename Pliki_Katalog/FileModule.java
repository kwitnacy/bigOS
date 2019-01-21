package filemodule;

/**
 *
 * @author Weronika Kowalska
 */
public class FileModule {

    public static void main(String[] args) {
        
        FileManagement.create("f1", "marmolada");
        FileManagement.create("f2", "marmolada");
        FileManagement.create("f3", "marmolada");
        FileManagement.create("f4", "marmolada");
        
        FileManagement.write("f1", "klapki babci GITARA SIEMA pfffff meh");
        FileManagement.printFileSystem();
        FileManagement.write("f2", "elo tutaj spuderswinia");
        FileManagement.write("f1", "DUPAdupaDUPAdupaDUPAdupaDUPAdupaDUPAdupaDUPAdupa");
        FileManagement.write("f3", "wifi daje raka plodom!wifi daje raka plodom!wifi daje raka plodom!wifi daje raka plodom!");
        FileManagement.printFileSystem();
        
        String output;
        output = FileManagement.read("f4", 0, 40);
        System.out.println(output);
      
        FileManagement.delete("f2");
        FileManagement.printFileSystem();
        FileManagement.displayFCB("f3");
        FileManagement.displayFCB("f1");
    
    }
    
}
