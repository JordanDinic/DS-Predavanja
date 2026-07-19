import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;

// Konkretna implementacija udaljenog chat servera.
public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {
    // Za svako korisnicko ime cuvamo callback preko kog tom klijentu
    // mozemo da isporucimo poruku.
    HashMap<String, ChatCallback> callbacks;
    
    public ChatServerImpl() throws RemoteException {
        // Export udaljenog objekta da bi RMI mogao da prima pozive nad njim.
        super();
        callbacks = new HashMap<>();
    }

    public void sendMessage(String usernameFrom, String usernameTo, String message) throws RemoteException {
        // Server salje poruku samo ako su i posiljalac i primalac registrovani.
        if (!usernameExists(usernameTo) || !usernameExists(usernameFrom)) {
            System.out.println("Wrong username.");
            return;
        }

        // Uzimamo callback primaoca i pozivamo njegovu udaljenu metodu.
        // Ovo je sustina callback mehanizma: server zove klijenta.
        ChatCallback cb = callbacks.get(usernameTo);
        cb.onMessageReceived(usernameFrom, message);
    }

    public synchronized void register(String username, ChatCallback cb) throws RemoteException {
        // Ako je ime vec zauzeto, ignorisemo novu registraciju.
        if (usernameExists(username)) return;
        System.out.println("Adding " + username + " to chat server.");
        callbacks.put(username, cb);
    }

    public synchronized void unregister(String username) throws RemoteException {
        // Ako korisnik nije prijavljen, nema sta da uklonimo.
        if (!usernameExists(username))  return;
        System.out.println("Removing " + username + " from chat server.");
        callbacks.remove(username);
    }

    // Pomocna metoda za proveru da li korisnik postoji u mapi.
    private boolean usernameExists(String username) {
        return callbacks.containsKey(username);
    }
}
