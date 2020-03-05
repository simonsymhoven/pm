package sql;

import entities.User;
import lombok.extern.log4j.Log4j2;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

@Log4j2
public class EntityUserImpl implements DatabaseInterface<User> {

    @Override
    public List<User> getAll() {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("FROM User", User.class).getResultList();
            session.close();
            return users;
        } catch (HibernateException e) {
            log.error(e);
        }
        return null;
    }

    @Override
    public User get(int id) {
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            session.close();
            return user;
        } catch (HibernateException e) {
            log.error(e);
        }
        return null;
    }

    @Override
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

    @Override
    public boolean add(User user) {
        Transaction transaction = null;
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public boolean update(User user) {
        Transaction transaction = null;
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(user);
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
        return false;
    }

    @Override
    public boolean delete(User user) {
        Transaction transaction = null;
        try (Session session = DatabaseFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(user);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            log.error(e);
        }
        return false;
    }


}
