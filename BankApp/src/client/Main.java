package client;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;

import utils.Constants;

public class Main {


	public static void main(String[] args) throws SocketException {
		
		

        String menu = "----------------------------------------------------------------\n" +
                "Please choose a service by typing [1-8]:\n" +
                "1: Open a new bank account\n" +
                "2: Query information from a bank account\n" +
                "3: Deposit to a bank account\n" +
                "4: Withdraw from a bank account\n" +
                "5: Monitor update from other accounts\n" +
                "6: Pay monthly maintenance fee from a bank account\n" +
                "7: Close a bank account\n" +
                "8: Print the menu\n" +
                "0: Stop the client\n";
        

       ClientServices clientService = new ClientServices(new Client());
       DatagramSocket socket = new DatagramSocket(new InetSocketAddress(Constants.CLIENT_IP, Constants.CLIENT_PORT));
       clientService.client.openSocketConnection(socket);
       
      

        System.out.print(menu);
        boolean endProgramme = false;
        Scanner scan = new Scanner(System.in);
        
        while (!endProgramme) {
        System.out.print("Enter any number: ");
        int num = scan.nextInt();
        
        try {
            switch (num) {
                case 1:
                	clientService.openBankAccount();
                    break;
                case 2:
                	clientService.runQueryService();
                    break;
                case 3:
                	clientService.runDepositService("Deposit");
                    break;
                case 4:
                	clientService.runDepositService("Withdrawal");
                    break;
                case 5:
                	clientService.runMonitorService(Constants.CLIENT_IP, Constants.CLIENT_PORT);
                    break;
                case 6:
                	clientService.runMaintenanceService();
                    break;
                case 7:
                	clientService.runCloseAccountService();
                    break;
                case 8:
                    System.out.println(menu);
                    break;
                case 0:
                	endProgramme = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        } catch (Exception e) {
            System.out.println("No response received.");
        } 
        }
	}

}
