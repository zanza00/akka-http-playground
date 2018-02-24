# Drone Service

My experiment in understanding RESTful in scala :crystal_ball: :space_invader:

## How to run the service

Clone the repository:

```bash
❯ git clone https://github.com/zanza00/akka-http-playground.git
```

Run the service:

```bash
❯ sbt run
```

The service runs on port 5000 by default.

## Docker

This repo can be run with Docker, first you need to build

```bash
❯ docker build . -t drone-service
```

Once the build it's finished, you can run it using the tag

```bash
❯ docker run -d -p 5000:5000 drone-service
```

This exposes the webserver on port 5000 for consistency

## Usage

The service use one endpoint `/drones` for all of it's operations

For the rest of this guide I will be using [HTTPie](https://httpie.org/) because I like it :sunglasses:

### Create or update a drone

#### Create a new drone

To create or update the drone use __POST__
The first time that a drones _ID_ is passed the status is always _OUT_

Request:

```bash
❯ http -v POST localhost:5000/drones/1
```

Response:

```text
POST /drones/1 HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 0
Host: localhost:5000
User-Agent: HTTPie/0.9.9

HTTP/1.1 200 OK
Content-Length: 23
Content-Type: application/json
Date: Sat, 24 Feb 2018 12:21:43 GMT
Server: akka-http/10.0.6

{
    "id": 1,
    "status": "OUT"
}
```

#### Update a drone

Any subsequent call at the same _ID_ will flip the status between _IN_ and _OUT_

Response:

```text
POST /drones/1 HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 0
Host: localhost:5000
User-Agent: HTTPie/0.9.9

HTTP/1.1 200 OK
Content-Length: 22
Content-Type: application/json
Date: Sat, 24 Feb 2018 12:41:34 GMT
Server: akka-http/10.0.6

{
    "id": 1,
    "status": "IN"
}
```

#### Update Errors

Please note that the _ID_ is of type _Int_.
Attempting to call with anything else will result in an error `406 Not Acceptable`.

Request:

```bash
❯ http -v POST localhost:5000/drones/not-a-valid-id
```

Response:

```test
POST /drones/not-a-valid-id HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 0
Host: localhost:5000
User-Agent: HTTPie/0.9.9

HTTP/1.1 406 Not Acceptable
Content-Length: 22
Content-Type: application/json
Date: Sat, 24 Feb 2018 12:50:05 GMT
Server: akka-http/10.0.6

{
    "error": "invalid ID"
}
```

### Get a drone status

#### Get a specific drone status

It's possible to get the status of any drone by using __GET__.

Request:

```bash
> http -v localhost:5000/drones/1
```

Response:

```text
GET /drones/1 HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Host: localhost:5000
User-Agent: HTTPie/0.9.9

HTTP/1.1 200 OK
Content-Length: 22
Content-Type: application/json
Date: Sat, 24 Feb 2018 12:45:07 GMT
Server: akka-http/10.0.6

{
    "id": 1,
    "status": "IN"
}

```

If the drone it's not found the service will respond accordingly

Request:

```bash
> http -v localhost:5000/drones/1000
```

Response:

```text
GET /drones/1000 HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Host: localhost:5000
User-Agent: HTTPie/0.9.9

HTTP/1.1 404 Not Found
Content-Length: 0
Content-Type: application/json
Date: Sat, 24 Feb 2018 12:48:38 GMT
Server: akka-http/10.0.6
```

#### Get Errors

If it's called with an incorrect _ID_ (i.e. not an _Int_) the same response as [POST](#Update-Errors) will be given.

### Get all drones

To get a list of all the currently known drone simply __GET__ without any _ID_

Request:

```bash
❯ http -v localhost:5000/drones/
```

Response:

```text
GET /drones/ HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Host: localhost:5000
User-Agent: HTTPie/0.9.9


HTTP/1.1 200 OK
Content-Length: 47
Content-Type: application/json
Date: Sat, 24 Feb 2018 13:04:03 GMT
Server: akka-http/10.0.6

[
    {
        "id": 3,
        "status": "OUT"
    },
    {
        "id": 1,
        "status": "IN"
    }
]
```

## Todo

- [x] Working service
- [x] Unit testing
- [ ] Using enums for status
- [ ] Force update a drone
- [ ] Persist change in SQLite
- [ ] Integration Testing
- [ ] Create a simple frontend for easier testing

## Credits

- __Marco F.__ for his collection of [web-frameworks-templates](https://github.com/mfirry/web-frameworks-templates) in scala that helped me to decide wich framework to use.
- __Daniela Sfregola__ for her awesome [tutorial](https://github.com/DanielaSfregola/quiz-management-service) and [series of articles](https://danielasfregola.com/2016/02/07/how-to-build-a-rest-api-with-akka-http/) about REST in scala. This repo is based (more like copied) on her work.
- Everyone in __Scala Italy__ [slack](https://slack.scala-italy.it/) for all of my questions, you guys rocks
