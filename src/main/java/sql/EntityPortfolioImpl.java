package sql;

import YahooAPI.YahooStockAPI;
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
public class EntityPortfolioImpl implements DatabaseInterface<ClientStock> {
    @Override
    public List<ClientStock> getAll() {
        return null;
    }

    @Override
    public ClientStock get(int id) {
        return null;
    }

    @Override
    public ClientStock get(String symbol) {
        return null;
    }

    @Override
    public boolean add(ClientStock clientStock) { return false; }

    @Override
    public boolean update(ClientStock clientStock) {
        return false;
    }

    @Override
    public boolean updateAll() {
        return false;
    }

    @Override
    public boolean delete(ClientStock clientStock) { return false; }

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
