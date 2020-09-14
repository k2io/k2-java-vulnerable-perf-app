## K2 Java Vulnerable Application
A vulnerability testing web application developed by <a href='https://www.k2io.com/'>K2 Cyber Security</a> to assess it Next-Gen Java Runtime protection technology.
The application is a Spring Boot based web application using embedded H2 DB as datastore.

#### Requirements :

###### Build : 

*Platform :* `Java JDK 8 and above`

*Environment :* `Linux` with internet connectivity.

*Binaries :* `git`

###### Run :
*Platform :* `Java JRE 8 and above`

*Environment :* `Linux`



#### Setup :

###### Docker 
Containerised setup is standalone & ready to run the application with the following setups
```
docker pull k2cyber/ic-test-application:k2-java-vulnerable-perf

docker run -itd -p 8080:8080 --name k2-java-vulernable-app k2cyber/ic-test-application:k2-java-vulnerable-perf
```

###### Standard Jar
In case of simple jar based approach, the application jar needs to be compiled first & for the same, `Java JDK 8 & above` is needed as mentioned in pre-requisites.
The build & setup script will require internet connection to download necessary dependencies.

Please ensure that correct JAVA_HOME is set for the terminal session to run the script by following command :
```shell script
java -version
``` 
This should reflect a Java JDK 8 or above installation.


Following are the instructions to compile & run the application :
```shell script

git clone https://github.com/k2io/k2-java-vulnerable-perf-app
cd k2-java-vulnerable-perf-app
bash ./mvnw spring-boot:run 

```

#### Accessing the application :
The application can be accessed at the API endpoint `http://HOST_MACHINE_IP:8080/` which will show a welcome message.
Please note this application is API only application & has no UI. 

#### Documentation 
This application comes with an extensive & interactive OpenAPI 3 documentation for all its supported APIs.
The same can be found at `http://HOST_MACHINE_IP:8080/docs` .

Kindly use the above link to interact with the application via OpenAPI 3 interactive documentation. 