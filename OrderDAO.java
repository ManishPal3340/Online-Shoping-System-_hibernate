package com.shopingsystem.dao;

import com.shopingsystem.config.HibernateConfig;
import com.shopingsystem.model.Order;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class OrderDAO {

    public void createOrder(Order order) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(order);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Failed to create order", e);
        }
    }

    public Order getOrderById(long orderId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            // Also fetch details here if needed in detail view later
            return session.createQuery(
                    "SELECT o FROM Order o LEFT JOIN FETCH o.orderDetails WHERE o.orderId = :id",
                    Order.class)
                    .setParameter("id", orderId)
                    .uniqueResult();
        }
    }

    public List<Order> getOrdersByUserId(long userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            // This is the most important fix → JOIN FETCH
            Query<Order> query = session.createQuery(
                "SELECT DISTINCT o FROM Order o " +
                "LEFT JOIN FETCH o.orderDetails " +
                "WHERE o.user.userId = :userId " +
                "ORDER BY o.orderDate DESC",
                Order.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        }
    }

    public List<Order> getAllOrders() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            // Same fix for admin view
            return session.createQuery(
                "SELECT DISTINCT o FROM Order o " +
                "LEFT JOIN FETCH o.orderDetails " +
                "ORDER BY o.orderDate DESC",
                Order.class
            ).getResultList();
        }
    }

    public void updateOrder(Order order) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(order);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Failed to update order", e);
        }
    }

    public void deleteOrder(long orderId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Order order = session.get(Order.class, orderId);
            if (order != null) {
                session.remove(order);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Failed to delete order", e);
        }
    }
}