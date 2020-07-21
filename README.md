# machinist
Application for managing industrial machines.

[![Build Status](https://travis-ci.com/BoardyB/machinist.svg?branch=master)](https://travis-ci.com/BoardyB/machinist) [![Coverage Status](https://coveralls.io/repos/github/BoardyB/machinist/badge.svg?branch=master&service=github)](https://coveralls.io/github/BoardyB/machinist?branch=master)

### Installation
The application requires Docker to run. In the repository root there is a `docker-compose.yml` which contains the
necessary docker images and configurations to run the application.

To install and start the application you need to run the following command:

`docker-compose up`

The application will be started and is available on the 8080 port of your PC.

### Using the API
The API consist of 5 operations to manage metadata of machines. These operations are defined in a Swagger API
descriptor `.yaml` file which contains information about the endpoints and examples to use them.
You can find this file in:

`src/main/resources/api.yaml`

With Swagger you can generate a client to use this API in your application. 
For further information please read (https://swagger.io/docs/)

#### Operations
##### Create new machine
`POST /api/machine`

Creates a new machine object in the application with the provided fields.

The API will return an HTTP 400 error if an invalid object was provided, please check the `api.yaml`
on what type of fields are allowed in the request body.

##### Update an existing machine
`PUT /api/machine`

Updates an existing machine in the application with the provided fields. 

The application will return HTTP 404 if a machine with the provided ID does not exist.

The API will also return an HTTP 400 error if invalid machine object was provided, please check the `api.yaml`
on what type of fields are allowed in the request body.

##### Fetch a single machine
`GET /api/machine/{machineId}`

Returns a single machine from the application store in `application/json` format.

The application will return HTTP 404 if a machine with the provided ID does not exist.

##### Fetch all machines
`GET /api/machine/all`

Returns an array of machines stored in the application.

The machines will be returned in the order of which was updated most recently.

##### Delete a single machine
`DELETE /api/machine/{machineId}`

Deletes a single machine from the application.

The application will return HTTP 404 if a machine with the provided ID does not exist.

The deletion is soft-delete, the actual record won't be deleted from the database, but the API will not serve
the deleted records. 

#### Using the application with Postman
The application can be used with Postman as a client. An example Postman collection can be found under:

`postman/machinist.postman_collection.json`

If you import this into your Postman application, you can find example requests to use the API.
All the IDs and fields provided in the requests are valid, they're operating on a test dataset described
in the next section.

#### Test data
The application's database contains default machine data to test the API functionalities. 
For checking the existing data it is recommended to use the `GET /api/machine/all` endpoint.
If you'd like to remove the existing data please use the delete endpoint of the API.

