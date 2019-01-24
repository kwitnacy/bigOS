package Security;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.omg.CORBA.FieldNameHelper;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Access_Control_Matrix
{

    private Table<SaID,String,Integer> ACM= HashBasedTable.create();
    private Scanner sc=new Scanner(System.in);
    public Access_Control_Matrix()
    {}
//tego nie zrobie dopoki nie bede wiedzial jak jest parsowane

    public boolean check_permission(String input,SaID c_user, String filename){
        switch (input)
        {
            case "df":
                if (ACM.get(c_user,filename)%2!=0)
                {
                    delete_obj(filename);
                    System.out.println("[Security]: User can delete this file.");
                    return true;
                }
                else {
                    System.out.println(ACM.get(c_user,filename));
                    System.out.println("[Security]: Delete not allowed for this user.");
                    return false;
                }
            case "cf":
                if (add2ACM(filename,c_user))
                {
                    System.out.println("[Security]: File added to ACM.");
                    return true;
                }
                else
                {
                    System.out.println("[Security]: Adding file error.");
                    return false;
                }
            case "wf":
                String value=bintotxt(ACM.get(c_user,filename));
                value=bintotxt(ACM.get(c_user,filename));
                if (value.contains("W"))
                {
                    System.out.println("[Security]: User can write to this file.");
                    return true;
                }
                else
                {
                    System.out.println("[Security]: User cannot write to this file.");
                    return false;
                }
            case "rf": //read
                if(bintotxt(ACM.get(c_user,filename)).contains("R"))
                {
                    System.out.println("[Security]: User can read this file.");
                }
                else
                {
                    System.out.println("[Security]: User does not have permission to read this file.");
                }

        }

        return false;
    } //read f1(100), write f1(010\110), del f1(111\001)

    private String bintotxt(int input) ///to już done
    {
        String txt=Integer.toBinaryString(input);
        String x="";
        while (txt.length()<3)
        {
            x="0"+txt;
            txt=x;
        }
        String result="";
        if (txt.charAt(0)=='0')
        {
            result+="-";
        }
        else //if (txt.charAt(0)=='1')
        {
            result+="R";
        }
        if (txt.charAt(1)=='0')
        {
            result+="-";
        }
        else
        {
            result+="W";
        }
        if (txt.charAt(2)=='0')
        {
            result+="-";
        }
        else
        {
            result+='D';
        }
        return result;
    }

    public void my_ACL(SaID a)                  //done
    {
        if (ACM.size()==0)
        {
            System.out.println("[Security]: ACL Empty.");
        }
        Map<String, Integer> per=ACM.row(a);
        System.out.printf("%-10s %-10s\n", "Object-name" , "Access");
        for (Map.Entry<String,Integer> entry:per.entrySet())
        {
            System.out.printf("%-10s %-10s\n", entry.getKey(), bintotxt(entry.getValue()));
        }
    }
    public void view_ACM()
    {
        if (ACM.size()==0)
        {
            System.out.println("[Security]: ACM Empty.");
        }
        else {
            System.out.printf("%-10s %-10s %-10s\n", "SaID", "Object-name", "Access");
            for (int i = 0; i < 5; i++) {
                Map<String, Integer> perm = ACM.row(SaID.fromInt(i));
                for (Map.Entry<String, Integer> group_entry : perm.entrySet()) {
                    System.out.printf("%-10d %-10s %-10s\n", i, group_entry.getKey(), bintotxt(group_entry.getValue()));
                }
            }
        }
    }
    public void dir_l(Map<String,String> x) //pierwszy name jutro username
    {
        String s="";
        for(Map.Entry<String,String> entry:x.entrySet())
        {
            Map<SaID, Integer> temp=ACM.column(entry.getKey());
            s="";
            s+=bintotxt(temp.get(SaID.HIGH));
            s+=bintotxt(temp.get(SaID.NORMAL));

            System.out.printf("%-10s %-10s %-10s\n", s,entry.getKey(), entry.getValue());
        }
    }

    private String permission_set(String in) {
        String REGEX = "[0|1]{3}";
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(in);
        if (in.length() == 3) {
            if (m.find()) {
                return m.group(0);
            }
        }
        System.out.println("[Security]: Input error.");
        return "ERR";
    }


    public boolean edit_ACM(SaID a){
        if (a.getValue()>=1)
        {
            System.out.println("[Security]: Access denied.");
            return false;
        }
        else
        {
            System.out.print("[Security]: Which object you wish to change?");
            String input=sc.nextLine();
            String cc= input.substring(0,2);
            System.out.println(cc);
            String p_result= "";
            int buf=0;
            if (ACM.containsColumn(cc)) //pliki dwuznakowe
            {
                for (int i=0; i<5;i++) {
                    do {
                        System.out.println("[Security]: Enter set of permissions to " + Integer.toString(i) + " group.");
                        System.out.print("[RWD]: ");
                        input = sc.nextLine();
                        p_result = permission_set(input);
                    }while(p_result.equals("ERR"));
                    buf=Integer.parseInt(input,2);
                    System.out.println("[Security]: Permission value for SaID "+Integer.toString(i)+":" + Integer.toString(buf));
                    ACM.put(SaID.fromInt(i),cc,buf);
                }
            }
            else
            {
                System.out.println("[Security]: Entry does not exsists.");
                return false;
            }
        }
        return false;

    }

    //READ WRITE DELETE
    public boolean add2ACM(String name,SaID a)
    {

        String input="";
        String cc= name.substring(0,2);
        String p_result="";
        if (ACM.containsColumn(cc))
        {
            System.out.println("[Security]: That file already exists in ACM.");
            return false;
        }
        else
        {

            System.out.println("[Security]: Enter permission value for lower group [RWD]: ");
            do {
                input = sc.nextLine();
                p_result = permission_set(input);
            }while(p_result.equals("ERR")); //111
            int buf=Integer.parseInt(p_result,2);
            if (a.getValue()==0) //(0)SYSTEM 111, <HIGH,LOW>-001, UNTRUSTED-000
            {
                ACM.put(SaID.SYSTEM, cc,7);
                for (int i=1;i<4;i++)
                {
                    ACM.put(SaID.fromInt(i),cc,buf);
                }
                ACM.put(SaID.UNTRUSTED,cc,0);
                return true;
            }
            else if (a.getValue()<4) //(1) <HIGH,x,LOW>   x=111      >x=111      <x=100
            {
                for(int i=0; i<=a.getValue();i++)
                {
                    ACM.put(SaID.fromInt(i),cc,7);
                }
                for(int i=a.getValue()+1; i<4; i++){
                    ACM.put(SaID.fromInt(i),cc,buf);
                }
                ACM.put(SaID.UNTRUSTED,cc,0);
                return true;
            }
            else //(2)UNTRUSTED-111-> ALL-111
            {
                for (int i=0;i<5;i++)
                {
                    ACM.put(SaID.fromInt(i), cc,7);
                }
                return true;
            }
        }
    }
    public boolean delete_obj(String name){
        String cc=name.substring(0,2);
        if (ACM.containsColumn(cc)) {
            ACM.column(cc).clear();
            return true;
        }
        else
        {
            System.out.println("[Security]: File does not exist!");
            return false;
        }
    }

}
