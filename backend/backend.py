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
        prompt += f" that fits any of the following categories excluding those provided in the input: {previous_filters_str}."

    # if previous_response:

        # previous_response_str = ", ".join(previous_response)
        # prompt += f" Ensure a diverse selection that differs from the previous response: {previous_response_str}"

    print(prompt)

    # Doesn't work for some reason
    instruction = (
        "Generate a JSON-formatted response with the 'filters' key containing a list of values based on the provided filter type.\n"
        "Ensure the response follows this format precisely and contains only the JSON-formatted response without additional text.\n"
        "Example response format:\n"
        "{\n"
        '  "filters": ["filter_type_1", "filter_type_2", "filter_type_3", "filter_type_4", "filter_type_5"]\n'
        "}\n"
        "Replace the placeholders in the example response with appropriate values based on the given categories.\n"
        "Ensure there are no duplicate values in the response.\n"
        "Ensure that the time period filter is represented in the decade and in the format XXXXs, where X indicates digits 0-9.\n"
        f"Ensure that {filter_type} is not included in the response.\n"
        # f"Ensure that the generated response differs from the previous one given.\n"
        # f"Please ensure that the response provided exclusively pertains to the specified {filter_type} filter type only.\n"
        f"Additionally, ensure that the response contains exactly {filter_amt} item(s).\n"

    )

    instruction = "Generate a JSON formatted response with the \"filters\" key and a list of values" + \
        " Ensure the response contains only the JSON formatted response, with no additional text. Example response format: " + \
        "{ \"filters\": [\"" + filter_type + "_1\", \"" + filter_type + "_2\", \"" + filter_type + "_3\", \"" + \
        filter_type + "_4\", \"" + filter_type + "_5\"] } Please note that the example response contains placeholders for " + filter_type + \
        " and should be replaced with appropriate values based on the given categories. " + \
        "Ensure that the time period filter is represented in the decade and in the format XXXXs, where X indicates digits 0-9. Also ensure that there are no duplicate values." + \
        "Also ensure that you dont give me the filters that I gave you as a response"

    response = request_openAI(prompt, 1, instruction)
    print(response)
    parsed_object = json.loads(response)
    return jsonify(parsed_object)


@app.route("/api/request-playlist", methods=["POST"])
def request_playlist():
    song_amt = request.json.get("amount")
    filters = request.json.get("filters")

    filters_str = " that fits any of the following category:" if len(
        filters) else ""

    for index, item in enumerate(filters):
        filters_str += (" " if index == 0 else ", ") + item

    prompt = ""
    prompt = "Give me a playlist of *" + \
        str(song_amt) + "* " + filters_str

    instruction = "Generate a JSON formatted response with the following example format: " + \
        "{\"playlist\": [ {\"title\": \"song_title\", \"artist\": \"song_artist\" }, { \"title\": \"song_title\", \"artist\": \"song_artist\" }, ... ]}" + \
        "Ensure the response contains only the JSON formatted response, with no additional text. Please fill in the \"title\" and \"artist\" fields for each song" + \
        "Ensure that There are no duplicate songs in the playlist." + \
        "Also ensure that you dont give me the filters that I gave you as a response" + \
        "Ensure that the order of songs in the playlist is randomized for each request." + \
        "Ensure that you include similar artists to the provided ones to diversify the playlist" + \
        "Ensure that there are exactly *" + \
        str(song_amt) + "* songs in the generated response"

    response = request_openAI(prompt, 0.7, instruction)
    parsed_object = json.loads(response)
    print(parsed_object)
    return jsonify(parsed_object)


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)
