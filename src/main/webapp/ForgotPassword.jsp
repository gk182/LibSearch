<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password</title>
    <link rel="stylesheet" href="css/login.css">
</head>

<body>
    <div class="container" id="forgot-password-form">
        <h2>Forgot Password</h2>
        <p>Enter your email address to receive an OTP</p>
        <form action="user" method="POST">
            <input type="hidden" name="action" value="forgot-password">
            <div class="input-container">
                <input type="email" name="email" placeholder="Email" required>
            </div>
            <button type="submit">Send OTP</button>
        </form>
        <div class="toggle">
            <p>Remembered your password? <a href="Login.jsp">Log in here</a></p>
        </div>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>
        <% if (request.getAttribute("message") != null) { %>
            <div class="success-message">
                <%= request.getAttribute("message") %>
            </div>
        <% } %>
    </div>
</body>

</html>
