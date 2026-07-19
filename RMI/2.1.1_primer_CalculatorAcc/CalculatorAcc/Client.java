import java.net.MalformedURLException;
import java.rmi.*;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) {
        String objectName = args[0];
        Scanner scanner = new Scanner(System.in);

            CalculatorAcc calculatorAcc;
            try {
                calculatorAcc = (CalculatorAcc) Naming.lookup("rmi://localhost:1099/" + objectName);

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
                            System.out.println("acc = " + calculatorAcc.getAcc());
                            break;
                    }
                }
            }
            catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NotBoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        scanner.close();
    }
}
