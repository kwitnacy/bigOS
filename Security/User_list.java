package Security;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User_list
{
    private List<User> UL;
    public User current_user;
    private Scanner input;
    public User_list()
    {
        input=new Scanner(System.in);
        UL= new ArrayList<User>();
        UL.add(new User(0,false,"SYSTEM","sys"));
        UL.add(new User(1,false, "admin", "admin"));
        current_user=new User(1,false,"admin","admin");
    }
    public SaID get_Said_byname(String name)
    {
        User a= find(name);
        return a.getSaid();
    }

    public boolean change_pwd(String old, String nw)
    {
        if (current_user.getPassword().equals(old))
        {
            current_user.setPassword(nw);
            System.out.println("[Security]: Password changed");
            return true;
        }
        else
        {
            System.out.println("[Security]: Wrong password.");
            return false;
        }
    }
    public boolean change_usrname(String pwd, String newusrname)
    {
        if (current_user.getPassword().equals(pwd))
        {
            if (!exists(newusrname))
            {
                current_user.setUsername(newusrname);
                System.out.println("[Security]: Username changed");
                return true;
            }
            else
            {
                System.out.println("[Security]: User with name "+ newusrname+ " already exsits!");
                return false;
            }
        }
        else
        {
            System.out.println("[Security]: Wrong password.");
            return false;
        }
    }
    public boolean login()
    {
        list_accounts();
        System.out.print("[Security]: Enter username: " );
        String name=input.next();
        System.out.print("[Security]: Enter password: " );
        String pwd=input.next();
        for (User u: UL)
        {
            if (u.getUsername().equals(name))
            {
                if (u.getPassword().equals(pwd))
                {
                    System.out.println("[Security]: Access granted");
                    if (current_user.isActive()) {
                        current_user.user_toggle();
                    }
                    current_user=u;
                    current_user.user_toggle();
                    return true;
                }
                else
                {
                    System.out.print("[Security]: Wrong password.\n");
                    return false;
                }
            }
        }
        System.out.println("[Security]: User not found.\n");
        return false;
    }

    

    public boolean create_acc(String name, String passwd) {
        System.out.print("[Security]: Repeat password: ");
        String passwd_rep = input.next();
        if (!passwd.equals(passwd_rep))
        {
            System.out.println("[Security] Passwords do not match. Exiting.");
            return false;
        }
        System.out.print("[Security]: Enter SaID: ");
        int said=input.nextInt();
        if (current_user.getSaIDValue()>said)
        {
            System.out.println("[Security]: You cannot create user with SaID higher than current.");
            return false;
        }
        else
        {
            UL.add(new User(said,false,name,passwd));
            return true;
        }

    }


    public void allusers()
    {
        for (User aUL : UL) {
            System.out.print(aUL);
        }

    }
    private User find(String username)
    {
        for (User aUL : UL) {
            if (aUL.getUsername().equals(username)) {
                return aUL;
            }
        }
        return null;
    }

    private boolean exists(String username)
    {
        for(User u: UL)
        {
            if(u.getUsername().equals(username))
                return true;
        }
        return false;
    }
 
    public boolean del_acc(String name)
    {

        if (name.equals("SYSTEM") || name.equals("ADMIN"))
        {
            System.out.print("[Security]: Cannot delete SYSTEM nor ADMIN accounts");
            return false;
        }
        System.out.print("[Security]: Enter your password to confirm operation: ");
        String password=input.next();
        if (current_user.getSaIDValue()>=1)
        {
            if (!name.equals(current_user.getUsername()))
            {
                System.out.print("[Security]: You do not have permission to delete other accounts.\n");
                return false;
            }
        }
        if (password.equals(current_user.getPassword()))
        {
            if (exists(name))
            {
                UL.remove(find(name));
                return true;
            }
            else
            {
                System.out.println("[Security]: User does not exists!");
                return false;
            }
        }
        else
        {
            System.out.println("[Security]: Wrong password");
            return false;
        }
    }

    public void list_accounts()
    {
        System.out.println("[Security]: Existing accounts:");
        for (User u: UL)
        {
            System.out.println(u.getUsername());
        }
    }

    public void user_info(String username)
    {
        for (User u: UL)
        {
            if (u.getUsername().equals(username))
            {
                System.out.println(u);
            }
        }
    }

    public boolean logout()
    {
        current_user.user_toggle();
        return true;
    }

    public void list_users()
    {
        System.out.printf("%-10s %-10s\n", "Username" , "SaID");
        for (User u: UL)
        {
            System.out.printf("%-10s %-10s\n",u.getUsername(), Integer.toString(u.getSaIDValue()));
        }
    }
}
