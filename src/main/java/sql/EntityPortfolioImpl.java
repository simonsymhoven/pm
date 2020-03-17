package sql;

import entities.*;
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
public class EntityPortfolioImpl {

    public List<ClientStock> getAllForStock(Stock stock) {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            List<ClientStock> clientStocks = session.createQuery("FROM Client_Stock WHERE stock_id=:stock_id", ClientStock.class)
                    .setParameter("stock_id", stock.getId()).getResultList();
            session.close();
            return clientStocks;
        } catch (HibernateException e) {
            log.error(e);
        }
        return null;
    }

    public List<ClientStock> getAll(Client client) {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            List<ClientStock> clientStocks = session.createQuery("FROM Client_Stock WHERE client_id=:client_id", ClientStock.class)
                    .setParameter("client_id", client.getId()).getResultList();
            session.close();
            return clientStocks;
        } catch (HibernateException e) {
            log.error(e);
        }
        return null;
    }

    public boolean update(ClientStock clientStock) {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            log.info("ClientStock ID to update: " + clientStock.getId());
            session.saveOrUpdate(clientStock);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    public List<PortfolioRevision> getAudit(Client client){
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            AuditQuery query = AuditReaderFactory.get(session)
                    .createQuery()
                    .forRevisionsOfEntity(ClientStock.class, false , true)
                    .add(AuditEntity.property("client_id").eq(client.getId()));

            ArrayList<Object[]> list = (ArrayList) query.getResultList();

            List<PortfolioRevision> revisions = new ArrayList<>();

            list.forEach(object -> {
                Object[] triplet = object;
                ClientStock clientStock = (ClientStock) triplet[0];
                DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) triplet[1];
                RevisionType revisionType = (RevisionType) triplet[2];

                revisions.add(new PortfolioRevision(clientStock, revisionEntity.getRevisionDate(), revisionType));
            });

            return revisions;
        } catch (HibernateException e) {
            log.error(e);
        }

        return null;
    }
}
