import os
from flask import Flask, request, jsonify
from flask_cors import CORS
import json
import re
import requests


app = Flask(__name__)

# Enable cross-origin requests
CORS(app)

registry = []


@app.route('/', methods=['GET'])
def info():
    result = []
    for survey in registry:
        # ref_link = "https://sdc-survey-registry-python.herokuapp.com/" + survey["reference"]
        # urn_link =
        # "https://sdc-survey-registry-python.herokuapp.com/urn:uk.gov.ons.surveys:id:ru:" +
        # survey["reference"]
        link = "/" + survey["reference"]
        # result.append({"name": survey["name"], "reference": survey["reference"], "urn": survey["urn"],
        # "refLink": ref_link, "urnLink": urn_link})
        result.append({"name": survey["name"], "reference": survey["reference"], "urn": survey["urn"], "link": link})
    return jsonify(result)


@app.route('/<reference>', methods=['GET'])
def login(reference):
    for survey in registry:
        if survey["reference"] == reference or survey["urn"] == reference:
            return jsonify(survey)

    # not found:
    resp = jsonify("Survey not found for: " + reference)
    resp.status_code = 400
    return resp


def get_json():
    url = "https://www.ons.gov.uk/surveys/informationforbusinesses/businesssurveys/staticlist/data?size=60"
    response = requests.get(url)
    return response.json()


if __name__ == '__main__':

    data = get_json()
    print(data)
    results = data["result"]["results"]
    for survey in results:
        title = survey["description"]["title"]
        if "releaseDate" in survey["description"]:
            releaseDate = survey["description"]["releaseDate"]

        # Create an acronym reference for this survey
        acronym = ""
        for e in re.split("\W+", title):
            if len(e) > 0:
                acronym += e[0]

        registry.append({"name": title, "urn": "urn:uk.gov.ons.surveys:id:ru:"+acronym, "reference": acronym, "releaseDate": releaseDate})

    # Start server
    port = int(os.environ.get("PORT", 5000))
    app.run(debug=True, host='0.0.0.0', port=port)

