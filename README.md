# jira-todoist

`jira-todoist` is a JIRA webhook implemented in Clojure to automatically add/update a task in todoist when a JIRA task is assigned to someone.

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server-headless

## License

Copyright © 2014 Benjamin Van Ryseghem.

The files contained is this repository are under the GPL v2 licence.
See `LICENCE` for me information.
