# Integration Tests
Tests in this package will invoke the running service to verify functionality.

Tests use rest assured but do assume a clean database. TODO add a rest assured filter to capture created reservation ids and cleanup in a `@AfterMethod`
The concurrency test will use multiple threads to make sure the application handles concurrent creates.