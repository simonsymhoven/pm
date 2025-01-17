package sql;

import entities.alternative.Alternative;
import entities.client.Client;
import entities.client.ClientRevision;
import entities.stock.Stock;
import lombok.extern.log4j.Log4j2;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class EntityClientImpl {


    public List<Client> getAll() {
        List<Client> clients = new ArrayList<>();
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            clients = session.createQuery("FROM Client", Client.class).getResultList();
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }
        return clients;
    }

    public boolean add(Client client) {
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(client);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    public boolean delete(Client client) {
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(client);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    public List<ClientRevision> getAudit(Client client) {
        List<ClientRevision> revisions = new ArrayList<>();
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            AuditQuery query = AuditReaderFactory.get(session)
                    .createQuery()
                    .forRevisionsOfEntity(Client.class, false, false)
                    .add(AuditEntity.id().eq(client.getId()));
            query.add(AuditEntity.property("symbol").eq(client.getSymbol()));

            ArrayList<Object[]> list = (ArrayList) query.getResultList();

            list.forEach(object -> {
                Client entity = (Client) object[0];
                log.info("Client Audit: " + entity.getCapital());
                DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) object[1];
                RevisionType revisionType = (RevisionType) object[2];
                revisions.add(new ClientRevision(entity, revisionEntity.getRevisionDate(), revisionType));
            });
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }

        return revisions;
    }

    public boolean update(Client client) {
        Transaction transaction = null;
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(client);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error(e);
        }
        return false;
    }

    public boolean removeStock(Client client, Stock stock) {
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Client clientToUpdate = session.load(Client.class, client.getId());
            clientToUpdate.getStocks().remove(stock);
            session.save(clientToUpdate);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    public boolean removeAlternative(Client client, Alternative alternative) {
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Client clientToUpdate = session.load(Client.class, client.getId());
            clientToUpdate.getAlternatives().remove(alternative);
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
