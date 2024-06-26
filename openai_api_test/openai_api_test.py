from openai import OpenAI
from config import OPENAI_API_KEY, GPT_MODEL, API_URL
import requests
from flask import Flask, request, jsonify

app = Flask(__name__)


def request_gpt(client, txt_prompt, instruction="Be nice", temperature=0.7):
    message_list = []

    system_message = {"role": "system", "content": f"{instruction}"}
    user_message = {"role": "user", "content": f"{txt_prompt}"}
    message_list.append(system_message)
    message_list.append(user_message)
    
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {OPENAI_API_KEY}"
    }

    data = {
        "model": GPT_MODEL,
        "temperature": temperature,
        "messages": message_list,
        #"max_tokens": 300,
    }

    try:
        response = requests.post(API_URL, json=data, headers=headers)
        responseJSON = response.json()

        if response.status_code == 200:
            return response.json()["choices"][0]["message"]["content"]
        else:
            print(f"Request failed with status code {response.status_code}: {responseJSON}")
            return None
    except Exception as e:
        print(f"An error occurred: {e}")
        return None
    

@app.route("/process_request", methods=["POST"])
def process_request():
    data = request.json
    prompt = data.get("prompt")

    client = OpenAI(api_key=OPENAI_API_KEY)
    response = request_gpt(client, prompt)
    return response


if __name__ == "__main__":
    app.run(debug=True)

