package info.kgeorgiy.ja.gerasimov.bank;

import java.rmi.RemoteException;

/**
 * Global {@link Person}
 */
public record RemotePerson(String name, String surname, int passport, Bank bank) implements Person {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getId(String subId) throws RemoteException {
        return passport + ":" + subId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account account(String subId) throws RemoteException {
        return bank.getAccount(getId(subId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account createAccount(String subId) throws RemoteException {
        return bank.createAccount(getId(subId));
    }
}
