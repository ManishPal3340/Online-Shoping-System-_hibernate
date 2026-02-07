package com.shopingsystem.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.shopingsystem.config.HibernateConfig;
import com.shopingsystem.model.Payment;

public class PaymentDAO {

    /**
     * Save a new payment record
     */
    public void savePayment(Payment payment) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(payment);   // Modern Hibernate recommendation (instead of save())
            transaction.commit();
            System.out.println("Payment saved successfully: " + payment.getPaymentId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            System.err.println("Failed to save payment");
        }
    }

    /**
     * Get payment by its own primary key (payment_id)
     */
    public Payment getPaymentById(long paymentId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.get(Payment.class, paymentId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get payment by associated order ID
     * (most commonly used in your current application logic)
     */
    public Payment getPaymentByOrderId(long orderId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Query<Payment> query = session.createQuery(
                "FROM Payment p WHERE p.order.orderId = :orderId",
                Payment.class
            );
            query.setParameter("orderId", orderId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all payments (useful for admin view)
     */
    public List<Payment> getAllPayments() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("FROM Payment p ORDER BY p.paymentDate DESC", Payment.class)
                         .list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // return empty list instead of null
        }
    }

    /**
     * Get all payments for a specific user (via orders)
     */
    public List<Payment> getPaymentsByUserId(long userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Query<Payment> query = session.createQuery(
                "SELECT p FROM Payment p " +
                "JOIN p.order o " +
                "WHERE o.user.userId = :userId " +
                "ORDER BY p.paymentDate DESC",
                Payment.class
            );
            query.setParameter("userId", userId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Update an existing payment (e.g. change status to FAILED, REFUNDED, etc.)
     */
    public void updatePayment(Payment payment) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(payment);   // merge() is safer for detached entities
            transaction.commit();
            System.out.println("Payment updated successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            System.err.println("Failed to update payment");
        }
    }

    /**
     * Delete a payment record (usually not needed in real systems,
     *  but added for admin completeness)
     */
    public void deletePayment(long paymentId) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Payment payment = session.get(Payment.class, paymentId);
            if (payment != null) {
                session.remove(payment);
                transaction.commit();
                System.out.println("Payment deleted: " + paymentId);
            } else {
                System.out.println("Payment not found: " + paymentId);
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            System.err.println("Failed to delete payment");
        }
    }
}