from flask import Flask, request, jsonify
from openai import OpenAI
from config import OPENAI_API_KEY, GPT_MODEL, API_URL
import json

app = Flask(__name__)


def request_openAI(prompt, temperature=0.2, instruction="Reply in json format only without adding anything else"):
    client = OpenAI(api_key=OPENAI_API_KEY)
    messages = [
        {
            "role": "system",
            "content": instruction
        },
        {
            "role": "user",
            "content": prompt,
        }
    ]
    try:

        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=messages,
            temperature=temperature
        )
        text = response.choices[0].message.content.strip()

        return text

    except Exception as e:
        return None


@app.route("/api/request-filter", methods=["POST"])
def request_filter():
    filter_amt = request.json.get("amount")
    filter_type = request.json.get("type")
    previous_filters = request.json.get("previous_filter")
    previous_response = request.json.get("previous_response")

    prompt = f"Please provide exactly *{filter_amt}* musical {filter_type}"

    if previous_filters:
        previous_filters_str = ", ".join(previous_filters)
        prompt += f" that fits any of the following categories excluding those provided in the input: {
            previous_filters_str}."

    if previous_response:

        previous_response_str = ", ".join(previous_response)
        prompt += f" Ensure a diverse selection that differs from the previous response: {
            previous_response_str}"

    print(prompt)

    instruction = "Generate a JSON formatted response with the \"filters\" key and a list of values" + \
        " Ensure the response contains only the JSON formatted response, with no additional text. Example response format: " + \
        "{ \"filters\": [\"" + filter_type + "_1\", \"" + filter_type + "_2\", \"" + filter_type + "_3\", \"" + \
        filter_type + "_4\", \"" + filter_type + "_5\"] } Please note that the example response contains placeholders for " + filter_type + \
        " and should be replaced with appropriate values based on the given categories. " + \
        "Ensure that the time period filter is represented in the decade and in the format YYYYs (i.e. 1980s, 2000s, 2010s etc). " + \
        "Also ensure that there are no duplicate values." + \
        "Also ensure that you dont give me the filters that I gave you as a response. " + \
        f"Please ensure that you give me artist names when I request for Artist filter type (E.g. Taylor Swift, Ed Sheeran, etc)"

    response = request_openAI(prompt, 0.5, instruction)
    print(response)
    parsed_object = json.loads(response)
    return jsonify(parsed_object)


@app.route("/api/request-playlist", methods=["POST"])
def request_playlist():
    song_amt = request.json.get("amount")
    filters = request.json.get("filters")

    filters_str = " that fits any of the following categories:" if len(
        filters) else ""

    for index, item in enumerate(filters):
        filters_str += (" " if index == 0 else ", ") + item

    prompt = "Give me a playlist of only *exactly* *" + \
        str(song_amt) + "* songs" + filters_str

    instruction = (
        "Generate a JSON-formatted response with the following format: \n"
        "{\n"
        '  "playlist": [\n'
        '    {"artist": "song_artist", "track": "track_title"},\n'
        '    {"artist": "song_artist", "track": "track_title"},\n'
        '    ...\n'
        '  ]\n'
        "}\n"
        "Ensure that the response contains only the JSON-formatted playlist, without additional text. Please provide the artist and track details for each song.\n"
        "Ensure that there are no duplicate songs in the playlist.\n"
        "Exclude the provided filters from the response to diversify the playlist.\n"
        "Randomize the order of songs in the playlist for each request.\n"
        "Include similar artists to the provided ones to ensure playlist diversity.\n"
        "Ensure that the response only contains *exactly* *" +
        str(song_amt) + "* songs."
    )

    response = request_openAI(prompt, 0.7, instruction)
    parsed_object = json.loads(response)
    print(parsed_object)
    return jsonify(parsed_object)


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)
