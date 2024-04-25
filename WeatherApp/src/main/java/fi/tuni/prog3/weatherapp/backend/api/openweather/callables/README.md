# Contents

This folder/package contains classes used to form a functional style "pipe" structure
for forming OpenWeather callables.

## BaseCallable

BaseCallable adds the following capabilities:
1) Add a language argument to a callable
2) Add a unit argument to a callable

## CityNameCallable

CityNameCallable extends an BaseCallable with implementing the functionality to
target the callable to a specific city. In addition, this callable also
provides the capability to add a country code to the callable.

## LatLonCallable

LatLonCallable extends an BaseCallable with implementing the functionality to
target the callable to a specific latitude and longitude.

## ZipCodeCallable

ZipCodeCallable extends an BaseCallable with implementing the functionality to
target the callable to a specific zip code and as CityNameCallable to also be
able to attach a country code to the callable.

Note!
Without adding the country code the default target will be the USA.