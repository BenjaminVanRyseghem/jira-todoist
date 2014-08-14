# jira-todoist

`jira-todoist` is a JIRA webhook implemented in Clojure to automatically add/update a task in todoist when a JIRA task is assigned to someone.

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server-headless

## Compiling

To compile the application as a stand-alone deplaoyable server, run:

    lein ring uberjar

The generated jars can be found in `./target`

You can then run it using:

    java -jar jira-todoist.jar

Note that an `info.json` file is needed along the `jar` file.

## info.json structure

The `info.json` file is structured like this: 

    {
	"email": "your todoist email address",
	"password": "your todoist password",
	"todoistProject": "the todoist project where will belong jira tickets",
	"jireUrl": "your jira url e.g. http://my-jira.atlassian.net"
    }

## License

Copyright © 2014 Benjamin Van Ryseghem.

The files contained is this repository are under the GPL v2 licence.
See `LICENCE` for me information.
