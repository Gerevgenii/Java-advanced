package info.kgeorgiy.ja.gerasimov.bank;

import java.io.Serializable;

/**
 * Local {@link Account} for the {@link LocalPerson}.
 */
public class LocalAccount extends AbstractAccount implements Serializable {
    /**
     * Constructor for {@link LocalAccount}. Create new local account with specified identifier.
     * Default {@code amount} = 0
     * @param id Special {@code id} for new {@link LocalAccount}.
     */
    public LocalAccount(final String id) {
        super(id);
    }

}
