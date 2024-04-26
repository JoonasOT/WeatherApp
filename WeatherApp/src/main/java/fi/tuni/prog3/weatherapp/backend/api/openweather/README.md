# Contents

This folder/package contains classes used to form an API class for the
OpenWeather API and some callables used to call this API as well as parsing
the responses to objects.

## OpenWeather class

This contains all the functionality to form an API class and enums for setting
the response language and units.

## CurrentWeather, DailyWeather, HourlyWeather, WeatherForecast & WeatherMap

These classes or "structures" contain all the callables used to generate what
each class named after. E.g. To create a callable that queries OpenWeather for
the current weather based on a given latitude and longitude, the required
callable would be located in CurrentWeather.callables (full location:
CurrentWeather.callables.CurrenWeatherLatLonCallable). 

In addition, these classes also provide all the objects contained in a response
from these callables. The functions used to convert the whole response to an
object are annotated with the FromJson-annotation.

E.g. Creating an OpenWeather API class and getting the current weather at
London (UK), in Spanish, in metric units and then converting it to an
CurrentWeatherObj would look like the following:

```java
API OpenWeather = new OpenWeather.factory().create();
var callable = new CurrentWeather.callables.CurrentWeatherCityNameCallable("London")
                                 .addCountryCode("GB")
                                 .addLangArg(OpenWeather.LANG.EN)
                                 .addUnitArg(OpenWeather.UNIT.METRIC);

Optional<Response> response = OpenWeather.call(callable);

if (response.isEmpty()) throw new RuntimeException("Response was empty!");
if (!response.get().CallWasOK()) throw new RuntimeException("Call was not ok!");

CurrentWeatherObj currentWeather = CurrentWeather.fromJson(response.get().getData());

// Get the description of the current weather:
System.out.println(currentWeather.weather.get(0).description());
```

## JSON_OBJs class

This class simply stores some of the most common object types from different API calls.