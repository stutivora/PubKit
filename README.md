# PubKit
===========================
Lightweight application platform for mobile developers

PubKit is a lightweight, easy to setup and easy to use generic messaging and push notification platform for mobile apps.

#Features
* Lightweight and easy setup
* Configure multiple apps
* Messaging API for real-time messaging needs
* Push notification (GCM and APNS) 
* Admin console for tracking messages and stats
* SDKs for iOS and Android platform to get started
* Social integration

#Credits:
<br/>
Moquette <a href="https://github.com/andsel/moquette">https://github.com/andsel/moquette</a>
for coder and decoder implementation of MQTT messages.
<br/>
Java-Apns <a href="https://github.com/notnoop/java-apns">https://github.com/notnoop/java-apns</a> for APNS
Quick Start
-----------
Requires Java1.6+ and Maven 3+

```
git clone https://github.com/narup/PubKit.git
cd PubKit/Server
mvn clean package
mvn spring-boot:run
```
#Roadmap
----
* MQTT Support
* Plugin support
* Metrics and Dashboard
* Admin
