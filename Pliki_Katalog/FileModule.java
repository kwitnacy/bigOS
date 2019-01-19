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
        
        filemanagement.write("f1", "klapki babci GITARA SIEMA pfffff meh");
        filemanagement.printFileSystem();
        filemanagement.write("f2", "elo tutaj spuderswinia");
        filemanagement.write("f1", "DUPAdupaDUPAdupaDUPAdupaDUPAdupaDUPAdupaDUPAdupa");
        filemanagement.write("f3", "wifi daje raka plodom!wifi daje raka plodom!wifi daje raka plodom!wifi daje raka plodom!");
        filemanagement.printFileSystem();
        
        String output;
        output = filemanagement.read("f3", 0, 40);
        System.out.println(output);
      
        filemanagement.delete("f2");
        filemanagement.printFileSystem();
        filemanagement.displayFCB("f3");
        filemanagement.displayFCB("f1");
    
    }
    
}
