# Contents

This folder/package contains the MaxMindGeoIP2 database class and it's
prerequisites. This database simply turns a given public IP address to an
approximate geolocation. This is simply used in this program to primarily
favour autofill suggestions in the same country.

The use of this database also requires downloading the GeoLite 2 Cities
database into the "/Databases/". Then setting the database name in Backend.java