package sql;

import entities.alternative.Alternative;
import entities.alternative.AlternativeRevision;
import lombok.extern.log4j.Log4j2;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import yahooapi.YahooStockAPI;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class EntityAlternativeImpl {

    public List<Alternative> getAll() {
        List<Alternative> alternative = new ArrayList<>();
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            alternative = session.createQuery("FROM Alternative", Alternative.class).getResultList();
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }
        return alternative;
    }

    public boolean add(Alternative alternative) {
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            Alternative s = session.createQuery("FROM Alternative WHERE symbol=:symbol", Alternative.class)
                    .setParameter("symbol", alternative.getSymbol()).uniqueResult();
            if (s == null) {
                Transaction transaction = session.beginTransaction();
                session.save(alternative);
                transaction.commit();
                session.close();
                return true;
            }
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }


    public boolean updateAll() {
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {

            YahooStockAPI yahooStockAPI = new YahooStockAPI();
            Transaction transaction = session.beginTransaction();
            getAll().forEach(alternative -> {
                Alternative s = yahooStockAPI.getAlternative(alternative.getSymbol());
                Alternative alternativeToUpdate = session.load(Alternative.class, alternative.getId());
                alternativeToUpdate.setChange(s.getChange());
                alternativeToUpdate.setPrice(s.getPrice());
                session.save(alternativeToUpdate);
            });

            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    public boolean delete(Alternative alternative) {
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(alternative);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    public List<AlternativeRevision> getAudit(Alternative alternative) {
        List<AlternativeRevision> revisions = new ArrayList<>();
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            AuditQuery query = AuditReaderFactory.get(session)
                    .createQuery()
                    .forRevisionsOfEntity(Alternative.class, false, true)
                    .add(AuditEntity.id().eq(alternative.getId()));
            ArrayList<Object[]> list = (ArrayList) query.getResultList();
            list.forEach(object -> {
                    Object[] triplet = object;
                    Alternative entity = (Alternative) triplet[0];
                    DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) triplet[1];
                    RevisionType revisionType = (RevisionType) triplet[2];
                    revisions.add(new AlternativeRevision(entity, revisionEntity.getRevisionDate(), revisionType));
            });
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }

        return revisions;
    }
}
