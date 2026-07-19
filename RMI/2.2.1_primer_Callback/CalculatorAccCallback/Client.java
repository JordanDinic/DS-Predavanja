import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Client {
    private CalculatorAcc calculatorAcc;
    private Callback cb; 

    public Client(String objectName) {
        Scanner scanner = new Scanner(System.in);

        try {
            calculatorAcc = (CalculatorAcc) Naming.lookup("rmi://localhost:1099/" + objectName);
            cb = new CallbackImpl();
            calculatorAcc.register(cb);

            while (true) {
                System.out.println("Enter '+', '-', '*' or '/' to execute operation, or 'get' to get acc value.\nEnter anything else to end the execution of program.");
                String op = scanner.nextLine();
                
                if (!op.equals("+") && 
                    !op.equals("-") && 
                    !op.equals("*") && 
                    !op.equals("/") && 
                    !op.equals("get")) break;

                int number = 0;
                if (!op.equals("get"))  {
                    System.out.println("Enter number.");
                    number = Integer.parseInt(scanner.nextLine());
                }

                switch (op) {
                    case "+":
                        calculatorAcc.add(number);
                        break;
                    case "-":
                        calculatorAcc.sub(number);
                        break;
                    case "*":
                        calculatorAcc.mul(number);
                        break;
                    case "/":
                        calculatorAcc.div(number);
                        break;
                    case "get":
                        getAcc();
                        break;
                }
            }
            
            calculatorAcc.unregister(cb);
        }
        catch (Exception e) {
        }

        scanner.close();
    }

    public void getAcc() {
        try {
            System.out.println("acc = " + calculatorAcc.getAcc());
        }
        catch (Exception e) {

        }
    }
    
    public static void main(String args[]) {
        String objectName = args[0];
        new Client(objectName);
    }

    public class CallbackImpl extends UnicastRemoteObject implements Callback {

        protected CallbackImpl() throws RemoteException {
            super();
        }

        public void accChanged() throws RemoteException {
            getAcc();
        }

    }
}
