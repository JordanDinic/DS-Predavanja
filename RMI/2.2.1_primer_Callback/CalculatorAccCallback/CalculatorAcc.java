import java.rmi.*;

public interface CalculatorAcc extends Remote {

    public void add(int a) throws RemoteException;
    public void sub(int a) throws RemoteException;
    public void mul(int a) throws RemoteException;
    public void div(int a) throws RemoteException;
    public int getAcc() throws RemoteException;
    public void register(Callback cb) throws RemoteException;
    public void unregister(Callback cb) throws RemoteException;
}