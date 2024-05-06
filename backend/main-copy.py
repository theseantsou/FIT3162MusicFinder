# Import Libraries
import os
import base64
from requests import post, get
import json
from flask import Flask, redirect, request, jsonify, session
import urllib.parse
import datetime

# Name of Flask app and Secret Key
app = Flask(__name__)
app.secret_key = 'AASDasd1345t6dcycy7-y78g87gf-FYTfyuf78'

# URLs and IDs
CLIENT_ID = "af3562ebce014ac0bee41e382e861f5b"
CLIENT_SECRET = "75576491633240049d1c0483232cc865"
REDIRECT_URI = "http://localhost:5000/callback"
AUTH_URL = "https://accounts.spotify.com/authorize"
TOKEN_URL = "https://accounts.spotify.com/api/token"
API_BASE_URL = "https://api.spotify.com"

# Main App Page


@app.route('/')
def index():
    return "Welcome <a href='/login'>Login Here</a>"

# Login App Page


@app.route('/login')
def login():

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
    return redirect(auth_url)

# Callback app page


@app.route('/callback')
def callback():
    req_body = None

    # Check for error in response
    if 'error' in request.args:
        return jsonify({"error": request.args['error']})

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
        return jsonify({"error": "No authorization code provided"})

    # Send request with auth code
    response = post(TOKEN_URL, data=req_body)
    token_info = response.json()

    # Store token information in session
    session['access_token'] = token_info['access_token']
    session['refresh_token'] = token_info['refresh_token']
    session['expires_at'] = datetime.datetime.now().timestamp() + \
        token_info['expires_in']
    print(session)
    return redirect('/populate_playlist')

# Add songs to playlist


