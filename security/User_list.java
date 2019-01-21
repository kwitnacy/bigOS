// TODO: 22.01.2019 input protection __IMPORTANTO__ 


import java.util.*;

public class User_list
{
    private List<User> UL;
    private User current_user;
    private Scanner input;
    public User_list()
    {
        input=new Scanner(System.in);
        UL= new ArrayList<User>();
        UL.add(new User(0,false,"SYSTEM","sys"));
        UL.add(new User(1,false, "admin", "admin"));
        UL.add(new User(4,false, "Dummy" ,""));
        current_user = UL.get(1);
        current_user.user_toggle();
    }

    public boolean login()
    {
        System.out.print("Enter username: " );
        String name=input.next();
        System.out.print("Enter password: " );
        String pwd=input.next();
        for (User u: UL)
        {
            if (u.getUsername().equals(name))
            {
                if (u.getPassword().equals(pwd))
                {
                    System.out.println("Access granted");
                    if (current_user.isActive()) {
                        current_user.user_toggle();
                    }
                    current_user=u;
                    current_user.user_toggle();
                    return true;
                }
                else
                {
                    System.out.print("Wrong password.\n");
                    return false;
                }
            }
        }
        System.out.println("User not found.\n");
        return false;
    }

    

    public boolean create_acc() {
        System.out.print("Enter new user name: ");
        String name = input.next();
        System.out.print("Enter password: ");
        String passwd = input.next();
        System.out.print("Repeat password: ");
        String passwd_rep = input.next();
        if (!passwd.equals(passwd_rep))
        {
            System.out.println("Passwords do not match. Exiting.");
            return false;
        }
        System.out.print("Enter SaID: ");
        while(true)
        {
            int said=input.nextInt();
            if (current_user.getSaIDValue()>said)
            {
                System.out.println("You cannot create user with SaID higher than current.");
                System.out.print("Enter correct SaID: ");
            }
            else
            {
                UL.add(new User(said,false,name,passwd));
                return true;
            }
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
 
    public boolean del_acc() 
    {
        System.out.print("Enter username to delete: ");
        String name= input.next();
        System.out.print("Enter your password: ");
        String password=input.next();
        if (current_user.getSaIDValue()>1)
        {
            if (!name.equals(current_user.getUsername()))
            {
                System.out.print("You do not have permission to delete other accounts.\n");
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
                System.out.println("User does not exists!");
                return false;
            }
        }
        else
        {
            System.out.println("Wrong password");
            return false;
        }
    }

    private void list_accounts()
    {
        System.out.println("Accounts:");
        for (User u: UL)
        {
            System.out.println(u.getUsername());
        }
    }

    public void user_info()
    {
        System.out.println("Current user info:");
        System.out.println(current_user);
    }

    public boolean logout()
    {
        current_user.user_toggle();
        list_accounts();
        return login();
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
