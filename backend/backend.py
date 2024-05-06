from flask import Flask, request, jsonify, redirect
from flask_sqlalchemy import SQLAlchemy

from requests import post, get
from openai import OpenAI
from config import *
import json
import urllib.parse
from datetime import datetime
import os

basedir = os.path.abspath(os.path.dirname(__file__))


app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + \
    os.path.join(basedir, 'spotify_user.db')

db = SQLAlchemy(app)


class SpotifyUser(db.Model):
    email = db.Column(db.String(120), primary_key=True)
    auth_token = db.Column(db.String(200), nullable=False)
    refresh_token = db.Column(db.String(200), nullable=False)
    expiry_time = db.Column(db.Float, nullable=False)


with app.app_context():
    db.create_all()


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
            model=GPT_MODEL,
            messages=messages,
            temperature=temperature
        )
        text = response.choices[0].message.content.strip()

        return text

    except Exception as e:
        return None


@app.route("/request-filter", methods=["POST"])
def request_filter():
    filter_amt = request.json.get("amount")
    filter_type = request.json.get("type")
    previous_filters = request.json.get("previous_filter")
    previous_response = request.json.get("previous_response")

    prompt = f"Please provide exactly *{filter_amt + 1}* musical {filter_type}"

    if previous_filters:
        previous_filters_str = ", ".join(previous_filters)
        prompt += f" that fits any of the following categories excluding those provided in the input: {previous_filters_str}."

    if previous_response:

        previous_response_str = ", ".join(previous_response)
        prompt += f" Ensure a diverse selection that differs from the previous response: {previous_response_str}"

    print(prompt)

    instruction = "Generate a JSON formatted response with the \"filters\" key and a list of values" + \
        " Ensure the response contains only the JSON formatted response, with no additional text. Example response format: " + \
        "{ \"filters\": [\"" + filter_type + "_1\", \"" + filter_type + "_2\", \"" + filter_type + "_3\", \"" + \
        filter_type + "_4\", \"" + filter_type + "_5\"] } Please note that the example response contains placeholders for " + filter_type + \
        " and should be replaced with appropriate values based on the given categories. " + \
        "Ensure that the time period filter is represented in the decade and in the format YYYYs (i.e. 1980s, 2000s, 2010s etc). " + \
        "Also ensure that there are *no duplicate* values in the response provided ." + \
        "Also ensure that you dont give me the filters that I gave you as a response. " + \
        "Please ensure that you give me *artist names* when I request for *Artist* filter type (E.g. Taylor Swift, Ed Sheeran, etc). "

    response = request_openAI(prompt, 0.5, instruction)
    print(response)
    parsed_object = json.loads(response)
    return jsonify(parsed_object)


@app.route("/request-playlist", methods=["POST"])
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
        '  "tilte": "playlist_title - Generated using BeatBlendr",'
        '  "playlist": [\n'
        '    {"artist": "song_artist", "track": "track_title"},\n'
        '    {"artist": "song_artist", "track": "track_title"},\n'
        '    ...\n'
        '  ]\n'
        "}\n"
        "Ensure that the response contains only the JSON-formatted playlist, without additional text. Please provide the artist and track details for each song.\n"
        "Come up with a creative playlist title and replace it with the playlist_title and keep the '- Generated using BeatBlendr' behind the title\n"
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


@app.route("/spotify-login", methods=["POST"])
def spotify_login():
    email = request.json.get("email")
    if email:

        user = db.session.query(SpotifyUser).filter_by(email=email).first()

        if user:
            current_timestamp = datetime.now().timestamp()
            if current_timestamp > user.expiry_time:
                refresh_token(user)

            return jsonify({"url": None})

    # Define SpofityAPI scope
    scope = 'user-read-private user-read-email playlist-modify-public playlist-modify-private'

    # Request params
    auth_params = {
        'client_id': CLIENT_ID,
        'response_type': 'code',
        'redirect_uri': REDIRECT_URI,
        'scope': scope,
        'show_dialog': True
    }

    # Create auth URL
    auth_url = f"{AUTH_URL}?{urllib.parse.urlencode(auth_params)}"

    # Goto auth_url
    return jsonify({"url": auth_url})


