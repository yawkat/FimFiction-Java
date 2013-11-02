FimFiction-Java
===============

Library for many story-related functions of the My Little Pony fanfiction website [FimFiction](http://fimfiction.net/).

Structure
---------
This library consists of three types of classes:

- The **FimFiction class** manages one session (Session ID)
- The **storage classes** store certain types of information such as story metadata, text content or user metadata
- The **operation classes** execute certain operations or requests (in a given) session, for example a search request.

There are also a few parser classes but they are only used internally by operation classes.

Usage
-----

First of all, create a FimFiction object:

	FimFiction session = new FimFiction();

Now you can login (if you need information such as unread favorites):

	LoginOperation login = new LoginOperation("username", "password");
	session.executeOperation(login);
	if (login.getResult() != LoginOperation.Result.SUCCESS) {
	    // handle error
	}

Do something:

	SearchRequest search = new SearchRequest();
	search.setParameters(new SearchParameters().withFavorite(true).withUnread(true));
	search.setRequestMethod(SearchRequest.RequestMethod.FULL);
	search.setPage(0);
	session.executeOperation(search);
	for (Story story : search.getResult()) {
	    System.out.println(story.getTitle());
	}

And finally log out (though it's no *really* required):

	session.executeOperation(new LogoutOperation());

And that's it! There are still a few more things to keep in mind if you want to design a high-performance application, such as different request types, but those should not be too complicated to understand from the javadocs.
