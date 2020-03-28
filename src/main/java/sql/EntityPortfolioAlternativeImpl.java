package sql;

import entities.alternative.Alternative;
import entities.client.Client;
import entities.client.client_alternative.ClientAlternative;
import entities.portfolio.PortfolioAlternativeRevision;
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
public class EntityPortfolioAlternativeImpl {

    public List<ClientAlternative> getAllForStock(Alternative alternative) {
        List<ClientAlternative> clientAlternatives = new ArrayList<>();
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            clientAlternatives = session.createQuery("FROM Client_Alternative WHERE alternative_id=:alternative_id", ClientAlternative.class)
                    .setParameter("alternative_id", alternative.getId()).getResultList();
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }
        return clientAlternatives;
    }

    public List<ClientAlternative> getAll(Client client) {
        List<ClientAlternative> clientAlternatives = new ArrayList<>();
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            clientAlternatives = session.createQuery("FROM Client_Alternative WHERE client_id=:client_id", ClientAlternative.class)
                    .setParameter("client_id", client.getId()).getResultList();
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }
        return clientAlternatives;
    }

    public List<ClientAlternative> getAll() {
        List<ClientAlternative> clientAlternatives = new ArrayList<>();
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            clientAlternatives = session.createQuery("FROM Client_Alternative", ClientAlternative.class).getResultList();
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }
        return clientAlternatives;
    }

    public void update(ClientAlternative clientAlternative) {
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(clientAlternative);
            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }
    }

    public List<PortfolioAlternativeRevision> getAudit(Client client) {
        List<PortfolioAlternativeRevision> revisions = new ArrayList<>();
        try (Session session = DatabaseFactoryUtils.getSessionFactory().openSession()) {
            AuditQuery query = AuditReaderFactory.get(session)
                    .createQuery()
                    .forRevisionsOfEntity(ClientAlternative.class, false, true)
                    .add(AuditEntity.property("client_id").eq(client.getId()));

            ArrayList<Object[]> list = (ArrayList) query.getResultList();

            list.forEach(object -> {
                ClientAlternative clientAlternative = (ClientAlternative) object[0];
                DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) object[1];
                RevisionType revisionType = (RevisionType) object[2];

                revisions.add(new PortfolioAlternativeRevision(clientAlternative, revisionEntity.getRevisionDate(), revisionType));
            });

            session.close();
        } catch (HibernateException e) {
            log.error(e);
        }

        return revisions;
    }
}
