package sql;

import YahooAPI.YahooStockAPI;
import entities.Client;
import entities.ClientRevision;
import entities.Stock;
import entities.StockRevision;
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
public class EntityStockImpl implements DatabaseInterface<Stock> {
    @Override
    public List<Stock> getAll() {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            List<Stock> stock = session.createQuery("FROM Stock", Stock.class).getResultList();
            session.close();
            return stock;
        } catch (HibernateException e) {
            log.error(e);
        }
        return null;
    }

    @Override
    public Stock get(int id) {
        return null;
    }

    @Override
    public Stock get(String symbol) {
        return null;
    }

    @Override
    public boolean add(Stock stock) {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            Stock s = session.createQuery("FROM Stock WHERE symbol=:symbol", Stock.class)
                    .setParameter("symbol", stock.getSymbol()).uniqueResult();
            if (s == null) {
                Transaction transaction = session.beginTransaction();
                session.save(stock);
                transaction.commit();
                session.close();
                return true;
            }
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public boolean update(Stock stock) {
        log.info("STOCK TO UPDATE ; "  + stock.getId());
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {

            YahooStockAPI yahooStockAPI = new YahooStockAPI();
            Transaction transaction = session.beginTransaction();

            Stock s = yahooStockAPI.getStock(stock.getSymbol());
            Stock stockToUpdate = session.load(Stock.class, stock.getId());
            stockToUpdate.setChange(s.getChange());
            stockToUpdate.setPrice(s.getPrice());
            session.save(stockToUpdate);

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
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {

            YahooStockAPI yahooStockAPI = new YahooStockAPI();
            Transaction transaction = session.beginTransaction();
            getAll().forEach(aktie -> {
                Stock stock = yahooStockAPI.getStock(aktie.getSymbol());
                Stock stockToUpdate = session.load(Stock.class, aktie.getId());
                stockToUpdate.setChange(stock.getChange());
                stockToUpdate.setPrice(stock.getPrice());
                session.save(stockToUpdate);
            });

            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public boolean delete(Stock stock) {
        log.info("STOCK TO DELETE ; "  + stock.getId());
        Transaction transaction = null;
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(stock);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    public List<StockRevision> getAudit(Stock stock){
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            AuditQuery query = AuditReaderFactory.get(session)
                    .createQuery()
                    .forRevisionsOfEntity(Stock.class, false , true)
                    .add(AuditEntity.id().eq(stock.getId()));

            ArrayList<Object[]> list = (ArrayList) query.getResultList();

            List<StockRevision> revisions = new ArrayList<>();

            list.forEach(object -> {
                Object[] triplet = object;
                Stock entity = (Stock) triplet[0];
                DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) triplet[1];
                RevisionType revisionType = (RevisionType) triplet[2];

                revisions.add(new StockRevision(entity, revisionEntity.getRevisionDate(), revisionType));
            });

            return revisions;
        } catch (HibernateException e) {
            log.error(e);
        }

        return null;
    }
}