@app.route('/populate-playlist', methods=["POST"])
def populate_user_playlist():

    # Define access token, if not get it
    access_token = session.get('access_token')
    if not access_token:
        return redirect('/login')

    # If session has expired, reset token
    if datetime.datetime.now().timestamp() > session['expires_at']:
        return redirect('/refresh_token')

    # Required for authorisation
    headers = {'Authorization': f'Bearer {access_token}'}

    # Define list of songs in Json, and playlist name
    songlist = [{'artist': 'The Killers', 'track': 'Mr. Brightside'}, {'artist': 'Calvin Harris', 'track': 'Feel So Close'}, {'artist': 'Vampire Weekend', 'track': 'A-Punk'}, {'artist': 'ODESZA', 'track': 'Say My Name (feat. Zyra)'}, {'artist': 'The Strokes', 'track': 'Last Nite'}, {'artist': 'Taylor Swift', 'track': 'Shake It Off'}, {'artist': 'Kanye West', 'track': 'Stronger'}, {'artist': 'Lana Del Rey', 'track': 'Summertime Sadness'}, {'artist': 'Mumford & Sons', 'track': 'I Will Wait'}, {'artist': 'Drake', 'track': 'Hotline Bling'}, {'artist': 'Foster The People', 'track': 'Pumped Up Kicks'}, {'artist': 'Disclosure', 'track': 'Latch (feat. Sam Smith)'}, {'artist': 'Two Door Cinema Club', 'track': 'What You Know'}, {'artist': 'Charli XCX', 'track': 'Boom Clap'}, {'artist': 'Bastille', 'track': 'Pompeii'}, {'artist': 'Kygo', 'track': 'Stole the Show (feat. Parson James)'}, {'artist': 'Arctic Monkeys', 'track': 'Do I Wanna Know?'}, {'artist': 'Billie Eilish', 'track': 'bad guy'}, {'artist': 'Twenty One Pilots', 'track': 'Stressed Out'}, {'artist': 'The Chainsmokers', 'track': 'Closer (feat. Halsey)'}, {'artist': 'Paramore', 'track': "Ain't It Fun"}, {'artist': 'Sam Smith', 'track': 'Stay With Me'}, {'artist': 'MGMT', 'track': 'Kids'}, {'artist': 'Dua Lipa', 'track': "Don't Start Now"}, {'artist': 'Imagine Dragons', 'track': 'Radioactive'}, {
        'artist': 'Troye Sivan', 'track': 'My My My!'}, {'artist': 'Hozier', 'track': 'Take Me to Church'}, {'artist': 'Rihanna', 'track': 'We Found Love (feat. Calvin Harris)'}, {'artist': 'Fleet Foxes', 'track': 'Mykonos'}, {'artist': 'Kendrick Lamar', 'track': 'HUMBLE.'}, {'artist': 'Florence + The Machine', 'track': 'Dog Days Are Over'}, {'artist': 'Alessia Cara', 'track': 'Here'}, {'artist': 'Ed Sheeran', 'track': 'Shape of You'}, {'artist': 'SZA', 'track': 'Good Days'}, {'artist': 'Maroon 5', 'track': 'Sugar'}, {'artist': 'Lorde', 'track': 'Royals'}, {'artist': 'Khalid', 'track': 'Young Dumb & Broke'}, {'artist': 'The 1975', 'track': 'Somebody Else'}, {'artist': 'M83', 'track': 'Midnight City'}, {'artist': 'Lewis Capaldi', 'track': 'Someone You Loved'}, {'artist': 'Panic! At The Disco', 'track': 'High Hopes'}, {'artist': 'Ariana Grande', 'track': 'thank u, next'}, {'artist': 'Coldplay', 'track': 'Viva La Vida'}, {'artist': 'Childish Gambino', 'track': 'Redbone'}, {'artist': 'Tame Impala', 'track': 'The Less I Know The Better'}, {'artist': 'Halsey', 'track': 'Without Me'}, {'artist': 'Vance Joy', 'track': 'Riptide'}, {'artist': 'Travis Scott', 'track': 'SICKO MODE'}, {'artist': 'Maggie Rogers', 'track': 'Light On'}, {'artist': 'Beyonce', 'track': 'Crazy in Love (feat. Jay-Z)'}]
    playlist_name = "Music Finder"

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
        f'https: // api.spotify.com/v1/users/{user_id}/playlists', headers=headers, json={'name': playlist_name, 'public': False})
    if create_playlist_response.status_code == 201:
        playlist_id = create_playlist_response.json()['id']
    else:
        return f'Failed to create {playlist_name} playlist.'

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
                print(f'Song {track["track"]} not found')
                continue

        track_uri = tracks_data[0]['uri']

        # Add the song to 'playlist_name' playlist
        add_track_response = post(
            f'https: // api.spotify.com/v1/playlists/{playlist_id}/tracks', headers=headers, json={'uris': [track_uri]})
        print(add_track_response.status_code)

    return f'Music added to {playlist_name}'

# Unused - May have to use for future features -


@ app.route('/playlists')
def get_playlists():

    if 'access_token' not in session:
        return redirect('/login')

    if datetime.datetime.now().timestamp() > session['expires_at']:
        return redirect('/refresh_token')

    headers = {
        'Authorization': f'Bearer {session["access_token"]}'
    }

    response = get(API_BASE_URL + '/v1/me/playlists', headers=headers)

    playlists = response.json()

    return jsonify(playlists)


@ app.route('/refresh_token')
def refresh_token():
    if 'refresh_token' not in session:
        return redirect('/login')

    if datetime.datetime.now().timestamp() > session['expires_at']:
        req_body = {
            'grant_type': 'refresh_token',
            'refresh_token': session['expires_at'],
            'client_id': CLIENT_ID,
            'client_secret': CLIENT_SECRET
        }

        response = post(TOKEN_URL, data=req_body)
        new_token_info = response.json()

        session['access_token'] = new_token_info['access_token']
        session['expires_at'] = datetime.datetime.now().timestamp() + \
            new_token_info['expires_in']

        return redirect('/populate_playlist')


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True, port=8888)
