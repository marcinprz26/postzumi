# Postzumi rest service
## Description
A simple webservice issuing REST API sharing posts from external API.
The serice collects data cyclically, once a day, or at the user's request.
Used external public API: [https://jsonplaceholder.typicode.com/posts](https://jsonplaceholder.typicode.com/posts)

## Tech stack
- Java 11
- Spring Boot
- Hibrnate
- Maven
- Mockito + JUnit
- PostgreSQL

## App run
To run in command line, ensure you have java 11 installed and indicated to *$JAVA_HOME*
For application start execute command:
`./mvnw spring-boot:run`
The application should be able to access the postgres database. 
Credentials and port of connection are available in *application.properties* file.
For example:
To run postgresql container with application credentials run:
`docker run --name credit-postgres -d -p 5432:5432 -e POSTGRES_PASSWORD=creditpostgres postgres:latest`

## API Endpoints
Application is enable on [http://localhost:8081](http://localhost:8081)
### Get all posts
Update and return all available posts
Method: GET
```
localhost:8081/posts
```
### Get posts by title
Return list of posts with given title
Method: GET
```
localhost:8081/posts/{title}
e.g. localhost:8081/posts/qui est esse
```

### Get all posts with no userId field
Update and return all available posts
Method: GET
```
localhost:8081/posts/noUser
```
### Update single post
Return updated post
Method: PUT
Body:
    title: some title
    body: some body
```
localhost:8081/posts/{postID}
e.g. localhost:8081/posts/1
```

### Delete single post
Delete selected post
Method: DELETE
```
localhost:8081/posts/{postID}
e.g. localhost:8081/posts/1
```
