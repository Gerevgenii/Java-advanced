package info.kgeorgiy.ja.gerasimov.bank;

import java.io.Serializable;
import java.util.Map;

/**
 * Local {@link Person}
 */
public record LocalPerson(String name, String surname, int passport, Map<String, Account> accounts) implements Person, Serializable {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getId(String subId) {
        return passport + ":" + subId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account account(String subId) {
        return accounts.get(getId(subId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account createAccount(String subId) {
        final Account account = new LocalAccount(getId(subId));
        accounts.put(getId(subId), account);
        return account;
    }
}
