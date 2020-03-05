package sql;

import entities.Client;
import lombok.extern.log4j.Log4j2;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

@Log4j2
public class EntityClientImpl implements DatabaseInterface<Client> {

    @Override
    public List<Client> getAll() {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            List<Client> clients = session.createQuery("FROM Clients", Client.class).getResultList();
            session.close();
            return clients;
        } catch (HibernateException e) {
            log.error(e);
        }
        return null;
    }

    @Override
    public Client get(int id) {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            Client client = session.get(Client.class, id);
            session.close();
            return client;
        } catch (HibernateException e) {
            log.error(e);
        }
        return null;
    }

    @Override
    public Client get(String symbol) {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            Client client = session.createQuery("FROM Clients WHERE symbol=:symbol", Client.class)
                    .setParameter("symbol", symbol).uniqueResult();
            session.close();
            return client;
        } catch (HibernateException e) {
            log.error(e);
        }
        return null;
    }

    @Override
    public boolean add(Client client) {
        Transaction transaction = null;
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(client);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public boolean update(Client client) {
        Transaction transaction = null;
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(client);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public boolean updateAll() {
        return false;
    }

    @Override
    public boolean delete(Client client) {
        Transaction transaction = null;
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(client);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }


}
