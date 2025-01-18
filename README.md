# Programming 3 project repository

This is my personal copy of my Programming 3 project. As it probably clear from the repo name this is a "simple" weather app that I may have over engineered a bit compared to the requirements.

This was ment to be a group project, but as I wanted to challenge myself, I did this project as a solo effort.

# NOTE

For this program to work, a valid OpenWeather API key is required (PRO TIER for all functionality. See [instructions](./WeatherApp/src/main/java/fi/tuni/prog3/weatherapp/README.md). This is done by storing the key as a JSON file and then "encrypting" (hashing and XOR) it to a binary with the KeyGen class. After this the plain text JSON file can be deleted and the SW uploaded. This mktigates the risk of people just stealing credentials from GitHub etc. (ofc still possible, but requires actual effort)
