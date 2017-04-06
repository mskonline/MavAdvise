# MavAdvise

An Android app for UTA Advising among students and professors

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

You need to install the following list of software in your system

```
Java 7
Maven
Eclipse IDE (Mars+)
Android Studio
Git
Tomcat Server 7
MySQL
```

### Installing

* Step 1 - Checkout the project

Checkout the src directory into your system. **src/App/MavAdvise** is the Android project. **src/Server/MavAdvise** is the Java Web project

* Step 2 - Open the Server code in Eclipse IDE

	- Open your Eclipse IDE
	- Select the Workspace as **src/Server**
	- File -> Import -> Existing project into workspace -> Select Root directory as **src/Server/MavAdvise**
	- Create a Tomcat 7 server in the servers tab
	- Add the project into the server

* Step 3 - Open the Android app code in Android Studio

	- Open your Android Studio
	- File ->Open project. Select **src/App/MavAdvise**
	
## Deployment

* Step 1 - Maven Build of the Server

Navigate to **src/Server/MavAdvise** and run the following in the command line

```
mvn clean install
```

This will generate the WAR file in the  **src/Server/MavAdvise/target** folder, which can be deployed in Tomcat server

* Step 2 - Run the Android App

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Gurleen Kaur** - [GurleenKaur793](https://github.com/gurleenkaur793) 
* **Remesh Sreemoolam Venkitachalam** - [RemeshSV](https://github.com/remeshsv)
* **Sai Kumar Manakan** - [MSKOnline](https://github.com/mskonline)