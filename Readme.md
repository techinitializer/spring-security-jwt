# Spring Boot JWT Authentication with Refresh Tokens

This project demonstrates a simple Spring Boot application with JWT-based authentication, including:

- **User Authentication:**
    - Handles user login and registration (basic implementation).
    - Generates JWT access tokens and refresh tokens.
- **JWT Authentication Filter:**
    - Intercepts incoming requests and validates JWT tokens.
    - Authenticates users based on valid JWTs.
- **Refresh Token Handling:**
    - Allows users to refresh their access tokens without re-authentication.
    - Implements basic refresh token storage and validation.
- **Logout Functionality:**
    - Revokes all refresh tokens associated with a user upon logout.
- **Security Configuration:**
    - Configures Spring Security with JWT authentication, authorization rules, and exception handling.

**Key Features:**

- **Secure Authentication:** Uses JWTs for secure authentication and authorization.
- **Refresh Token Support:** Enables users to extend their sessions without re-authentication.
- **Logout Functionality:** Revokes refresh tokens upon logout.
- **Basic Security Measures:** Includes basic security measures like CSRF protection and password encoding.