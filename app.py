import os
from flask import Flask, jsonify
from flask_cors import CORS
import re
import requests
from queue import Queue
from threading import Thread

app = Flask(__name__)

# Enable cross-origin requests
CORS(app)

registry = []


@app.route('/', methods=['GET'])
def info():
    result = []
    for survey in get_registry():
        link = "/" + survey["reference"]
        result.append({
            "title": survey["title"],
            "reference": survey["reference"],
            # "urn": survey["urn"],
            "link": link,
            "frequency": survey["frequency"]
        })
    return jsonify(result)


@app.route('/<reference>', methods=['GET'])
def login(reference):
    for survey in get_registry():
        if survey["reference"].lower() == reference.lower():
            result = dict(survey)
            result["name"] = result["title"]
            return jsonify(result)

    # not found:
    resp = jsonify("Survey not found for: " + reference)
    resp.status_code = 400
    return resp


def abbreviate(name):
    acronym = ""
    stop_words = name
    for stop in ("and", "of", "in", "the", "by"):
        stop_words = stop_words.replace(" " + stop + " ", " ")
    for e in re.split("\W+", stop_words):
        if len(e) > 0:
            acronym += e[0]
    return acronym.lower()


def frequency(name):
    if "monthly" in name.lower():
        return "monthly"
    if "quarterly" in name.lower():
        return "quarterly"
    if "annual" in name.lower():
        return "annual"
    # Fall back to a default guess - 1 in 3 ain't bad for now..
    return "quarterly"


def get_registry():
    global registry

    if len(registry) == 0:
        url = "https://www.ons.gov.uk/surveys/informationforbusinesses/businesssurveys/staticlist/data?size=100"
        response = requests.get(url)
        data = response.json()
        print("Survey list: " + repr(data))
        results = data["result"]["results"]

        # Set up the queue - https://www.toptal.com/python/beginners-guide-to-concurrency-and-parallelism-in-python
        queue = Queue()
        for x in range(8):
            worker = DownloadWorker(queue)
            # Setting daemon to True will let the main thread exit even though the workers are blocking
            worker.daemon = True
            worker.start()

        for survey in results:
            name = survey["description"]["title"]
            uri = survey["uri"]
            url = "https://www.ons.gov.uk" + uri + "/data"
            print("Enqueueueing: " + url)
            queue.put((name, url))

        queue.join()
        print("Survey registry building finished.")

    return registry


class DownloadWorker(Thread):
    def __init__(self, queue):
        Thread.__init__(self, None, queue)
        self.queue = queue

    def run(self):
        while True:
            # Get the work from the queue and expand the tuple
            name, url = self.queue.get()
            download_survey(name, url)
            self.queue.task_done()


id_ = 1
form_type = 1


def download_survey(name, url):
    global id_
    global form_type

    print("Downloading " + name + " from " + url)

    response = requests.get(url)
    data = response.json()

    # Create an acronym reference for this survey

    item = data["description"]
    title = item["title"]
    acronym = abbreviate(item["title"])
    item["markdown"] = data["markdown"]
    item["links"] = data["links"]
    item["reference"] = acronym
    item["urn"] = "urn:uk.gov.ons.surveys:id:survey:" + acronym
    item["frequency"] = frequency(title)
    for link in item["links"]:
        link["uri"] = "https://www.ons.gov.uk" + link["uri"]

    # Generate some dummy collection instruments and form types for the time being:
    collection_instruments = [
        {"id": "90" + str(id_), "type": "online", "form_types": [str(form_type), str(form_type + 1)]},
        {"id": "90" + str(id_ + 1), "type": "offline", "form_types": [str(form_type + 2), str(form_type + 3)]},
        {"id": "90" + str(id_ + 2), "type": "paper", "form_types": [str(form_type + 4), str(form_type + 5)]}
    ]
    id_ += 3
    form_type += 6
    item["collection_instruments"] = collection_instruments

    registry.append(item)
    print("\tDownloaded " + name + ": " + repr(item))


if __name__ == '__main__':

    # Build the registry
    get_registry()

    # Start server
    port = int(os.environ.get("PORT", 5000))
    app.run(debug=True, host='0.0.0.0', port=port)
