# Contents

This folder/package contains all the frontend functionality.

## Scenes

The project uses one stage (window), but switches different scenes to that
window. The different scenes available at the moment are the SearchScene and
the WeatherScene.

The SearchScene consists of all the functionality to search for cities and see
favourites and history.

The WeatherScene is the scene responsible for showing the user the actual weather
statistics.

All of these scenes are stored within the Scenes folder.

## Fonts folder

All the fonts used by the frontend have dedicated enums for ease of use and for
centralization purposes. All these font classes and the actual font locations
are stored inside this folder.

## Search folder

This folder contains all the logic for forming search queries and for the autofill
search box.

## Weather folder

This folder contains all the logic to populate the WeatherScene with different
"views" or panes or sections (however you'd like to call them).