\# VIP Pass Scanner



An Android application designed for fast and reliable VIP pass verification at events.



\## Features



\- QR/Barcode scanning for VIP passes

\- Firebase Authentication for staff login

\- Firebase Firestore for pass verification and data management

\- Scan history

\- Staff profile management

\- Success confirmation with sound

\- Simple and user-friendly interface



\## Tech Stack



\- \*\*Language:\*\* Java

\- \*\*Platform:\*\* Android

\- \*\*UI:\*\* XML

\- \*\*Authentication:\*\* Firebase Authentication

\- \*\*Database:\*\* Firebase Firestore

\- \*\*Barcode Scanning:\*\* Google ML Kit

\- \*\*Camera:\*\* CameraX

\- \*\*Networking:\*\* Retrofit

\- \*\*Animations:\*\* Lottie



\## Requirements



\- Android Studio

\- Android SDK 36

\- Minimum Android version: API 26

\- Java 11



\## Setup



1\. Clone this repository.

2\. Open the project in Android Studio.

3\. Create/configure a Firebase project.

4\. Add your Firebase Android configuration as:



&#x20;  `app/google-services.json`

Create your own Firebase project and replace app/google-services.json with the configuration file downloaded from your Firebase project.

5\. Sync Gradle.

6\. Build and run the application.



> `google-services.json` is intentionally excluded from this repository because it contains project-specific Firebase configuration.



\## Project Structure



```text

app/

├── src/main/java/

│   └── com/nikhil/vippassscanner/

│       ├── activities/

│       ├── adapters/

│       └── models/

├── src/main/res/

└── build.gradle.kts

