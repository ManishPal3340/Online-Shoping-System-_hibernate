package com.shopingsystem.service;

import com.shopingsystem.dao.OrderDAO;
import com.shopingsystem.dao.ProductDAO;
import com.shopingsystem.model.Order;
import com.shopingsystem.model.OrderDetail;
import com.shopingsystem.model.Product;
import com.shopingsystem.model.User;

import java.util.Date;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public Order placeOrder(User user, List<OrderDetail> orderDetails) {
        if (user == null || orderDetails == null || orderDetails.isEmpty()) {
            throw new IllegalArgumentException("Invalid order data");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setOrderDetails(orderDetails);

        // Link back-reference (bidirectional)
        for (OrderDetail detail : orderDetails) {
            detail.setOrder(order);

            // Reduce stock
            Product product = detail.getProduct();
            if (product.getStock() < detail.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            product.setStock(product.getStock() - detail.getQuantity());
            productDAO.updateProduct(product);
        }

        orderDAO.createOrder(order);
        return order;
    }

    public Order getOrderById(long orderId) {
        return orderDAO.getOrderById(orderId);
    }

    public List<Order> getOrdersByUserId(long userId) {
        return orderDAO.getOrdersByUserId(userId);
    }

    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    public void updateOrder(Order order) {
        orderDAO.updateOrder(order);
    }

    public void deleteOrder(long orderId) {
        orderDAO.deleteOrder(orderId);
    }
}