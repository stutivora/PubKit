[![Build Status](https://travis-ci.org/narup/PubKit.svg?branch=master)](https://travis-ci.org/narup/PubKit)

# PubKit
PubKit is a lightweight, easy to setup and easy to use generic messaging and push notification platform for mobile apps. See PubKit in action at <a href="https://pubkit.co">http://pubkit.co</a>. For messaging it supports MQTT and PubKit's custom lightweight protocol PKMP. 

#Features
* Lightweight and easy setup
* Configure multiple apps
* Messaging API for real-time messaging needs
* Push notification (GCM and APNS) 
* Admin console for tracking users, messages and other stats
* SDKs for iOS and Android

#Credits:
- Moquette - <a href="https://github.com/andsel/moquette">https://github.com/andsel/moquette</a>
for coder and decoder implementation of MQTT messages.
<br/>
- JavaApns - <a href="https://github.com/notnoop/java-apns">https://github.com/notnoop/java-apns</a> for APNS
<br>
- MapDB - MapDB for in memory message storage.

#Quick Start
Requires Java 1.6+ and Maven 3+

```
git clone https://github.com/narup/PubKit.git
cd PubKit/Server
mvn clean package
mvn spring-boot:run
```
#Roadmap
* MQTT Support
* Plugin support
* Metrics and Dashboard
* iOS and Android SDK with samples
* JavaScript SDK
