package info.kgeorgiy.ja.gerasimov.bank;

import java.rmi.*;

/**
 * Interface of {@link Person}'s accounts.
 */
public interface Account extends Remote {
    /** Returns account identifier. */
    String getId() throws RemoteException;

    /** Returns amount of money in the account. */
    int getAmount() throws RemoteException;

    /** Sets amount of money in the account. */
    void setAmount(int amount) throws RemoteException;
}
