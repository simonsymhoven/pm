package sql;

import entities.User;
import lombok.extern.log4j.Log4j2;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Log4j2
public class EntityUserImpl {

    public User get(String userName) {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            User user = session.createQuery("FROM User WHERE userName=:userName", User.class)
                    .setParameter("userName", userName).uniqueResult();
            session.close();
            return user;
        } catch (HibernateException e) {
            log.error(e);
        }
        return null;
    }

    public boolean add(User user) {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }
}
