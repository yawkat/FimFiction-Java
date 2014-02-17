Fimfiction-Java
===============

Library for many story-related functions of the My Little Pony fanfiction website [Fimfiction](http://fimfiction.net/).

Structure
---------

This library contains four subpackages of `at.yawk.fimfiction`:

- `data`: Storage and data classes used to access Fimfiction data.
- `core`: Classes used for building and processing requests to the site.
- `json`: Classes to serialize objects in the `data` package to JSON and deserialize them back again.
- `net`: Internally used HTTP utility class and Cookie management.

Usage
-----

### data

#### Bundles

The primary class of the `data` package is the `Bundle` class. It is a dictionary-/map-like class that stores a set of key->value pairs. It supports things such as immutability. It's subclasses are `Story`, `Chapter`, `User`, `SearchParameters` and `SearchResult`. An example using `Story`:

    Story bundle = Story.createMutable();
    bundle.set(Story.StoryKey.TITLE, "Test Story");
    System.out.println(bundle.getString(Story.StoryKey.TITLE));

The different value accessor methods:

    bundle.has(Story.StoryKey.TITLE);
    bundle.get(Story.StoryKey.TITLE);
    bundle.getBoolean(Story.StoryKey.GORE);
    bundle.getInt(Story.StoryKey.LIKE_COUNT);
    bundle.getString(Story.StoryKey.TITLE);
    bundle.set(Story.StoryKey.TITLE, "Test Story");
    bundle.unset(Story.StoryKey.TITLE);

Each of the get() methods also accepts a default value parameter. Values are *not* nullable but might not be set. get() methods without a default value will throw a MissingKeyException if the key is unset, the set() method will throw a NullPointerException if a null value is given.

#### Value Types

The ValueType class provides a validation mechanism for Bundle values: Each bundle key has a specific ValueType associated with it that will be used to verify input. There are various such ValueTypes, for example Numbers, Booleans, Strings, other Bundles, Optional values, Lists et cetera.

#### Optionals

`Optional` values are values that can be *known to not exist*. For example, if it is simply unknown if a story has a cover image or not, the story entry URL_COVER will be unset. However, if it was determined that the story has no cover image it will be set to a *missing Optional*. An optional can be either existing or missing, while the first has a value and the second one doesn't. An optional also always has a type assigned to it so type safety can be ensured for missing optionals as well.

#### Bundle mutability

A bundle can be either *mutable* or *immutable*. There are three methods to work with these states:

- `mutableCopy()` creates a copy of a Bundle that is mutable.
- `immutableCopy()` returns a copy of a Bundle that is definitely immutable. If the initial Bundle is already immutable no new one will be created but simply the old one returned.
- `mutableVersion()` works similar to mutableCopy() but does not copy an already mutable Bundle: This is useful if the initial Bundle was returned by an API call (for example a Search request) and should be safe to edit later while still avoiding performance loss through unnecessary copying.

#### Enums

The data package contains the Enums `Category`, `ContentRating`, `FavoriteState`, `Order`, `Rating` and `StoryStatus`. They are used as bundle value types.

#### Fimfiction Characters

Because of the frequent changes to the Fimficiton.net character list, the `FimCharacter` class is not an Enum but rather an interface. It contains an inner enum `DefaultCharacter` which provides implementations of this interface. Parsers might also create custom instances of this interface to accomodate newer characters.

### json

The `json` package provides two classes for serializing and deserializing `Bundle`s to / from JSON.

### net

The `net` package contains two classes:

- The `NetUtil` class is intended mainly for internal use and can be ignored.
- The `SessionManager` class provides an easy way of managing cookies on Fimficiton.net. It allows direct access to the *view mature* flag used by Fimfiction to hide mature content as well as providing login support using the `core.SessionActions` class.

### core

The `core` package contains the classes used for requesting and parsing data from Fimficiton.net.

#### Download

The `Download` class can be used to generate download URLs for stories and chapters. See the javadoc for more information.

#### Meta

The `Meta` class provides access to the two (currently known) JSON APIs on the Fimficiton website.

Example:

    SessionManager sessionManager = SessionManager.create();
    Story withMeta = Meta.create()
                         .story(Story.createMutable().set(Story.StoryKey.ID, 10)) // or just .story(10)
                         .noContent()
                         .request(sessionManager.getHttpClient());
    System.out.println(withMeta.getString(Story.StoryKey.TITLE)); // Accolade

The `content()` and `noContent()` methods switch between the two modes, the former one being slower but giving access to BB-formatted content.

#### Search

##### Search URL

The `SearchUrl` class serializes a SearchRequest bundle to a search URL:

    URL url = SearchUrl.create()
                       .parameters(SearchParameters.createMutable()
                                                   .set(SearchParameters.SearchParameter.ORDER, Order.HOT))
                       .page(0)
                       .build();

The class can also be used to build single-page story URLs instead using the `story(Story)` method.

For multi-page requests the CompiledSearchParameters class should be used instead, see the javadocs for more information.

##### Request

The `Search` class requests and parses the URL generated by a `SearchUrl` instance:

    SearchResult result = Search.create().idOnly().url(url).search(sessionManager.getHttpClient());

It also contains a few methods that can be used instead of the url(URL) method to skip parsing using `SearchUrl`:

    Search.create()
          .idOnly()
          .parameters(SearchParameters.createMutable().set(SearchParameters.SearchParameter.ORDER, Order.HOT), 0)
          .search(sessionManager.getHttpClient());

There are two main modes of this class: `idOnly()` is a more reliable parsing method that will only read story IDs, `full()` is a more extensive but more likely to fail method to parse a lot of data from the search page.

Lastly, there is a third `unreadFeed(User)` mode which will parse a user's RSS feed to receive the last updated unread favorites.

#### Session

The `SessionActions` class contains the `login(String, String)` and `logout(String)`. The login parameters are username and password, the logout parameter is a *Nonce* which must be aquired previously using a search request (`SearchResultKey.LOGOUT_NONCE`).
