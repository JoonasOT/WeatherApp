# Contents

This folder/package contains all the functionality for displaying a weather map for
the user.

## WeatherMapView

The actual view or pane displayed to the user is located here.

## MapLoader

This is a class used by the WeatherScene to load a WeatherMapView async so,
that the map is only shown after it has loaded.

## MapGenerator

This is the class responsible for generating the map for WeatherMapView.

## MapTileGenerator

This class generates all the weather tiles with their layers for the MapGenerator.

## Tile

This class is used to store all the map layers (images) and the base map as well
as then positioning the layers one at time over the base map.