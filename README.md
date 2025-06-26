<body>
    <h1>Easy Shop Capstone</h1>
    <p>
        Easy Shop is an e-commerce application featuring a frontend interface, with a primary focus on developing and enhancing robust backend APIs.
    </p>
    <h2>Key Features</h2>
    <h3>Authentication & Security</h3>
    <ul>
        <li>Secure user authentication with JWT (JSON Web Tokens).</li>
        <li>User registration and login functionality.</li>
    </ul>
   <h2>Admin Capabilities</h2>
    <ul>
        <li>Manage product categories: create, update, and delete.</li>
        <li>Manage products: add, update, and delete.</li>
    <li>View all orders via <code>GET /orders/admin.</code>
            <span class="tag"> ✅ Additional Feature</span>
        </li>
        <li>Update order status via <code>PUT /orders/{orderId}/status.</code>
            <span class="tag"> ✅ Additional Feature</span>
        </li>
    </ul>
    <h2>User Capabilities</h2>
    <ul>
        <li>Browse and filter products by price, ID, and other criteria.</li>
        <li>Add products to shopping cart.</li>
        <li>View shopping cart.</li>
        <li>Update cart item quantities or remove items.</li>
        <li>Clear shopping cart manually.</li>
        <li>Create orders from cart contents (checkout).</li>
        <li>Automatically clear cart after placing an order.</li>
          <li>View own orders via <code>GET /orders</code>
            <span class="tag"> ✅ Additional Feature</span>
        </li>
        <li>Delete own orders only if status is <code>Pending</code> via <code>DELETE /orders/{orderId}</code>
            <span class="tag"> ✅ Additional Feature</span>
        </li>
        <li>Create and update profile.</li>
        <li>View own profile.</li>
    </ul>
    <div class="section">
        <h2>🗂Categories Management</h2>
        <div class="screenshot">
            <img src="./capstone-starter%20/src/Image/CatgoriesReadme.png" alt="Categories Screenshot">
        </div>
    </div>
     <div class="section">
        <h2>📦 Product Management</h2>
        <div class="screenshot">
            <img src="./capstone-starter%20/src/Image/product.png" alt="Products Screenshot">
        </div>
    </div>
    <div class="section">
        <h2>🛒 Shopping Cart Management</h2>
        <div class="screenshot">
            <img src="./capstone-starter%20/src/Image/shoppincart.png" alt="Shopping Cart Screenshot">
        </div>
    </div>
    <div class="section">
        <h2>👤 Profile Management</h2>
        <div class="screenshot">
            <img src="./capstone-starter%20/src/Image/profile.png" alt="Profile Screenshot">
        </div>
    </div>
    <div class="section">
        <h2>📑 Order Management</h2>
        <div class="screenshot">
            <img src="./capstone-starter%20/src/Image/order.png" alt="Orders Screenshot">
        </div>
    </div>
    <h2>Technologies Used</h2>
    <ul>
        <li>Java</li>
        <li>Spring Boot</li>
        <li>Spring Security with JWT</li>
        <li>RESTful API design</li>
        <li>MySQL Database</li>
    </ul>
</body>
