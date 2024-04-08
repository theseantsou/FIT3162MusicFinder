from flask import Flask, request, jsonify
from openai import OpenAI
from config import OPENAI_API_KEY, GPT_MODEL, API_URL


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
    filter_type = request.json.get("type")

    prompt = "Give me 5 random " + filter_type
    instruction = "Generate a JSON formatted response, specifically containing a key \"filters\" and values being a list of items requested. The output should contain only the JSON formatted response with no additional text"

    return jsonify(request_openAI(prompt, 0.6, instruction))


if __name__ == "__main__":
    app.run(debug=True)
