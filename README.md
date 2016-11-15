# ONS Survey Registry
This component provides information about ONS surveys. 

In the current form, information returned by this service is a combination of the data available from the ONS public website API (https://www.ons.gov.uk/surveys/informationforbusinesses/businesssurveys/staticlist/data?query=&size=100) plus some additional (partly accurate, partly fictional) data that helps us explore what a fully-featured survey registry would need to provide.

We think that ultimately this should provide full information about all the questions in each survey and the paths through a survey, but we're still exploring that.

There are two endpoints on this service as follows:

## /

Send a *get* to the default route to retrieve a summary list of all surveys.

## /[reference]

Send a *get* to any 'link' URI provided by the '/' endpoint to retrieve full detail about a specific survey.

NB the survey 'reference' is likely to change, the 'frequency' may not be accurate and the collection instrument and form type data is dummy, so please don't rely on them.

## Survey summary

A survey summary looks like this:

| Survey         |
| -------------- |
| reference      |
| name           |
| link           |

## Survey detail

The most useful fields of the survey detail look like this (NB most of the information comes directly from the ONS website and we don't currently make use of it for survey data collection):

| Survey                              |
| ----------------------------------- |
| reference                           |
| name                                |
| frequency  (best guess)             |
| collection_instruments (dummy data) | 

## Links

Try:
 * [https://sdc-survey-registry-python.herokuapp.com/](https://sdc-survey-registry-python.herokuapp.com/)
 * [https://sdc-survey-registry-python.herokuapp.com/VS](https://sdc-survey-registry-python.herokuapp.com/VS)
