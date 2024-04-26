# API class and it's prerequisites

This folder/package contains all the necessary elements to create an API
request over the internet.

The API class is the actual class used for making said calls and adding the API
key to a set API and the works kinda like threads, as a thread could be created
with Callable< T >, which in turn returns an object of the type Future< T >. An
API call is made by forming an API and then using calling the call method with
a iCallable which finally supplies a Response.

Forming API classes is done via the API factories. Any object that want to be
able to make api calls MUST provide an implementation of API_Factory. Said
API_Factory binds a Key to the formed API, thus all callables that require an
API key must be used through the right API. Adding the value of the api key to 
the url can be done by adding "{API key}" in the place the actual API key
should be placed at.

To make an API call the user must supply the API.call() method a so called
"callable" aka a type implementing the interface iCallable.

## Callables

The main point of using these "callables" is the ease of creating new different
API calls as an iCallable must just provide the methods url() and args().
The function ulr() returns the url WITH VARIABLES AS VARIABLE NAMES
and args() returns a map for mapping the variable names in the url to their
actual final values before the call. E.G. the plain url could be of the form:

url() = "https://www.amazon.com/login/user={USERNAME}&passwd={PASSWORD}"

and args could be of the form:

args() = {"{USERNAME}" : "my_name", "{PASSWORD}": "doggo:D"}

and this would result in the API provided with this callable to call the url:
https://www.amazon.com/login/user=my_name&passwd=doggo:D

## Api response

Finally, after making an API call the method returns an object of the Response
type. The Response class provides some methods to read and validate the content
returned by the API call. E.g. CallWasOk() checks for response code 200 and
getAllBytes() returns all the bytes read from the connection and getData() gets
the returned content as a string.

## Note!

For more advanced usage the project also includes a few different annotations
(RequestMethod and SetRequestProperty) which can be used to fine tune calls.

Adding @RequestMethod(method=X) sets the request method (GET, POST, ...) of
an API call to the string X (defaults to GET).

Adding @SetRequestProperty(Property=X) sets the request property X to the
output of the method this notation is attached to. Before performing an API
call, the API class goes through all the declared methods of a callable and if
there are some methods with this annotation, it adds all the properties.