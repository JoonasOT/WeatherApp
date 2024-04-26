# TODO:
- Fix the god awfull UML


- Fix OpenWeather license? (hourly forecasts return 401 for not high enough key access level)


- SearchScene:
  - The default units could be selected from the IP Service result


- CurrentWeather, DailyForecast, WeatherForecast:
  - Clean the if-ifelse-else structure with a monad


- MapGenerator:
  - Get the OpenStreetMap response and images in a separate thread
  - Maybe cache N most recent maps (max for an hour or so from last call)

- Backend.getNxNtiles
  - Upgrade to 2.0 Maps

- WeatherMapView:
  - Add an option to remove the current active layer reset
  - Add the ability to move map with smt else than just scroll-wheel


- Backend
  - If we can't get a connection, display it on the GUI (needs backend constructor to be async)

