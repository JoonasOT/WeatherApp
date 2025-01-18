# Programming 3 project repository

This is my personal copy of my Programming 3 project. As it probably clear from the repo name this is a "simple" weather app that I may have over engineered a bit compared to the requirements.

This was ment to be a group project, but as I wanted to challenge myself, I did this project as a solo effort.

# NOTE

For this program to work, a valid OpenWeather API key is required (PRO TIER for all functionality. See [instructions](./WeatherApp/src/main/java/fi/tuni/prog3/weatherapp/README.md )). This is to mitigating leaking own API keys by accident to Github etc.

In addition to get the user's approximate location the user needs to download the [MaxMind GeoLiteIP database](https://dev.maxmind.com/geoip/geolite2-free-geolocation-data/) in binary format (.mmdb). The downloaded and unzipped folder should be placed in the [databases dir](./WeatherApp/Databases/) and the path to the actual .mmdb file should be set in the [backend class](./WeatherApp/src/main/java/fi/tuni/prog3/weatherapp/backend/Backend.java) (Backend.GEOIP_DATABASE_LOC ).
