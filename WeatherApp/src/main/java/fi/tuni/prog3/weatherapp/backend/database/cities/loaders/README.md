# Contents

This folder/package contains all the functionality to load the Cities database.

These contained loaders in the order of usage (use the first that works):
1) ParallelLoader - loads the cities in parallel from an optimised file and 
another file containing the count of cities that will be loaded.
2) BaseLoader - loads the cities from the optimised file with a single thread.
3) FallbackLoader - loads the cities from the downloadable city list file
(city.list.json.gz) from https://bulk.openweathermap.org/sample/

Note!

If the FallbackLoader fails on it's first attempt, Cities will try and download
the above file before trying FallbackLoader for one final time.