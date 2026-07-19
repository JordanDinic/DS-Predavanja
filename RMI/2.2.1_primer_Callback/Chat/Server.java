import java.rmi.*;
import java.rmi.registry.*;

// Pokretanje chat servera i registracija servisa u RMI registry-ju.
public class Server {
    public Server(String chatName) {
        try {
            // Pravimo konkretnu implementaciju udaljenog servera.
            ChatServer cs = new ChatServerImpl();

            // Pokrecemo registry na standardnom RMI portu.
            LocateRegistry.createRegistry(1099);

            // Objavljujemo servis pod imenom chatName da klijenti mogu da ga nadju.
            Naming.rebind(chatName, cs);

            // Drzimo proces zivim da server ne bi odmah zavrsio.
            System.in.read();
        }
        catch (Exception e) {
            // U pravoj aplikaciji ovde bi trebalo prijaviti gresku.
        }
        
    }

    public static void main(String args[]) {
        // args[0] je ime pod kojim ce servis biti registrovan.
        String chatName = args[0];
        new Server(chatName);
    }
}
