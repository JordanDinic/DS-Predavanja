import java.rmi.*;
import java.rmi.server.*;

public class CalculatorAccImpl extends UnicastRemoteObject implements CalculatorAcc  {

    private int acc;

    protected CalculatorAccImpl() throws RemoteException {
        super();
        acc = 0;
        //TODO Auto-generated constructor stub
    }

    @Override
    public int add(int a) throws RemoteException {
        acc += a;
        return acc;
    }

    @Override
    public int sub(int a) throws RemoteException {
        acc -= a;
        return acc;
    }

    @Override
    public int mul(int a) throws RemoteException {
        acc *= a;
        return acc;
    }

    @Override
    public int div(int a) throws RemoteException {
        acc /= a;
        return acc;
    }

    @Override
    public int getAcc() throws RemoteException {
        return acc;
    }
    
}
