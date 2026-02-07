package com.shopingsystem.dao;

import com.shopingsystem.config.HibernateConfig;
import com.shopingsystem.model.Product;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ProductDAO {

    public void saveProduct(Product product) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(product);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Failed to save product", e);
        }
    }

    public Product getProductById(long id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.get(Product.class, id);
        }
    }

    public List<Product> getAllProducts() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("FROM Product", Product.class).list();
        }
    }

    public void updateProduct(Product product) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(product);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Failed to update product", e);
        }
    }

    public void deleteProduct(long id) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Product product = session.get(Product.class, id);
            if (product != null) {
                session.remove(product);
                tx.commit();
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    // Bonus: search by name (partial match)
    public List<Product> searchProductsByName(String namePart) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Query<Product> query = session.createQuery(
                "FROM Product WHERE name LIKE :name", Product.class);
            query.setParameter("name", "%" + namePart + "%");
            return query.list();
        }
    }
}