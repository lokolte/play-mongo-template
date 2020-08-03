
## Play Mongo Template  
  
This repo contains a POC application based on:  

 - Play framework 2.7.3
 - Scala 2.13.1
 - MongoDB 4.2.1
 - OpenApi 3 (for docs)

### How to run the app:

There are multiple ways to run the app:

#### Method # 1 (Easiest)
1. execute `./startup.sh` (You may need to make the script executable by executing `chmod +x ./startup.sh` first)   

This will start the mongodb at `localhost:27017` and application at `localhost:9000`, use port `9005` for debugging.

Mongodb port can be changed in the `docker-compose.yml` and application/debug port can be changed in `startup.sh`.

#### Method # 2
1. Uncomment the contents of `docker-compose.yml`.
 
2. In `application.conf` change `uri:"mongodb://localhost:27017/test?authenticationMechanism=scram-sha1&rm.nbChannelsPerNode=100"` to `uri:"mongodb://mongodb:27017/test?authenticationMechanism=scram-sha1&rm.nbChannelsPerNode=100"`

3. Execute `docker-compose up`.

This will start the mongodb at `localhost:27017` and application at `localhost:9000`, use port `9005` for debugging.

All ports can be changed in the `docker-compose.yml` file.

#### Method # 3
1. Create volume:  
`docker volume create --name mongo-data`  
  
2. Run mongo:  
`docker run -d -p 27017:27017 -v mongo-data:/data/db --name mongodb mongo:4.2.1`   

3. Start application in debug mode (access service at `localhost:9000` and use port `9005` for debugging):  
`sbt -jvm-debug 9005 run`  


### API Docs
`http://localhost:9000/docs`

### Testing
1. To run tests:
   `sbt clean coverage test`
   
2. To generate coverage report:
`sbt coverageReport` Report can be viewed in the browser by opening `play-mongo-template/target/scala-2.13/scoverage-report/index.html`.