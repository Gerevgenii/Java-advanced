package info.kgeorgiy.ja.gerasimov.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * My tests for my Bank. These tests check for:
 * <p> - the correct creation of several {@link Person}'s and interaction with their name, surname and passport
 * <p> - the correct use and filling of {@link RemoteAccount}
 * <p> - the correct use of {@link LocalPerson}
 * <p> - the correct use of {@link LocalAccount}
 * <p> - the correct launch of my bank via {@link Client#main}
 * <p> Also, each of these tests creates its own {@link RemoteBank}
 */
public class BankTests {
    private Bank bank;
    @BeforeEach
    void fillBank() throws MalformedURLException, NotBoundException, RemoteException {
        Server.main();
        bank =  (Bank) Naming.lookup("//localhost/bank");
    }

    @Test
    void correctlyCreatePerson() throws RemoteException {
        final Person person = bank.createPerson("Arcadii", "Paravozov", 666);
        final Person person2 = bank.createPerson("ArcadiiTheBest", "ParavozovNumber2", 999);
        Assertions.assertEquals("Arcadii", person.name());
        Assertions.assertEquals("Paravozov", person.surname());
        Assertions.assertEquals(666, person.passport());
        Assertions.assertEquals("ParavozovNumber2", person2.surname());
    }

    @Test
    void correctlyCreateAccounts() throws RemoteException {
        final Person person1 = bank.createPerson("Arcadii", "Paravozov", 666);
        bank.createPerson("ArcadiiTheBest", "ParavozovNumber2", 999);

        final Account account1 = person1.createAccount("11");
        final Account account2 = person1.createAccount("12");
        final Account account3 = person1.createAccount("13");
        account1.setAmount(500);
        account2.setAmount(1324);

        Assertions.assertEquals(0, account3.getAmount());
        Assertions.assertEquals(1324, account2.getAmount());
        Assertions.assertEquals(500, account1.getAmount());
        account2.setAmount(51);
        Assertions.assertEquals(51, account2.getAmount());
    }

    @Test
    void correctlyLocalPerson() throws RemoteException {
        final Person person = bank.createPerson("Arcadii", "Paravozov", 666);
        person.createAccount("11");
        final Person localPerson = bank.getPerson(person.passport(), PersonType.LOCAL_PERSON);
        localPerson.createAccount("22");
        Assertions.assertNotNull(person.account("11"));
        Assertions.assertNull(person.account("22"));
    }

    @Test
    void correctlyLocalAccount() throws RemoteException {
        final Person person = bank.createPerson("Arcadii", "Paravozov", 666);
        final Account account1 = person.createAccount("11");
        account1.setAmount(500);
        final Person localPerson = bank.getPerson(person.passport(), PersonType.LOCAL_PERSON);
        final Account account2 = localPerson.account("11");

        Assertions.assertEquals(500, account2.getAmount());
        account2.setAmount(1000);
        Assertions.assertEquals(1000, account2.getAmount());
        Assertions.assertEquals(500, account1.getAmount());
    }

    @Test
    void correctlyBankPerson() throws RemoteException {
        Client.main("Arcadii", "Paravozov", "666", "11", "500");
        final Person person = bank.getPerson(666, PersonType.REMOTE_PERSON);
        final Account account = person.account("11");

        Assertions.assertEquals(500, account.getAmount());
        Assertions.assertEquals("Arcadii", person.name());
        Assertions.assertEquals("Paravozov", person.surname());
        Assertions.assertEquals(666, person.passport());

    }

}
