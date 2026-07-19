import java.rmi.*;
import java.util.Scanner;

// Klijentska aplikacija.
public class Client {
    
    // Callback objekat koji klijent daje serveru.
    private ChatCallback cb;

    // Referenca na udaljeni chat server.
    private ChatServer cs;

    // Korisnicko ime pod kojim je ovaj klijent prijavljen.
    private String username;

    public Client(String chatName, String username) { 
        try {
            // Pronalazimo server po imenu u RMI registry-ju.
            cs = (ChatServer) Naming.lookup("rmi://localhost:1099/" + chatName);

            // Pravimo callback objekat koji server moze da poziva.
            // Callback vise nije unutrasnja klasa, vec poseban exportovan objekat.
            cb = new ChatCallbackImpl();

            // Registrujemo se na serveru i ostavljamo callback referencu.
            cs.register(username, cb);
            this.username = username;
        } catch (Exception e) {
            // U pravoj aplikaciji ovde bi trebalo prijaviti gresku.
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                // Jednostavan meni: "s" salje novu poruku, sve ostalo gasi klijenta.
                System.out.println("Enter 's' for sending a new message.\nEnter anything else to exit.");
                String input = scanner.nextLine();
                if (!input.equals("s")) break;

                // Unos primaoca i same poruke.
                System.out.println("Enter receiver's username: ");
                String usernameTo = scanner.nextLine();
                System.out.println("Enter message: ");
                String message = scanner.nextLine();

                // Udaljeni poziv serveru; server ce zatim pozvati callback primaoca.
                cs.sendMessage(username, usernameTo, message);
            }

            // Pri izlasku se odjavljujemo da server ne cuva zastarelu referencu.
            cs.unregister(username);
        } catch (Exception e) {
            // U pravoj aplikaciji ovde bi trebalo prijaviti gresku.
        }

        scanner.close();
    }

    public static void main(String[] args) {
        // args[0] = ime servisa u registry-ju, args[1] = korisnicko ime.
        String chatName = args[0];
        String username = args[1];

        new Client(chatName, username).run();
    }
}
