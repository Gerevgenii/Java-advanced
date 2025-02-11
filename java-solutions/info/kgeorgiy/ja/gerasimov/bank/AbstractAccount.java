package info.kgeorgiy.ja.gerasimov.bank;

import java.io.Serializable;
import java.rmi.RemoteException;

public abstract class AbstractAccount implements Account {
    protected final String id;
    protected int amount;

    public AbstractAccount(String id) {
        this.id = id;
        amount = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() throws RemoteException {
        return id;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int getAmount() throws RemoteException {
        System.out.println("Getting amount of money for account " + id);
        return amount;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setAmount(final int amount) throws RemoteException {
        System.out.println("Setting amount of money for account " + id);
        this.amount = amount;
    }
}
