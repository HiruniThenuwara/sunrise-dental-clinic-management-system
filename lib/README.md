# Library Folder

External libraries required by the project.

| File | Library | Version | Purpose |
|---|---|---|---|
| `mysql-connector-j.jar` | MySQL Connector/J | 9.1.0 | JDBC driver used by the data access layer to connect to the MySQL database |

**Note:** MySQL Connector/J is a JDBC **driver**, not an application framework.
The rest of the system uses only the standard Java SE API
(Swing for the user interface, `com.sun.net.httpserver` for the web service and
`java.net.http.HttpClient` for the client), as required by the assessment brief.

## How this jar is added to the project in Eclipse

1. Right click the project -> **Build Path** -> **Configure Build Path...**
2. Open the **Libraries** tab -> select **Classpath** -> **Add JARs...**
3. Select `SunriseDentalClinic/lib/mysql-connector-j.jar` -> **Apply and Close**

The entry is already saved in the project `.classpath` file, so importing the
project into Eclipse adds the driver automatically.

## Original download source

https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.1.0/mysql-connector-j-9.1.0.jar
