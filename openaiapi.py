import requests

# OpenAI API key
API_KEY = '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'
API_URL = 'https://api.openai.com/v1/chat/completions'

def ask_chatgpt(question):
    headers = {
        'Authorization': f'Bearer {API_KEY}'
    }
    data = {
        'model': 'gpt-3.5-turbo',  # make sure to use the correct model here
        'messages': [
            {"role": "user", "content": question}
        ],
        'temperature': 0.5,
        'max_tokens': 100,
        'top_p': 1.0,
        'frequency_penalty': 0.0,
        'presence_penalty': 0.0,
    }
    response = requests.post(API_URL, headers=headers, json=data)
    return response.json()

# use the function to ask a question
try:
    response = ask_chatgpt("What is 1+1?")

    if 'choices' in response and response['choices']:
        latest_message = response['choices'][0]['message']['content']
        print(latest_message.strip())
    else:
        print("No response from the API.")
except KeyError as e:
    print(f"There was an error processing your request: {e}")
    print(response)

#This comment is just a test by Sam Halford to see if I can update GitHub Code
