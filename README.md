# DemoBlaz-AutoTesting
# E-Commerce Website Test Automation Framework DemoBlaze

## Overview

This project implements a comprehensive automated testing framework for the DemoBlaze e-commerce website using Selenium WebDriver with Java. The framework follows industry best practices including the Page Object Model (POM) design pattern, data-driven testing, and detailed HTML reporting with ExtentReports.

**Project Highlights:**
- **Total Test Cases:** 112 test cases written and executed
- **Coverage:** Core e-commerce functionalities (login, product search, cart, checkout)
- **Framework:** Selenium WebDriver with Java and JUnit 5
- **Design Pattern:** Page Object Model (POM)
- **Reporting:** ExtentReports with screenshots and detailed logging

## Technology Stack

- **Programming Language:** Java
- **Testing Framework:** JUnit 5
- **Automation Tool:** Selenium WebDriver
- **Build Tool:** Maven
- **Reporting:** ExtentReports 5.1.1
- **Browser:** Chrome (with WebDriverManager for driver management)
- **IDE:** Eclipse/IntelliJ IDEA

Here’s your **file structure formatted in clean Markdown with comments**:

```markdown
### Project Structure

```

├── test/
│   └── java/
│       ├── pages/                  # Page Object classes
│       │   ├── modals/             # Modal dialog page objects
│       │   │   ├── AboutUsModal.java
│       │   │   ├── ContactModal.java
│       │   │   ├── LoginModal.java
│       │   │   └── SignUpModal.java
│       │   ├── CartPage.java
│       │   └── HomePage.java
│       │
│       ├── tests.base/             # Test classes
│       │   ├── BaseTest.java
│       │   ├── CategoryTest.java
│       │   ├── CheckoutTest.java
│       │   ├── ContactTest.java
│       │   ├── LoginTest.java
│       │   ├── LogoutTest.java
│       │   ├── NavigationTest.java
│       │   ├── OrderConfirmationTest.java
│       │   ├── PerformanceTest.java
│       │   ├── ProductDetailsTest.java
│       │   ├── ProductTest.java
│       │   ├── RegistrationTest.java
│       │   └── UserJourneyTest.java
│       │
│       └── utils/                  # Utility classes
│           ├── DriverManager.java
│           ├── ExtentReportManager.java
│           └── TestData.java
│
├── target/                         # Compiled output
├── test-output/                    # ExtentReport output
│   └── ExtentReport.html
│
├── pom.xml                          # Maven dependencies
└── README.md

```
```

## Key Features

1. **Modular Framework Design:**
   - Separation of test logic from page interactions
   - Centralized test data management
   - Reusable components and methods

2. **Robust Test Methods:**
   - Explicit and implicit waits for better synchronization
   - JavaScript executor for reliable element interactions
   - Multiple verification points with fallback strategies

3. **Comprehensive Reporting:**
   - Detailed HTML reports with ExtentReports
   - Step-by-step test execution logs
   - Screenshots at critical test points
   - Failure analysis with error messages and stack traces

4. **Test Coverage:**
   - User authentication (login/logout)
   - Navigation and UI elements
   - Product search and filtering
   - Shopping cart operations
   - Checkout process
   - Order confirmation

## Setup Instructions

### Prerequisites

- Java JDK 11 or higher installed
- Maven installed
- Chrome browser installed

Here’s your text properly formatted as Markdown (`.md`):

````markdown
### Installation Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Ree323/demoblazetests.git
   cd ecommerce-test-automation
````

2. **Install dependencies:**

   ```bash
   mvn clean install
   ```

3. **Update test data (if needed):**

   * Open `test/java/utils/TestData.java`.
   * Modify test credentials or URLs if required.

---

### How to Run the Tests

#### Running All Tests

```bash
mvn test
```

#### Running Specific Test Classes

```bash
mvn test -Dtest=LogoutTest
```

#### Running Specific Test Methods

```bash
mvn test -Dtest=LogoutTest#testLogoutEndsSession
```

---

### Viewing Test Reports

After test execution, open the HTML report at:

```
test-output/ExtentReport.html
```

The report includes:

* Test summary with pass/fail statistics.
* Detailed test steps with timestamps.
* Screenshots at key points.
* Error logs for failed tests.

---

### Test Case Highlights

Our **102 test cases** cover the following key scenarios:

#### User Authentication:

* Valid and invalid login attempts.
* User registration.
* Logout functionality and session management.

#### Navigation Testing:

* Header and footer navigation.
* Menu category navigation.
* Logo navigation to homepage.

#### Product Interaction:

* Product search and filtering.
* Product details page validation.
* Product image and information verification.

#### Shopping Cart:

* Add to cart functionality.
* Cart item management (update quantity, remove item).
* Cart persistence across sessions.

#### Checkout Process:

* Shipping information validation.
* Payment method selection.
* Order placement.
* Order confirmation.

---

### Future Enhancements

* Parallel test execution.
* Cross-browser testing.
* API testing integration.
* Performance metrics collection.
* CI/CD integration.
