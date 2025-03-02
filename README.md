# Canvassing app
This is the back end for a canvassing app.

# Build and run
It builds and runs on Java 17. You can build it with `mvn clean install`, and run it with `java -jar canvassing-1.0.0.jar`. 
A postman collection with sample data is included so you can exercise the endpoints.

# Overview
Upon start-up, it populates a database with 500 random fake households in the Austin area and a 
questionnaire with two questions for the canvassers to ask. It provides API endpoints for canvassers and organizational adminstrators.

Administrators can:
1. Add a household
2. Remove a household
3. Change the questionnaire
4. Retrieve all responses and statuses

Canvassers can:
1. Retrieve a list of uncanvassed households to canvass, along with questions to ask. It returns households near them.
2. Update the status of a household (this includes statuses like "inaccessible" and "refused" that do not generate responses)
3. Update answers to the questions for a household.

# Architectural Diagram

# Database Schema

# Decisions and tradeoffs
Because of the requirements that all data updates are propagated to all users immediately,
I chose a relational database with transactions as the simplest way to keep the data consistent. 
I also chose to have all reads and writes communicate directly with the database and not implement caching 
due to time constraints. 

I chose to make the canvass a list of questions with multiple-choice answers because that seems
like the most common type of canvass. It is a list because often subsequent questions won't make sense
depending on the answer to the first question. Other types of questions are possible, like integer responses
("how many boxes of Thin Mints do you want to order?") or phone numbers ("can we get your number to 
let you know about events?"), but supporting arbitrary answers adds complexity. 

# Avenues for further work
Like all initial releases, this one is more of an MVP than a full-polished product with all possible bells and whistles.
Further potential developments include:
* Moving the data to a real database instead of H2 
* A more intelligent way of searching for households near the user. Right now the query is doing a full table scan to find these households, which won't scale the way it needs to. This would really use more robust location support like a grid system, PostGIS, or Google's S2 library.
* Storing results of past canvasses. Right now that information is wiped out when the questions change.
* Consider solutions to data-related bottlenecks. These might include:
    * Federation or sharding. Probably it will be rare to work with data from multiple states, for instance.
    * Indexing the database by canvassing status. So far our two biggest queries are by canvassing status, so we could avoid full table scans.
    * A read replica, so retrieving the canvassing lists and results isn't putting pressure on the same database as the writes.
* Support multiple organizations. So far this only supports one.
* Support different types of answers to canvass questions. So far it only supports multiple-choice.