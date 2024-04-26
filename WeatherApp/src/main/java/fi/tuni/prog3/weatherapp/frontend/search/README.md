# Contents

This folder/package contains all the code used in helping create a SearchScene.

## SearchResult

The SearchResult class is a JavaFX node, that displays the city information in
the format:
```
<tag> <city name> <country code>
```
Where tag is a symbol for if the SearchResult happens to be in user favourites
or history.

## SuggestionTextField

This is the actual JavaFX node responsible for performing queries and displaying
search suggestions. The actual suggestion generation is performed by the
SuggestionFiller class, which is run on a separate thread by the
SuggestionTextField each time the contents of the SuggestionTextField are changed.

## SuggestionFiller

As noted above this is the actual class responsible for generating search
suggestion and populating said results into a ContextMenu that is finally
displayed to the user.