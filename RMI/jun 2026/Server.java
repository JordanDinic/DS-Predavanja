import java.rmi.registry.*;
import java.rmi.*;
import java.io.IOException;

public class Server 
{
    public static void main (String args[])
    {
        try{
            LocateRegistry.createRegistry(5000);
            EnciklopedijaImpl obj = new EnciklopedijaImpl();
            Naming.rebind("rmi://localhost:5000/" + args[0], obj);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        try{
            System.in.read();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}