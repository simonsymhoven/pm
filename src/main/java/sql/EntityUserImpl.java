package sql;

import entities.user.User;
import lombok.extern.log4j.Log4j2;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

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

    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            users = session.createQuery("FROM User", User.class).getResultList();
            session.close();
            return users;
        } catch (HibernateException e) {
            log.error(e);
        }
        return users;
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
