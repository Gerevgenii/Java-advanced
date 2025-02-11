package info.kgeorgiy.ja.gerasimov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A {@link Person} who can store a lot of accounts
 */
public interface Person extends Remote {
    /**
     * Getter for {@code name}
     * @return {@link Person}'s name
     * @throws RemoteException if some problem with the {@link Server}.
     */
    String name() throws RemoteException;

    /**
     * Getter for {@code name}
     * @return {@link Person}'s surname
     * @throws RemoteException if some problem with the {@link Server}.
     */
    String surname() throws RemoteException;

    /**
     * Getter for {@code name}
     * @return {@link Person}'s passport
     * @throws RemoteException if some problem with the {@link Server}.
     */
    int passport() throws RemoteException;

    /**
     * Local function for getting full {@link Account}'s {@code id}
     * @return full {@link Account}'s {@code id}
     * @throws RemoteException if some problem with the {@link Server}.
     */
    String getId(String subId) throws RemoteException;

    /**
     * Returns account by identifier.
     * @param subId account id
     * @return account with specified identifier or {@code null} if such account does not exist.
     */
    Account account(String subId) throws RemoteException;

    /**
     * Creates a new account with specified identifier if it does not already exist.
     * @param subId account id
     * @return created or existing account.
     */
    Account createAccount(String subId) throws RemoteException;
}
