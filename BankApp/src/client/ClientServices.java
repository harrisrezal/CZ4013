package client;

/*
 * Client Services
 *
 *
 * */

import request.*;
import response.*;
import utils.Constants;
import utils.Currency;
import utils.ReadInputs;

import java.time.Duration;
import java.util.Optional;


public class ClientServices {


    public static Client client;

    public ClientServices(Client client) {
        ClientServices.client = client;
    }


    public void openBankAccount() throws IllegalAccessException {
        System.out.println("Please input the following information to open an account");
        String name = this.askName();
        String password = this.askPassword();
        Currency currency = this.askCurrency();
        Double balance = this.askAmount();

        ResponseMessage resp = client.sendRequest("OpenAccount", new OpenAccountRequest(name, password, currency, balance));
        if (resp != null) {
            OpenAccountResponse openResp = (OpenAccountResponse) resp.obj;

            System.out.println("\n[Client] Thank you for opening up an account with us");
            System.out.println("************************************************************");
            System.out.println("[Client] Your account number is : " + openResp.accountNumber);
            System.out.println("************************************************************");
        }


    }

    public void runQueryService() throws IllegalAccessException {
        System.out.println("Please input the following information to query from an account");
        int accountNumber = this.askAccountNumber();
        String password = this.askPassword();

        ResponseMessage resp = client.sendRequest("QueryAccount", new QueryAccountRequest(accountNumber, password));


        if (resp != null) {
            QueryAccountResponse queryResp = (QueryAccountResponse) resp.obj;

            if (queryResp.success) {
                System.out.println("\n[Client] Account detail as requested");
                System.out.println("************************************************************");
                System.out.println("[Client] Account is under     : " + queryResp.name);
                System.out.println("[Client] Account currency is  : " + queryResp.currency.toString());
                System.out.println("[Client] Account balance is   : " + queryResp.balance);
                System.out.println("************************************************************");

            } else {

                System.out.println("\n[Client] Error retrieving your account due to the following reason ");
                System.out.println("************************************************************");
                System.out.println("[Client] : " + queryResp.errorMessage);
                System.out.println("************************************************************");
            }
        }
    }


    public void runDepositService(String transaction) throws IllegalAccessException {
        System.out.println("Please input the following information to perform a " + transaction);
        int accountNumber = this.askAccountNumber();
        String name = this.askName();
        String password = this.askPassword();
        Currency currency = this.askCurrency();
        Double amount = this.askAmount();


        ResponseMessage resp = client.sendRequest("DepositAccount", new DepositAccountRequest(name, accountNumber, password, currency, -amount));
        if (resp != null) {
            DepositAccountResponse DepositResp = (DepositAccountResponse) resp.obj;

            if (DepositResp.success) {
                System.out.println("\n[Client] Transaction Successful. Detailed as followed: ");
                System.out.println("************************************************************");
                System.out.println("[Client] Account is under     : " + DepositResp.name);
                System.out.println("[Client] Account currency is  : " + DepositResp.currency.toString());
                System.out.printf("[Client] Amount to %s is : %s", transaction, DepositResp.amount);
                System.out.println("\n[Client] Account new balance is   : " + DepositResp.balance);
                System.out.println("************************************************************");

            } else {

                System.out.println("\n[Client] Error retrieving completing your deposit transaction due to the following reason ");
                System.out.println("************************************************************");
                System.out.println("[Client] : " + DepositResp.errorMessage);
                System.out.println("************************************************************");
            }
        }

    }


    public void runMonitorService(String ipAddress, int port) throws IllegalAccessException {
        System.out.print("Monitor interval (s) = ");
        int interval = ReadInputs.safeReadInt();

        ResponseMessage resp = client.sendRequest("MonitorAccount", new MonitorAccountRequest(ipAddress, port, interval));

        if (resp != null) {
            MonitorAccountStatusResponse StatusResp = (MonitorAccountStatusResponse) resp.obj;

            if (StatusResp.success) {
                System.out.println("[Client] Successfully requested to monitor, waiting for updates...");
                System.out.println("************************************************************");
                client.poll(Duration.ofSeconds(interval));
                System.out.println("************************************************************");
                System.out.println("[Client] Finished monitoring.");

            } else {
                System.out.println("Failed to request to monitor");
            }
        }
    }

    public void runMaintenanceService() throws IllegalAccessException {
        System.out.println("Please input the following information to pay the maintenance fee");
        int accountNumber = this.askAccountNumber();
        String name = this.askName();
        String password = this.askPassword();

        ResponseMessage resp = client.sendRequest("PayMaintenanceFee", new MaintenanceFeeAccountRequest(accountNumber, name, password));
        if (resp != null) {
            MaintenanceFeeAccountResponse feeResp = (MaintenanceFeeAccountResponse) resp.obj;
            if (feeResp.success) {
                System.out.println("************************************************************");
                System.out.printf("[Client] Successfully pay maintenance fee, new balance = %f %s\n", feeResp.balance, feeResp.currency);
                System.out.println("************************************************************");
            } else {
                System.out.println("************************************************************");
                System.out.printf("[Client]Failed to pay maintenance fee with reason: %s\n", feeResp.errorMessage);
                System.out.println("************************************************************");

            }
        }

    }


    public void runCloseAccountService() throws IllegalAccessException {
        System.out.println("Please input the following information to close an account");
        int accountNumber = this.askAccountNumber();
        String name = this.askName();
        String password = this.askPassword();

        ResponseMessage resp = client.sendRequest("CloseAccount", new CloseAccountRequest(name, accountNumber, password));

        if (resp != null) {
            CloseAccountResponse closeResp = (CloseAccountResponse) resp.obj;
            if (closeResp.success) {
                System.out.println("************************************************************");
                System.out.printf("Successfully closed bank account with number = %d\n", accountNumber);
                System.out.println("************************************************************");
            } else {
                System.out.println("************************************************************");
                System.out.printf("Failed to close bank account with reason: %s\n", closeResp.errorMessage);
                System.out.println("************************************************************");

            }
        }

    }

    private String askName() {
        System.out.print("Your name = ");
        return ReadInputs.readLine();
    }

    private String askPassword() {
        System.out.printf("Your password (%d characters) = ", Constants.PASSWORD_LENGTH);
        String password = ReadInputs.readLine();
        if (password.length() != Constants.PASSWORD_LENGTH) {
            System.out.printf("Password must have exactly %d characters!\n", Constants.PASSWORD_LENGTH);
            return this.askPassword();
        } else {
            return password;
        }
    }

    private int askAccountNumber() {
        System.out.print("Your account number = ");
        return ReadInputs.safeReadInt();
    }

    private Currency askCurrency() {
        System.out.printf("Your currency choice (%s) = ", Currency.getAllCurrenciesString());
        String currency = ReadInputs.readLine().toUpperCase();
        Optional<Currency> currencyOpt = Currency.getAllCurrencies().filter((x) ->
        {
            return x.toString().equals(currency);
        }).findFirst();
        if (currencyOpt.isPresent()) {
            return currencyOpt.get();
        } else {
            System.out.println("Invalid currency code!");
            return this.askCurrency();
        }
    }

    private Double askAmount() {
        System.out.print("Amount of money = ");
        double amount = ReadInputs.safeReadDouble();
        if (amount < 0.0D) {
            System.out.println("Amount cannot be negative!");
            return this.askAmount();
        } else {
            return amount;
        }
    }

}
