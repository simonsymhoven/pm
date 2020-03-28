package sql;

import entities.client.client_stock.ClientStock;
import entities.client.Client;
import entities.portfolio.PortfolioStockRevision;
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
public class EntityPortfolioStockImpl {

    public List<ClientStock> getAllForStock(Stock stock) {
        List<ClientStock> clientStocks = new ArrayList<>();
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            clientStocks = session.createQuery("FROM Client_Stock WHERE stock_id=:stock_id", ClientStock.class)
                    .setParameter("stock_id", stock.getId()).getResultList();
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }
        return clientStocks;
    }

    public List<ClientStock> getAll(Client client) {
        List<ClientStock> clientStocks = new ArrayList<>();
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            clientStocks = session.createQuery("FROM Client_Stock WHERE client_id=:client_id", ClientStock.class)
                    .setParameter("client_id", client.getId()).getResultList();
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }
        return clientStocks;
    }

    public List<ClientStock> getAll() {
        List<ClientStock> clientStocks = new ArrayList<>();
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            clientStocks = session.createQuery("FROM Client_Stock", ClientStock.class).getResultList();
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }
        return clientStocks;
    }

    public void update(ClientStock clientStock) {
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            log.info("ClientStock ID to update: " + clientStock.getId());
            session.saveOrUpdate(clientStock);
            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }
    }

    public List<PortfolioStockRevision> getAudit(Client client) {
        List<PortfolioStockRevision> revisions = new ArrayList<>();
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            AuditQuery query = AuditReaderFactory.get(session)
                    .createQuery()
                    .forRevisionsOfEntity(ClientStock.class, false, true)
                    .add(AuditEntity.property("client_id").eq(client.getId()));

            ArrayList<Object[]> list = (ArrayList) query.getResultList();

            list.forEach(object -> {
                ClientStock clientStock = (ClientStock) object[0];
                DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) object[1];
                RevisionType revisionType = (RevisionType) object[2];

                revisions.add(new PortfolioStockRevision(clientStock, revisionEntity.getRevisionDate(), revisionType));
            });
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }

        return revisions;
    }
}
