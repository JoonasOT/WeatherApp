# Contents

This folder/package contains all the code used in helping create a WeatherScene.

## CustomToolBar

This creates a custom toolbar with some buttons to the top of the screen.

## MillisToTime

This is a helper class that can convert millis since epoch to a valid time and
date. This class can be compared easily with each other with the comparable
interface or by forming a functional style structure with chained
isSmallerThan(other) and isLargerThan(other) calls and finally getting the final
result by eval().

For example could look like this:
```java
boolean isBetween = new MillisToTime(1000)
                        .isLargerThan(new MillisToTime(100))
                        .isSmallerThan(new MillisToTime(10000))
                        .eval();
// isBetween = true
```

## ReadingsToStrings

This class contains only static methods for converting a given measurement and
OpenWeather UNIT to a string representation for displaying to the user. E.g.:
```
getTemperature(10.0, METRIC) -> "10.0°C"
getTemperature(10.0, IMPERIAL) -> "10.0°F"
getTemperature(10.0, STANDARD) -> "10.0 K"
```

## The current folder

This directory contains all the functionality for displaying the current
weather for the user.

## The daily folder

This directory contains all the functionality for displaying a daily weather
forecast for the user.

## The forecast folder

This directory contains all the functionality for displaying an hourly or a
3-hour step weather forecast for the user.

## The map folder

This directory contains all the functionality for displaying a weather map for
the user.