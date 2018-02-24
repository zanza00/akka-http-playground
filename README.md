# Drone Service
My experiment in understaing RESTful in scala

## How to run the service
Clone the repository:
```
> git clone https://github.com/zanza00/akka-http-playground.git
```

Run the service:
```
> sbt run
```

The service runs on port 5000 by default.

## Docker

This repo can be run with Docker, first you need to build

```
> docker build . -t drone-service
```

Once the build it's finished, you can run it using the tag

```
> docker run -d -p 5000:5000 drone-service
```

this exposes the webserver on port 5000


## Usage

The service use one endpoint `/drones` for all of it's operations

For the rest of this guide I will be using [HTTPie](https://httpie.org/) because I like it :)

### Update a drone
Request:
```
http -v POST localhost:5000/drones/1
```
Response:


### Get a drone status


### Get all drones


## Todo

## Credits

- __Marco F.__ for his collection of [web-frameworks-templates](https://github.com/mfirry/web-frameworks-templates) in scala  
- __Daniela Sfregola__ for her awesome [tutorial](https://github.com/DanielaSfregola/quiz-management-service) and [series of articles](https://danielasfregola.com/2016/02/07/how-to-build-a-rest-api-with-akka-http/) about REST in scala. This repo is based on her work. 
