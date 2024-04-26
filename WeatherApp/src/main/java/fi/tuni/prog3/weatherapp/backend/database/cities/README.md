# Contents

This folder/package contains the Cities database, which stores all the cities
stored in the OpenWeather database. This database allows the user to get a list
of cities that are similar to a given query. These results are also biased
based on a supplied location (country code).

## Cities class and CityJSON

Cities contains the main construction of the database and the get() method. This
database will first try to construct itself in parallel, then with one thread
with an optimised file, if those don't work it will try to construct it from a
OpenWeather bulk file and if that isn't readily found on the machine it will
download it and then read it and then construct the necessary files for a more
optimised load on the next start-up. These different loading mechanisms can ve
found from the loaders folder/package.

CityJSON in turn is a class used to convert the json string from OpenWeather to
objects in Java.

## Params class / structure

The params class hold all the static default locations used in construction of
the database. When building the Cities database with the CitiesBuilder the
builder will use these values if not otherwise set to something else.

Note!

Construction of the Cities database happens only through the CitiesBuilder
(found inside the builder folder/package).

## EditDistance class

This class holds functionality for calculating the edit distances between a
word and given target words in parallel and in a single thread form. The 
edit distance between two words simply means the number of operations to
transform one into the other. In other words this is basically a metric of how
similar these words are.