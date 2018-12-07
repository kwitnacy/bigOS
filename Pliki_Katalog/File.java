package filemodule;

/**
 *
 * @author Weronika Kowalska
 */
public class File {
    private String name;                    //nazwa pliku
    private String userName;                //nazwa użytkownika
    private Integer size, index;            //rozmiar pliku i index początku pliku w tablicy FAT
    private boolean read, write, delete;    //flagi oznaczające kolejno: 
                                            //plik otwrty, plik otwrty do zapisu, plik usunięty?????????????????????????????????????
 
    public File() {
        this.name = "";       //format nazwy pliku: pierwsza mała litera, potem cyfra
        this.userName = ""; //????????????
        this.size = 0;
        this.index = 0;
        this.read = false;      //nie jest w trybie odczytu
        this.write = false;     //nie jest w trybie zapisu
        this.delete = false;    //nie jest usunięty
    }

    //gettery
    public Integer getIndex() { return index; }

    public String getName() { return name; }

    public String getUserName() { return userName; }

    public Integer getSize() { return size; }

    public boolean isDelete() { return delete; }

    public boolean isRead() { return read; }

    public boolean isWrite() { return write; }

    //settery
    public void setIndex(Integer index) { this.index = index; }

    public void setName(String name) { this.name = name; }

     public void setUserName(String userName) { this.userName = userName; }
     
    public void setSize(Integer size) { this.size = size; }

    public void setDelete(boolean delete) { this.delete = delete; }
    
    public void setRead(boolean read) { this.read = read; }

    public void setWrite(boolean write) { this.write = write; }
}
