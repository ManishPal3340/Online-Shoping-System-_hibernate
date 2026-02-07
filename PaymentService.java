package com.shopingsystem.service;

import com.shopingsystem.dao.PaymentDAO;
import com.shopingsystem.model.Order;
import com.shopingsystem.model.Payment;

import java.util.Date;
import java.util.List;

public class PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();

    /**
     * Process a new payment for an order
     * @param order         the order being paid
     * @param amount        total amount to be paid
     * @param method        payment method (UPI, Card, Cash, etc.)
     * @return              the created Payment object (with generated ID)
     */
    public Payment makePayment(Order order, double amount, String method) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method is required");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setPaymentMethod(method.trim().toUpperCase());
        payment.setPaymentStatus("SUCCESS");           // For console demo we assume success
        payment.setPaymentDate(new Date());

        paymentDAO.savePayment(payment);
        return payment;
    }

    /**
     * Get payment details by payment primary key
     */
    public Payment getPaymentById(long paymentId) {
        return paymentDAO.getPaymentById(paymentId);
    }

    /**
     * Get payment associated with a specific order
     * (most commonly used method in your application)
     */
    public Payment getPaymentByOrderId(long orderId) {
        return paymentDAO.getPaymentByOrderId(orderId);
    }

    /**
     * Get all payments in the system
     * → Useful for admin dashboard / reports
     */
    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }

    /**
     * Get all payments made by a specific customer
     * → Useful in customer order history
     */
    public List<Payment> getPaymentsByUser(long userId) {
        return paymentDAO.getPaymentsByUserId(userId);
    }

    /**
     * Update an existing payment record
     * (e.g. mark as FAILED, REFUNDED, update method, etc.)
     */
    public void updatePayment(Payment payment) {
        if (payment == null || payment.getPaymentId() == 0) {
            throw new IllegalArgumentException("Invalid payment object");
        }
        paymentDAO.updatePayment(payment);
    }

    /**
     * Convenience method: Mark payment as failed
     */
    public void markPaymentAsFailed(long paymentId, String reason) {
        Payment payment = getPaymentById(paymentId);
        if (payment != null) {
            payment.setPaymentStatus("FAILED");
            // You could add a failure reason field in future
            // payment.setFailureReason(reason);
            updatePayment(payment);
        }
    }

    /**
     * Convenience method: Mark payment as refunded
     */
    public void markPaymentAsRefunded(long paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (payment != null) {
            payment.setPaymentStatus("REFUNDED");
            updatePayment(payment);
        }
    }

    // -------------------------------------------------------------------------
    //  Future possible extensions (commented for now)
    // -------------------------------------------------------------------------

    /*
    public double getTotalRevenue() {
        return getAllPayments().stream()
                .filter(p -> "SUCCESS".equals(p.getPaymentStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public List<Payment> getSuccessfulPaymentsBetween(Date start, Date end) {
        // Would require date-range query in DAO
    }
    */
}