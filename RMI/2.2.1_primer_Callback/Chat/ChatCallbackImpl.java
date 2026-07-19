import java.rmi.*;
import java.rmi.server.*;

// Callback implementacija moze da bude i posebna klasa.
// Bitno je samo da zivi u klijentskom procesu i da bude exportovana.
public class ChatCallbackImpl extends UnicastRemoteObject implements ChatCallback {

    public ChatCallbackImpl() throws RemoteException {
        // Export callback objekta da bi server mogao da ga poziva preko mreze.
        super();
    }

    public void onMessageReceived(String usernameFrom, String message) throws RemoteException {
        // Kada server pozove callback, ova metoda se izvrsava na klijentskoj strani.
        System.out.println("New message received.");
        System.out.println(usernameFrom + ": " + message);
    }
}
