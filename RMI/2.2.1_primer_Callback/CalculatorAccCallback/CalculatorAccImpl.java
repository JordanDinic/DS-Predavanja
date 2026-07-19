import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;

public class CalculatorAccImpl extends UnicastRemoteObject implements CalculatorAcc  {

    private ArrayList<Callback> clients = new ArrayList<Callback>();
    private int acc;

    protected CalculatorAccImpl() throws RemoteException {
        super();
        acc = 0;
        //TODO Auto-generated constructor stub
    }

    @Override
    public void add(int a) throws RemoteException {
        acc += a;
        callCallbacks();
    }

    @Override
    public void sub(int a) throws RemoteException {
        acc -= a;
        callCallbacks();
    }

    @Override
    public void mul(int a) throws RemoteException {
        acc *= a;
        callCallbacks();
    }

    @Override
    public void div(int a) throws RemoteException {
        acc /= a;
        callCallbacks();
    }

    @Override
    public int getAcc() throws RemoteException {
        return acc;
    }

    @Override
    public synchronized void register(Callback cb) throws RemoteException {
        clients.add(cb);
    }

    @Override
    public synchronized void unregister(Callback cb) throws RemoteException {
        clients.remove(cb);
    }

    private void callCallbacks() {
        try {
            for (Callback cb : clients) {
                cb.accChanged();
            }
        } catch (Exception e) {

        }
    }
}
