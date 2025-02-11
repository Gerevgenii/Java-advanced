package info.kgeorgiy.ja.gerasimov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * {@link Bank} for Person's money
 */
public interface Bank extends Remote {
    /**
     * Creates a new account with specified identifier if it does not already exist.
     * @param id account id
     * @return created or existing account.
     */
    Account createAccount(String id) throws RemoteException;

    /**
     * Returns account by identifier.
     * @param id account id
     * @return account with specified identifier or {@code null} if such account does not exist.
     */
    Account getAccount(String id) throws RemoteException;

    /**
     * Returns {@link Person} by passport and Local or Remote identifier.
     * @param passport {@link Person} passport.
     * @param type {@link PersonType} of returned {@link Person}.
     * @return {@link Person} with specified {@code passport} or {@code null} if such {@link Person} does not exist.
     * @throws RemoteException if some problem with the {@link Server}.
     */
    Person getPerson(int passport, PersonType type) throws RemoteException;

    /**
     * Creates a new {@link Person} with specified name, surname and passport if it does not already exist
     * @param name {@link Person}'s name.
     * @param surname {@link Person}'s surname
     * @param passport {@link Person}'s passport
     * @return created or existing {@link Person}
     * @throws RemoteException if some problem with the {@link Server}.
     */
    Person createPerson(String name, String surname, int passport) throws RemoteException;

}
