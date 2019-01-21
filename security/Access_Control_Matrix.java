// TODO: 22.01.2019 co zrobic jak program interpretera bedzie chcial na plikach dzialac.

import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;

import java.util.Map;
import java.util.Scanner;


public class Access_Control_Matrix
{
    Table<SaID,String,Integer> ACM= HashBasedTable.create();
    public Access_Control_Matrix()
    {
        ACM.put(SaID.HIGH,"text.txt",7);
        ACM.put(SaID.SYSTEM,"text.txt",7);
    }

    void my_ACL(){}
    public boolean delete_obj(){return false;}
    public boolean edit_ACM(SaID a){return false;}
    public void view_ACM(){}
    public boolean check_permission(String chyba){return false;} //read f1(100), write f1(010\110), del f1(111\001)
    private String permission_set(){return "";} //do sensownej kontroli inputu

    public boolean add2ACM(String name,SaID a)
    {
        if (ACM.containsColumn(name))
        {
            System.out.println("That file already exists in ACM.");
            return false;
        }
        else
        {
            Scanner input= new Scanner(System.in);
            System.out.println("Enter permission values: ");
            String in= input.next();
            //READ WRITE DELETE
            //(0)SYSTEM 111, <HIGH,LOW>-001, UNTRUSTED-000
            //(1) <HIGH,x,LOW>   x=111      >x=111      <x=100
            //(2)UNTRUSTED-111-> ALL-111
        }

        return true;
    }
}
