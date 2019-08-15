Requirements:
- Java 8
- Maven 3
- Available http ports
    - for server 8090
    - for client 8080
    - or change accordingly at application.properties of corresponding projects

For build and tests running:
- mvn clean install

For run:
- server
    mvn -pl server spring-boot:run
- client
    mvn -pl client spring-boot:run

API:
- server
    - GET: http://localhost:8090/api/v1/documents/{key}
    - GET: http://localhost:8090/api/v1/keys?tokens=tag1,tag2..
    - POST: http://localhost:8090/api/v1/documents
        - Body:
            {
              "key":"key1",
              "document":"tag1 tag2"
            }
- client
    - GET: http://localhost:8080/client/documents/{key}
    - GET: http://localhost:8080/client/search?tokens=tag1,tag2..
    - POST: http://localhost:8080/client/documents
        - Body:
            {
              "key":"key1",
              "document":"tag1 tag2"
            }


Total spent time: ~8h
Comments: keys and tokens are case-sensitive

