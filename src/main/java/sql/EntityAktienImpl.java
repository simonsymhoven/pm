package sql;

import YahooAPI.YahooStockAPI;
import entities.Stock;
import lombok.extern.log4j.Log4j2;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

@Log4j2
public class EntityAktienImpl implements DatabaseInterface<Stock> {
    @Override
    public List<Stock> getAll() {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            List<Stock> aktien = session.createQuery("FROM Aktien", Stock.class).getResultList();
            session.close();
            return aktien;
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
            Stock s = session.createQuery("FROM Aktien WHERE symbol=:symbol", Stock.class)
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
        return false;
    }

    @Override
    public boolean updateAll() {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {

            YahooStockAPI yahooStockAPI = new YahooStockAPI();
            Transaction transaction = session.beginTransaction();
            getAll().forEach(aktie -> {
                Stock stock = yahooStockAPI.getStock(aktie.getSymbol());
                log.info(stock.getExchange());
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
        return false;
    }
}
