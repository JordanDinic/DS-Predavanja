import java.rmi.*;

public interface Callback extends Remote {

    public void accChanged() throws RemoteException;
}