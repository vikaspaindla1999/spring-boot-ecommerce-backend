# Spring Boot E-Commerce Backend

A complete **Spring Boot–based E-commerce backend** that provides APIs for product management, categories, user authentication, cart, orders, addresses, and role-based access control (Admin, Seller, User).

This project follows a modular structure using **Spring Boot**, **Spring Security (JWT)**, **Spring Data JPA**, **ModelMapper**, and **Swagger/OpenAPI** for API documentation.


###  Authentication & Authorization
- JWT-based login & signup
- Role-based access control  
  - **ADMIN** – Full access (categories, products, users, orders)
  - **SELLER** – Manage products
  - **USER** – Place orders, manage cart, addresses

###  Product & Category Management
- Create categories
- Add products to specific categories
- CRUD APIs for products

###  Cart Management
- Add/remove products to cart
- Update cart quantities
- View cart details with total amount

###  Order Management
- Place orders from cart
- Payment details included
- Order history
- Admin can manage orders

###  Address Management
- Add/update/delete address for users
- Assign address during checkout

###  API Documentation
- Swagger UI enabled with OpenAPI 3  
  Endpoint: `/swagger-ui/index.html`
