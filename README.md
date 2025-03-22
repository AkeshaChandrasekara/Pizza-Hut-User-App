# Pizza-Hut-User-App

#### Video Demo: [https://youtu.be/9N_pawMlpS4?si=7H9JRv3mgzxFVxln](https://youtu.be/9N_pawMlpS4?si=7H9JRv3mgzxFVxln)

#### Description:
The Pizza Hut User App is a comprehensive mobile application designed to make ordering your favorite food and beverages easier than ever. Built with a powerful Java Android frontend and a robust Firebase & SQLite backend, it ensures a seamless user experience, real-time tracking, and a hassle-free checkout process.

### Features

- **User-Friendly Interface**: The app features a clean and intuitive interface built with Java and XML layouts, following Material Design principles for a modern look and feel.
- **Real-Time Order Tracking**: Users can track their orders in real-time, ensuring they know exactly when their food will arrive.
- **Secure Payments**: Integrated with the PayHere API, the app provides a secure and reliable payment gateway for all transactions.
- **Offline Support**: Utilizing SQLite, the app offers offline functionality, allowing users to browse menus and place orders even without an internet connection.
- **Fast Image Loading**: The app uses Picasso for efficient image loading, ensuring that menu items and promotional images load quickly and smoothly.
- **Location Services**: Integrated with the Google Maps API, the app provides accurate location services for delivery tracking and store locator features.
- **Shake Detection**: An innovative shake detection feature using the device's accelerometer allows users to refresh the app's content with a simple shake gesture.

### Technologies Used

- **Frontend**: Java, XML Layouts, Material Design, Bottom Navigation, Animations
- **Backend**: Firebase Firestore, SQLite (Offline), PayHere API
- **External Libraries**: Picasso for image loading, Google Maps API for location services

### Project Structure

- **MainActivity.java**: The entry point of the application, handling the main navigation and user interface.
- **MenuFragment.java**: Displays the menu items and handles user interactions for adding items to the cart.
- **CartFragment.java**: Manages the shopping cart, allowing users to review and modify their orders before checkout.
- **OrderTrackingActivity.java**: Provides real-time tracking of orders, showing the current status and estimated delivery time.
- **PaymentActivity.java**: Handles the payment process, integrating with the PayHere API for secure transactions.
- **DatabaseHelper.java**: Manages local data storage using SQLite, ensuring offline functionality.
- **FirebaseService.java**: Handles communication with Firebase Firestore for real-time data synchronization.

### Design Choices

- **Material Design**: Chosen for its modern and user-friendly interface components, ensuring a consistent and intuitive user experience.
- **Firebase Firestore**: Selected for its real-time database capabilities, allowing for instant updates and synchronization across devices.
- **SQLite**: Implemented to provide offline support, ensuring users can access the app's features even without an internet connection.
- **PayHere API**: Chosen for its robust security features and ease of integration, providing a reliable payment solution.

### Challenges and Solutions

- **Real-Time Synchronization**: Ensuring real-time updates across devices was challenging. This was addressed by using Firebase Firestore, which provides real-time data synchronization.
- **Offline Functionality**: Implementing offline support required careful management of local data storage. SQLite was used to store essential data locally, allowing users to access the app's features offline.
- **Secure Payments**: Integrating a secure payment gateway was crucial. The PayHere API was chosen for its robust security features and ease of integration.

### Future Enhancements

- **User Authentication**: Adding user authentication to provide personalized experiences and order history.
- **Push Notifications**: Implementing push notifications to keep users informed about their order status and promotions.
- **Enhanced Analytics**: Integrating analytics to gain insights into user behavior and improve the app's features and performance.

### Conclusion

The Pizza Hut User App is a robust and user-friendly application designed to enhance the food ordering experience. With its modern interface, real-time tracking, and secure payments, it provides a seamless and enjoyable experience for users. The use of advanced technologies like Firebase, SQLite, and the PayHere API ensures that the app is both powerful and reliable.
