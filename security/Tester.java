import javax.xml.ws.handler.LogicalHandler;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        boolean running=true;
        User_list UU=new User_list();
        Scanner userIn = new Scanner(System.in);
        while(running)
        {
            System.out.println("input shit");
            String in=userIn.next();
            switch (in)
            {
                case "login":
                    System.out.println("[SECURITY] User "+((UU.login()? "logged in.":"login failed.")));
                    break;
                case "create_acc":
                    System.out.println("[SECURITY] Creating account "+((UU.create_acc()? "success.":" failed.")));
                    break;
                case "delete_acc":
                    System.out.println("[SECURITY] Deleting account "+((UU.del_acc()? "success.":" failed.")));
                    break;
                case "logout":
                    System.out.println("[SECURITY] Logout "+((UU.logout()? "success.":" failed.")));
                    break;
                case "exit":
                    running=false;
                    break;
                case "list_users":
                    UU.list_users();
                    break;
            }
        }
    }
}
