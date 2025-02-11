package info.kgeorgiy.ja.gerasimov.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@inheritDoc}
 */
public class RemoteBank implements Bank {
    private final int port;
    private final ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Person> persons = new ConcurrentHashMap<>();

    /**
     * Create new {@link RemoteBank}.
     * @param port current port
     */
    public RemoteBank(final int port) {
        this.port = port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account createAccount(final String id) throws RemoteException {
        System.out.println("Creating account " + id);
        final Account account = new RemoteAccount(id);
        if (accounts.putIfAbsent(id, account) == null) {
            UnicastRemoteObject.exportObject(account, port);
            return account;
        } else {
            return getAccount(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account getAccount(final String id) {
        System.out.println("Retrieving account " + id);
        return accounts.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person getPerson(int passport, PersonType type) throws RemoteException {
        final Person person = persons.get(passport);
        return switch (type) {
            case LOCAL_PERSON -> {
                final Map<String, Account> localAccounts = new HashMap<>();
                for (Map.Entry<String, Account> account : accounts.entrySet()) {
                    final String key = account.getKey();
                    if (key.substring(
                            0,
                            key.indexOf(':')
                    ).equals(String.valueOf(passport))) {
                        final Account localAccount = new LocalAccount(account.getValue().getId());
                        localAccount.setAmount(account.getValue().getAmount());
                        localAccounts.put(account.getKey(), localAccount);
                    }
                }
                try {
                    final var x = LocalPerson.class;
                    final var y = LocalPerson.class.getConstructors();
                    final var z = new LocalPerson(person.name(), person.surname(),person.passport(), localAccounts);
                    yield new LocalPerson(person.name(), person.surname(), person.passport(), localAccounts);
                } catch (final Throwable e) {
                    throw new RuntimeException(e);
                }
            }
            case REMOTE_PERSON -> person;
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person createPerson(String name, String surname, int passport) throws RemoteException {
        final Person person = new RemotePerson(name, surname, passport, this);
        if (persons.putIfAbsent(passport, person) == null) {
            UnicastRemoteObject.exportObject(person, port);
            return person;
        } else {
            return getPerson(passport, PersonType.REMOTE_PERSON);
        }
    }
}
