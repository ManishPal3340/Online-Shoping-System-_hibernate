package com.shopingsystem;

import com.shopingsystem.model.*;
import com.shopingsystem.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        UserService userService = new UserService();
        ProductService productService = new ProductService();
        OrderService orderService = new OrderService();
        PaymentService paymentService = new PaymentService();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = getIntInput(sc);

            switch (choice) {
                case 1:
                    registerUser(sc, userService);
                    break;
                case 2:
                    login(sc, userService, productService, orderService, paymentService);
                    break;
                case 0:
                    System.out.println("\nThank you for using the Shopping System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        sc.close();
    }

    // ────────────────────────────────────────────────
    // MAIN MENU
    // ────────────────────────────────────────────────
    private static void printMainMenu() {
        System.out.println("\n=====================================");
        System.out.println("      WELCOME TO SHOPPING SYSTEM      ");
        System.out.println("=====================================");
        System.out.println(" 1. Register new user");
        System.out.println(" 2. Login");
        System.out.println(" 0. Exit");
        System.out.print("Enter choice: ");
    }

    // ────────────────────────────────────────────────
    // REGISTER
    // ────────────────────────────────────────────────
    private static void registerUser(Scanner sc, UserService userService) {
        System.out.println("\n--- Register ---");
        System.out.print("Username: "); String username = sc.nextLine().trim();
        System.out.print("Password: "); String password = sc.nextLine().trim();
        System.out.print("Email: ");     String email    = sc.nextLine().trim();
        System.out.print("Role (CUSTOMER/ADMIN): "); String role = sc.nextLine().trim().toUpperCase();

        if (!role.equals("CUSTOMER") && !role.equals("ADMIN")) {
            System.out.println("Invalid role. Only CUSTOMER or ADMIN allowed.");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(role);

        try {
            userService.registerUser(user);
            System.out.println("Registration successful!");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // LOGIN
    // ────────────────────────────────────────────────
    private static void login(Scanner sc, UserService us, ProductService ps,
                              OrderService os, PaymentService pays) {
        System.out.println("\n--- Login ---");
        System.out.print("Username: "); String username = sc.nextLine().trim();
        System.out.print("Password: "); String password = sc.nextLine().trim();

        User user = us.login(username, password);
        if (user == null) {
            System.out.println("Login failed. Invalid username or password.");
            return;
        }

        System.out.println("Login successful! Welcome, " + user.getUsername() + " (" + user.getRole() + ")");

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            adminMenu(sc, user, ps, os, pays, us);
        } else {
            customerMenu(sc, user, ps, os, pays);
        }
    }

    // ────────────────────────────────────────────────
    // CUSTOMER MENU
    // ────────────────────────────────────────────────
    private static void customerMenu(Scanner sc, User user,
                                     ProductService ps, OrderService os, PaymentService pays) {
        while (true) {
            System.out.println("\n=== CUSTOMER MENU ===");
            System.out.println("1. View all products");
            System.out.println("2. Place order (Add / Update / Remove from cart)");
            System.out.println("3. View my orders & payments");
            System.out.println("0. Logout");
            System.out.print("Choice → ");

            int ch = getIntInput(sc);
            switch (ch) {
                case 1 :displayAllProducts(ps);
                case 2 : manageCartAndPlaceOrder(sc, user, ps, os, pays);
                case 3 :viewMyOrders(user, os, pays);
                case 0 :{
                    System.out.println("Logging out...");
                    return;
                }
                default : System.out.println("Invalid choice.");
            }
        }
    }

    // ────────────────────────────────────────────────
    // CART MANAGEMENT + PLACE ORDER (Customer)
    // ────────────────────────────────────────────────
    private static void manageCartAndPlaceOrder(Scanner sc, User user,
                                                ProductService ps, OrderService os, PaymentService pays) {
        List<OrderDetail> cart = new ArrayList<>();

        System.out.println("\n=== Build Your Cart ===");
        displayAllProducts(ps);

        while (true) {
            showCart(cart);
            System.out.print("\nCommands: a = add, u = update qty, r = remove, c = checkout, q = quit\n→ ");
            String cmd = sc.nextLine().trim().toLowerCase();

            if (cmd.equals("q")) {
                System.out.println("Cart discarded.");
                return;
            }

            if (cmd.equals("c")) {
                if (cart.isEmpty()) {
                    System.out.println("Cart is empty.");
                    continue;
                }
                break; // proceed to checkout
            }

            if (cmd.equals("a")) {
                System.out.print("Product ID: ");
                long pid = getLongInput(sc);
                Product p = ps.getProductById(pid);
                if (p == null) {
                    System.out.println("Product not found.");
                    continue;
                }

                System.out.printf("Selected: %s | ₹%.2f | Stock: %d\n", p.getName(), p.getPrice(), p.getStock());
                System.out.print("Quantity: ");
                long qty = getLongInput(sc);

                if (qty < 1 || qty > p.getStock()) {
                    System.out.println("Invalid quantity (1 to " + p.getStock() + ")");
                    continue;
                }

                OrderDetail od = new OrderDetail();
                od.setProduct(p);
                od.setQuantity(qty);
                cart.add(od);
                System.out.println("Added to cart.");
            }
            else if (cmd.equals("u")) {
                if (cart.isEmpty()) {
                    System.out.println("Cart is empty.");
                    continue;
                }
                showCart(cart);
                System.out.print("Item number to update: ");
                int idx = getIntInput(sc) - 1;
                if (idx < 0 || idx >= cart.size()) {
                    System.out.println("Invalid item number.");
                    continue;
                }

                OrderDetail item = cart.get(idx);
                System.out.printf("Current: %dx %s\n", item.getQuantity(), item.getProduct().getName());
                System.out.print("New quantity (0 = remove): ");
                long newQty = getLongInput(sc);

                if (newQty == 0) {
                    cart.remove(idx);
                    System.out.println("Item removed.");
                } else if (newQty > 0 && newQty <= item.getProduct().getStock()) {
                    item.setQuantity(newQty);
                    System.out.println("Quantity updated.");
                } else {
                    System.out.println("Invalid quantity.");
                }
            }
            else if (cmd.equals("r")) {
                if (cart.isEmpty()) {
                    System.out.println("Cart is empty.");
                    continue;
                }
                showCart(cart);
                System.out.print("Item number to remove: ");
                int idx = getIntInput(sc) - 1;
                if (idx >= 0 && idx < cart.size()) {
                    cart.remove(idx);
                    System.out.println("Item removed.");
                } else {
                    System.out.println("Invalid item number.");
                }
            }
        }

        // Checkout
        double total = 0;
        for (OrderDetail d : cart) {
            total += d.getProduct().getPrice() * d.getQuantity();
        }

        System.out.println("\nFinal Cart Summary:");
        showCart(cart);

        System.out.printf("\nGrand Total: ₹%.2f\n", total);
        System.out.print("Confirm order? (yes/no): ");
        if (!sc.nextLine().trim().toLowerCase().startsWith("y")) {
            System.out.println("Order cancelled.");
            return;
        }

        try {
            Order order = os.placeOrder(user, cart);
            System.out.print("Payment method (UPI/CARD/CASH): ");
            String method = sc.nextLine().trim().toUpperCase();
            pays.makePayment(order, total, method);

            System.out.println("\nOrder placed successfully!");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.printf("Total: ₹%.2f | Paid via: %s\n", total, method);
        } catch (Exception e) {
            System.out.println("Order failed: " + e.getMessage());
        }
    }

    private static void showCart(List<OrderDetail> cart) {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        System.out.println("\nCurrent Cart:");
        double total = 0;
        for (int i = 0; i < cart.size(); i++) {
            OrderDetail d = cart.get(i);
            double itemTotal = d.getQuantity() * d.getProduct().getPrice();
            System.out.printf("%2d. %dx %-20s @ ₹%.2f = ₹%.2f\n",
                    i + 1, d.getQuantity(), d.getProduct().getName(), d.getProduct().getPrice(), itemTotal);
            total += itemTotal;
        }
        System.out.printf("Total: ₹%.2f\n", total);
    }

    // ────────────────────────────────────────────────
    // VIEW MY ORDERS (unchanged)
    // ────────────────────────────────────────────────
    private static void viewMyOrders(User user, OrderService os, PaymentService pays) {
        List<Order> orders = os.getOrdersByUserId(user.getUserId());
        if (orders.isEmpty()) {
            System.out.println("You have no orders yet.");
            return;
        }

        System.out.println("\n=== Your Orders ===");
        for (Order o : orders) {
            System.out.printf("Order #%d | %s | Items: %d\n",
                    o.getOrderId(), o.getOrderDate(), o.getOrderDetails().size());

            Payment p = pays.getPaymentByOrderId(o.getOrderId());
            if (p != null) {
                System.out.printf("   → %s | ₹%.2f | %s\n",
                        p.getPaymentMethod(), p.getAmount(), p.getPaymentStatus());
            }
            System.out.println("─".repeat(50));
        }
    }

    // ────────────────────────────────────────────────
    // ADMIN MENU (full product CRUD)
    // ────────────────────────────────────────────────
    private static void adminMenu(Scanner sc, User admin,
                                  ProductService ps, OrderService os,
                                  PaymentService pays, UserService us) {
        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View all products");
            System.out.println("2. Add new product");
            System.out.println("3. Update product");
            System.out.println("4. Delete product");
            System.out.println("5. View all users");
            System.out.println("6. View all orders & payments");
            System.out.println("0. Logout");
            System.out.print("Choice → ");

            int ch = getIntInput(sc);
            switch (ch) {
                case 1 : displayAllProducts(ps);
                case 2 : addProduct(sc, ps);
                case 3 : updateProduct(sc, ps);
                case 4 : deleteProduct(sc, ps);
                case 5 : viewAllUsers(us);
                case 6 : viewAllOrders(os, pays);
                case 0 : {
                    System.out.println("Logging out...");
                    return;
                }
                default : System.out.println("Invalid choice.");
            }
        }
    }

    // ────────────────────────────────────────────────
    // PRODUCT CRUD METHODS (ADMIN)
    // ────────────────────────────────────────────────
    private static void addProduct(Scanner sc, ProductService ps) {
        System.out.println("\n--- Add Product ---");
        System.out.print("Name : ");  String name  = sc.nextLine().trim();
        System.out.print("Price: ");  double price = getDoubleInput(sc);
        System.out.print("Stock: ");  long stock  = getLongInput(sc);

        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setStock(stock);

        ps.addProduct(p);
        System.out.println("Product added! ID = " + p.getProductId());
    }

    private static void updateProduct(Scanner sc, ProductService ps) {
        System.out.print("Product ID to update: ");
        long id = getLongInput(sc);

        Product p = ps.getProductById(id);
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.println("Current → " + p.getName() + " | ₹" + p.getPrice() + " | Stock: " + p.getStock());

        System.out.print("New name (empty = keep): ");     String n = sc.nextLine().trim();
        if (!n.isEmpty()) p.setName(n);

        System.out.print("New price (empty = keep): ");    String pr = sc.nextLine().trim();
        if (!pr.isEmpty()) try { p.setPrice(Double.parseDouble(pr)); } catch (Exception ignored) {}

        System.out.print("New stock (empty = keep): ");    String st = sc.nextLine().trim();
        if (!st.isEmpty()) try { p.setStock(Long.parseLong(st)); } catch (Exception ignored) {}

        ps.updateProduct(p);
        System.out.println("Product updated.");
    }

    private static void deleteProduct(Scanner sc, ProductService ps) {
        System.out.print("Product ID to delete: ");
        long id = getLongInput(sc);
        ps.deleteProduct(id);
        System.out.println("Delete attempted.");
    }

    // ────────────────────────────────────────────────
    // OTHER ADMIN VIEWS (unchanged)
    // ────────────────────────────────────────────────
    private static void viewAllUsers(UserService us) { /* your existing code */ }
    private static void viewAllOrders(OrderService os, PaymentService pays) { /* your existing code */ }

    // ────────────────────────────────────────────────
    // HELPER METHODS
    // ────────────────────────────────────────────────
    private static void displayAllProducts(ProductService ps) {
        List<Product> products = ps.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.println("\n=== Available Products ===");
        System.out.println("ID    Name                  Price    Stock");
        System.out.println("--------------------------------------------");
        for (Product p : products) {
            System.out.printf("%-5d %-20s ₹%-7.2f %d%n",
                    p.getProductId(), p.getName(), p.getPrice(), p.getStock());
        }
    }

    private static int getIntInput(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return -1;
        }
    }

    private static long getLongInput(Scanner sc) {
        try {
            return Long.parseLong(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return -1;
        }
    }

    private static double getDoubleInput(Scanner sc) {
        try {
            return Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return -1;
        }
    }
}