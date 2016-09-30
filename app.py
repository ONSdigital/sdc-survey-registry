import os
from flask import Flask, request, jsonify
from flask_cors import CORS
import json
import re


app = Flask(__name__)

# Enable cross-origin requests
CORS(app)

registry = []


@app.route('/', methods=['GET'])
def info():
    result = []
    for survey in registry:
        refLink = "https://sdc-survey-registry.herokuapp.com/" + survey["reference"]
        urnLink = "https://sdc-survey-registry.herokuapp.com/urn:com.herokuapp.sdc-survey-registry:id:ru:" + survey["reference"]
        result.append({"name": survey["name"], "reference": survey["reference"], "urn": survey["urn"], "refLink": refLink, "urnLink": urnLink})
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


if __name__ == '__main__':

    with open("surveys.json") as surveys:
        data = json.load(surveys)
        results = data["result"]["results"]
    for survey in results:
        title = survey["description"]["title"]
        if "releaseDate" in survey["description"]:
            releaseDate = survey["description"]["releaseDate"]
        #print(title)

        # Create an acronym reference for this survey
        #print(re.split("\W+", title))
        acronym = ""
        for e in re.split("\W+", title):
            if len(e) > 0:
                acronym += e[0]
        #print(acronym)

        registry.append({"name": title, "urn": "urn:com.herokuapp.sdc-survey-registry:id:ru:"+acronym, "reference": acronym, "releaseDate": releaseDate})
        #print(json.dumps(registry))

    # Start server
    port = int(os.environ.get("PORT", 5000))
    app.run(debug=True, host='0.0.0.0', port=port)