@app.route("/callback")
def callback():

    req_body = None

    # Check for error in response
    if 'error' in request.args:
        return jsonify({"status": "error"})

    # If the request is successful
    if 'code' in request.args:
        req_body = {
            'code': request.args['code'],
            'grant_type': 'authorization_code',
            'redirect_uri': REDIRECT_URI,
            'client_id': CLIENT_ID,
            'client_secret': CLIENT_SECRET
        }

    # Check if auth code was given
    if req_body is None:
        return jsonify({"status": "error"})

    # Send request with auth code
    response = post(TOKEN_URL, data=req_body)
    token_info = response.json()

    access_token = token_info['access_token']
    refresh_token = token_info['refresh_token']
    expiry_time = datetime.now().timestamp() + \
        token_info['expires_in']

    email = getSpotifyEmail(access_token)
    user = SpotifyUser(
        email=email,
        auth_token=access_token,
        refresh_token=refresh_token,
        expiry_time=expiry_time
    )
    db.session.add(user)
    db.session.commit()

    return jsonify({"status": "success", "email": email})


def getSpotifyEmail(access_token):
    SPOTIFY_API_URL = API_BASE_URL + "/v1/me"
    headers = {
        'Authorization': f'Bearer {access_token}'
    }

    response = get(SPOTIFY_API_URL, headers=headers)

    if response.status_code == 200:
        user_data = response.json()
        return user_data["email"]
    else:
        return None


def refresh_token(user):

    req_body = {
        'grant_type': 'refresh_token',
        'refresh_token': user.refresh_token,
        'client_id': CLIENT_ID,
        'client_secret': CLIENT_SECRET
    }

    response = post(TOKEN_URL, data=req_body)
    new_token_info = response.json()

    user.auth_token = new_token_info['access_token']
    user.expiry_time = datetime.now().timestamp() + \
        new_token_info['expires_in']
    db.session.commit()


@app.route("/save-playlist", methods=["POST"])
def save_playlist():
    email = request.json.get("email")
    songlist = request.json.get("songs")
    playlist_title = request.json.get("title")
    if email:
        user = db.session.query(SpotifyUser).filter_by(email=email).first()

        if user:
            access_token = user.auth_token

        else:
            return jsonify({"status": "error"})

    else:
        return jsonify({"status": "error"})

    # Required for authorisation
    headers = {'Authorization': f'Bearer {access_token}'}

    # Clean songs of apostrophes
    for song in songlist:

        newstring = ""
        for character in song["artist"]:
            if character != "'":
                newstring += character
        song["artist"] = newstring

        newstring = ""
        for character in song["track"]:
            if character != "'":
                newstring += character
        song["track"] = newstring

    # Create playlist
    user_profile_response = get(
        'https://api.spotify.com/v1/me', headers=headers)
    user_id = user_profile_response.json()['id']
    create_playlist_response = post(
        f'https://api.spotify.com/v1/users/{user_id}/playlists', headers=headers, json={'name': playlist_title, 'public': False})
    if create_playlist_response.status_code == 201:
        playlist_id = create_playlist_response.json()['id']
    else:
        return jsonify({"status": "error"})

    # Iterate over all songs
    for track in songlist:

        # Find the URI of the current song, get the uri
        track_search_url = 'https://api.spotify.com/v1/search'
        search_params = {
            'q': f'artist: {track["artist"]} track: {track["track"]}', 'type': 'track', 'limit': 1}
        track_search_response = get(
            track_search_url, headers=headers, params=search_params)
        tracks_data = track_search_response.json()['tracks']['items']

        # Clean data further if track is not found
        if not tracks_data:
            newstring = ""
            bracket_dash_flag = False
            for character in track["track"]:
                if character == "(" or character == "-":
                    bracket_dash_flag = True
                if character != "'" and bracket_dash_flag == False:
                    newstring += character
                if character == ")":
                    bracket_dash_flag = False

            search_params = {
                'q': f'artist: {track["artist"]} track: {newstring}', 'type': 'track', 'limit': 1}
            track_search_response = get(
                track_search_url, headers=headers, params=search_params)
            tracks_data = track_search_response.json()['tracks']['items']

            # Skip track search if still not found
            if not tracks_data:
                continue

        track_uri = tracks_data[0]['uri']

        # Add the song to 'playlist_name' playlist
        add_track_response = post(
            f'https://api.spotify.com/v1/playlists/{playlist_id}/tracks', headers=headers, json={'uris': [track_uri]})
        print(add_track_response.status_code)

    return jsonify({"status": "success"})


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)
