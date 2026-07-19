import java.rmi.*;

public interface CalculatorAcc extends Remote {

    public int add(int a) throws RemoteException;
    public int sub(int a) throws RemoteException;
    public int mul(int a) throws RemoteException;
    public int div(int a) throws RemoteException;
    public int getAcc() throws RemoteException;
}