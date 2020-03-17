package sql;

import entities.Client;
import entities.ClientRevision;
import entities.Stock;
import lombok.extern.log4j.Log4j2;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import yahooAPI.YahooStockAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            Client clientToUpdate = session.get(Client.class, client.getId());
            clientToUpdate.setDepoValue(client.getDepoValue());
            session.update(clientToUpdate);
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

    public List<ClientRevision> getAudit(Client client){
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            AuditQuery query = AuditReaderFactory.get(session)
                    .createQuery()
                    .forRevisionsOfEntity(Client.class, false , true)
                    .add(AuditEntity.id().eq(client.getId()));

            ArrayList<Object[]> list = (ArrayList) query.getResultList();

            List<ClientRevision> revisions = new ArrayList<>();

            list.forEach(object -> {
                Object[] triplet = object;
                Client entity = (Client) triplet[0];
                DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) triplet[1];
                RevisionType revisionType = (RevisionType) triplet[2];

                revisions.add(new ClientRevision(entity, revisionEntity.getRevisionDate(), revisionType));
            });

            return revisions;
        } catch (HibernateException e) {
            log.error(e);
        }

        return null;
    }


    public boolean addStocks(Client client, List<Stock> selectedList) {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Client clientToUpdate = session.load(Client.class, client.getId());
            clientToUpdate.getStocks().addAll(selectedList);
            session.save(clientToUpdate);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    public boolean removeStocks(Client client, List<Stock> selectedList) {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Client clientToUpdate = session.load(Client.class, client.getId());
            clientToUpdate.getStocks().removeAll(selectedList);
            session.save(clientToUpdate);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }
}
