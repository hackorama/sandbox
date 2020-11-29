# Skore API Service

A REST API micro service using Flask for basic derived score calculations based on player data from Dota 2 public API.

Runs at `http://localhost:5000`

## REST API

URL:
  /<count>
Method:
  GET
Path Params:
  Name: count
  Type: Number
  Optional: Yes
Query Params:
  None
Response Body:
  JSON list of teams
Response Code:
  200 OK

## Running the service

### Using pipenv

```
$ pipenv install
$ python server.py
...
 * Running on http://0.0.0.0:5000/
...
```

### Using Docker

```
$ docker build -t skore_service .
$ docker run -p 5000:5000 skore_service
...
* Running on http://0.0.0.0:5000/
```

## Test the service

```
$ curl -s http://0.0.0.0:5000/ | jq
$ curl -s http://0.0.0.0:5000/3 | jq
```
