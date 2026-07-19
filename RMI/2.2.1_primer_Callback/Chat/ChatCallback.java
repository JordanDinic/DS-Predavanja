import java.rmi.*;

// Klijent izlozi ovaj interfejs serveru kako bi server mogao da ga
// "pozove nazad" kada stigne nova poruka.
public interface ChatCallback extends Remote {
    // Server poziva ovu metodu na strani klijenta.
    // usernameFrom je posiljalac, a message je tekst poruke.
    public void onMessageReceived(String usernameFrom, String message) throws RemoteException;
}
