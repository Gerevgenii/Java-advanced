package info.kgeorgiy.ja.gerasimov.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public final class Client {
    /** Utility class. */
    private Client() {}

    public static void main(final String... args) throws RemoteException {
        if (args.length != 5) {
            System.err.println("Write correct parameters");
            return;
        }
        final Bank bank;
        try {
            bank = (Bank) Naming.lookup("//localhost/bank");
        } catch (final NotBoundException e) {
            System.out.println("Bank is not bound");
            return;
        } catch (final MalformedURLException e) {
            System.out.println("Bank URL is invalid");
            return;
        }

        if (args[0] == null || args[1] == null || args[2] == null || args[3] == null || args[4] == null) {
            System.out.println("Write correct parameters");
        }

        final Person person = bank.createPerson(args[0], args[1], Integer.parseInt(args[2]));
        final String subId = args[3];

        Account account = person.account(subId);
        if (account == null) {
            System.out.println("Creating account");
            account = person.createAccount(subId);
        } else {
            System.out.println("Account already exists");
        }
        System.out.println("Account id: " + account.getId());
        System.out.println("Money: " + account.getAmount());
        System.out.println("Adding money");
        account.setAmount(account.getAmount() + Integer.parseInt(args[4]));
        System.out.println("Money: " + account.getAmount());
    }
}
