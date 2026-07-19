import java.rmi.*;

// Udaljeni interfejs servera. Klijent preko ovih metoda komunicira sa serverom.
public interface ChatServer extends Remote {
    // Klijent trazi od servera da prosledi poruku drugom korisniku.
    public void sendMessage(String usernameFrom, String usernameTo, String message) throws RemoteException;

    // Klijent se prijavljuje na server i ostavlja svoj callback objekat.
    public void register(String username, ChatCallback cb) throws RemoteException;

    // Klijent se odjavljuje kada zavrsi rad.
    public void unregister(String username) throws RemoteException;
}
