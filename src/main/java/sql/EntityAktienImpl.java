package sql;

import YahooAPI.StockDTO;
import YahooAPI.YahooStockAPI;
import entities.Aktie;
import lombok.extern.log4j.Log4j2;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

@Log4j2
public class EntityAktienImpl implements DatabaseInterface<Aktie> {
    @Override
    public List<Aktie> getAll() {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            List<Aktie> aktien = session.createQuery("FROM Aktien", Aktie.class).getResultList();
            session.close();
            return aktien;
        } catch (HibernateException e) {
            log.error(e);
        }
        return null;
    }

    @Override
    public Aktie get(int id) {
        return null;
    }

    @Override
    public Aktie get(String symbol) {
        return null;
    }

    @Override
    public boolean add(Aktie aktie) {
        return false;
    }

    @Override
    public boolean update(Aktie aktie) {
        return false;
    }

    @Override
    public boolean updateAll() {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {

            YahooStockAPI yahooStockAPI = new YahooStockAPI();
            Transaction transaction = session.beginTransaction();
            getAll().forEach(aktie -> {
                StockDTO stock = yahooStockAPI.getStock(aktie.getSymbol());
                Aktie aktieToUpdate = session.load(Aktie.class, aktie.getId());
                aktieToUpdate.setChange(stock.getChange());
                aktieToUpdate.setPrice(stock.getPrice());
                session.save(aktieToUpdate);
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
    public boolean delete(Aktie aktie) {
        return false;
    }
}
