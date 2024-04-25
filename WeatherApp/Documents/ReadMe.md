# Note!
The program requires a working connection to the internet and using a VPN might
make the suggestions provided somewhat weird.

The use of this program requires a valid API key to the OpenWeatherAPI and that
the key has been run through the KeyGen (found in package fi.tuni.prog3.keygen).
The recommended encrypted key location and name is:
"{working_dir}/secrets/ApiKeys/OpenWeather".

If used with another location than the one specified above the file (with
location) must be supplied as the first commandline argument to WeatherApp.

If for some reason the encryption system doesn't work or the user can't be
bothered with using it (:D) it can be totally nerfed by a quick modification to
Key.Java.

# 1. Program structure

This program has been mainly divided into two different sections:
1) The frontend
2) The backend

## 1.1 Frontend structure

The frontend consists of one JavaFX stage (window) that then either displays a
search scene or a weather scene to the user.

### 1.1.1 Search scene

The search scene consists of all a query field where the user can type a cities
name and possibly a country code to specify the country (separated with ", ").

If the user clears the text-field they are displayed with their favourite cities
and search history. As the user also starts to type for some city the program
provides the user with some suggestions. To use an entry from the suggestion box
simply click on the entry. Also pressing enter once ready with the typed query
works as well.

Before querying for a result the user can also specify what units should be used.
This is done by selecting the preferred unit from the dropdown under

### 1.1.2 Weather scene
Once the user queries for a city the program will switch from the search scene
to the weather scene. If the user's query was erroneous (query wasn't found
from the OpenWeather database), the user is simply prompted with a message of
this error.

If the user's query was OK, the user will be provided with the following views
or panes:
1) Current weather at the location
2) Daily weather forecast
3) Hourly weather forecast or 5-day forecast with 3 hour steps (depends on the
on if the supplied API key has the permissions for the hourly forecasts)
4) A weather map centered around the given location

As the weather map by default consists of a 10x10 grid of images with 6 layers
(1 base map + 5 weather layers) the program has to make 600 api calls it might
take a few seconds for the map to appear (happens mostly in parallel but still
might have a small delay). As this happens asynchronously, the text "Map is 
loading..." is displayed while the map formation takes place. Panning the map
happens with scrolling (shift for horizontal) and selecting the weather layer
happens from the dropdown on top of the map (top left corner).

To favourite a search result press the star symbol at the top of the window.
To un-favourite uses the same mechanism.

To return to the search scene to press the magnifying glass symbol next to
the star symbol.

To delete search history can also be done from the "Reset history" button.

## 1.2 Backend structure

The backend can be divided into "APIs" and "databases". These APIs are classes
that can make API calls and databases are classes that work with local
data. The backend can be divided into the following:
1) IPService (singleton for managing the IP_Getter API class)
2) OpenWeather API class
3) MaxMindGeoIP2 database class
4) Cities database class

Before diving into how these classes function we must understand how this
program forms API calling classes and database classes.

### 1.2.1 API classes

Api calls are made by forming an instance of the API class and then providing
this class with so called "callables" (types that implement the interface
iCallable) which in turn provide a Response object.

This could be compared to how a thread could be created with Callable< T >, which
in turn returns an object of the type Future< T >.

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

Forming API classes is done via the API factories. Any class that wants to form
an API MUST provide an implementation of API_Factory. Said API_Factory binds a
Key to the formed API, thus all callables that require an API key must be
used through the right API. Adding the value of the api key to the url can be
done by adding "{API key}" in the place the actual API key should be placed at.

Finally, the Response class provides some methods to read and validate the
content returned by the API call. E.g. CallWasOk() checks for response code 200
and getAllBytes() returns all the bytes read from the connection and getData()
gets the returned content as a string.

Note!
For more advanced usage the project also includes a few different annotations
(RequestMethod and SetRequestProperty) which can be used to fine tune calls.

Adding @RequestMethod(method=X) sets the request method (GET, POST, ...) of
an API call to the string X (defaults to GET).

Adding @SetRequestProperty(Property=X) sets the request property X to the
output of the method this notation is attached to. Before performing an API
call, the API class goes through all the declared methods of a callable and if
there are some methods with this annotation, it adds all the properties.

### 1.2.2 Database classes

Database classes are notably a lot simpler than API classes. The only
limitation is that they must implement the Database< T > interface. Thus, they
must provide the function get< T >().

### 1.2.1 IPService and IP_Getter

IP_Getter is a class that provides functionality for gaining the machine's
public ip address. IPService is a singleton class that when constructed runs
through the different 