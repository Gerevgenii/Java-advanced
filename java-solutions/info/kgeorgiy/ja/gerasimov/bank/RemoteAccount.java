package info.kgeorgiy.ja.gerasimov.bank;

import java.rmi.RemoteException;

/**
 * Global {@link Account} for {@link RemotePerson}.
 */
public class RemoteAccount extends AbstractAccount {
    /**
     * Constructor for {@link RemoteAccount}. Create new global account with specified identifier.
     * Default {@code amount} = 0
     * @param id Special {@code id} for new {@link RemoteAccount}.
     */
    public RemoteAccount(final String id) {
        super(id);
    }
}
