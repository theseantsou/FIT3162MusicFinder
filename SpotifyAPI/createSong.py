from dotenv import load_dotenv
import os
import base64
from requests import post, get
import json
from flask import Flask, redirect, request, jsonify, session
import urllib.parse
import datetime


from spotifyClient import SpotifyClient

app = Flask(__name__)
app.secret_key = 'AASDasd1345t6dcycy7-y78g87gf-FYTfyuf78'

CLIENT_ID="af3562ebce014ac0bee41e382e861f5b"
CLIENT_SECRET="75576491633240049d1c0483232cc865"
REDIRECT_URI ="http://localhost:5000/callback"
AUTH_URL =  "https://accounts.spotify.com/authorize"
TOKEN_URL = "https://accounts.spotify.com/api/token"
#API_BASE_URL = "https://accounts.spotify.com/v1"
API_BASE_URL = "https://api.spotify.com"

@app.route('/')
def index():
    return "Welcome <a href='/login'>Login Here</a>"

@app.route('/login')
def login():
    #scope = 'playlist-modify-public playlist-modify-private'
    scope = 'user-read-private user-read-email playlist-modify-public playlist-modify-private'

    auth_params = {
        'client_id': CLIENT_ID,
        'response_type': 'code',
        'redirect_uri': REDIRECT_URI,
        'scope': scope,
        'show_dialog': True
    }

    auth_url = f"{AUTH_URL}?{urllib.parse.urlencode(auth_params)}"
    return redirect(auth_url)

@app.route('/callback')
def callback():
    req_body = None

    if 'error' in request.args:
        return jsonify({"error": request.args['error']})
    
    if 'code' in request.args:
        req_body = {
            'code': request.args['code'],
            'grant_type': 'authorization_code',
            'redirect_uri': REDIRECT_URI,
            'client_id': CLIENT_ID,
            'client_secret': CLIENT_SECRET
        }

    if req_body is None:
        return jsonify({"error": "No authorization code provided"})

    #print(req_body)
    response = post(TOKEN_URL, data=req_body)
    #print("Bingo")
    #print(response.content)
    #print("Bingo")

    token_info = response.json()

    session['access_token'] = token_info['access_token']
    session['refresh_token'] = token_info['refresh_token']
    session['expires_at'] = datetime.datetime.now().timestamp() + token_info['expires_in']   

    return redirect('/add-love-song')


@app.route('/add-love-song')
def add_love_song():
    access_token = session.get('access_token')
    if not access_token:
        return redirect('/login')
    
    headers = {'Authorization': f'Bearer {access_token}'}

    # Step 1: Check if 'MusicFinder' playlist exists, if not, create it
    playlist_id = None
    playlists_url = 'https://api.spotify.com/v1/me/playlists'
    response = get(playlists_url, headers=headers)
    if response.status_code == 200:
        playlists_data = response.json()
        for playlist in playlists_data['items']:
            if playlist['name'] == 'MusicFinder':
                playlist_id = playlist['id']
                break
    
    # If 'MusicFinder' playlist doesn't exist, create it
    if not playlist_id:
        user_profile_response = get('https://api.spotify.com/v1/me', headers=headers)
        user_id = user_profile_response.json()['id']
        create_playlist_response = post(f'https://api.spotify.com/v1/users/{user_id}/playlists', headers=headers, json={'name': 'MusicFinder', 'public': False})
        if create_playlist_response.status_code == 201:
            playlist_id = create_playlist_response.json()['id']
        else:
            return "Failed to create 'MusicFinder' playlist."

    # Step 2: Find the URI of the song 'Love'
    track_search_url = 'https://api.spotify.com/v1/search'
    search_params = {'q': 'Love', 'type': 'track', 'limit': 1}
    track_search_response = get(track_search_url, headers=headers, params=search_params)
    tracks_data = track_search_response.json()['tracks']['items']
    if not tracks_data:
        return "Song 'Love' not found."
    track_uri = tracks_data[0]['uri']

    # Step 3: Add the song to 'MusicFinder' playlist
    add_track_response = post(f'https://api.spotify.com/v1/playlists/{playlist_id}/tracks', headers=headers, json={'uris': [track_uri]})
    if add_track_response.status_code != 201:
        print(add_track_response.json())  # 
        if add_track_response.status_code != 201:
            if add_track_response.status_code == 200:
                return "'Love' have already been in 'MusicFinder' playlist."
            else:
                return f"Failed to add 'Love' to 'MusicFinder' playlist, status code: {add_track_response.status_code}"

    return "'Love' added to 'MusicFinder' playlist successfully."

@app.route('/playlists')
def get_playlists():
    if 'access_token' not in session:
        #print("A")
        return redirect('/login')
    
    if datetime.datetime.now().timestamp() > session['expires_at']:
        #print("B")
        return redirect('/refresh_token')
    
    headers = {
        'Authorization': f'Bearer {session['access_token']}'
    }

    #print("C")
    response = get(API_BASE_URL + '/v1/me/playlists', headers=headers)
    #print("D")
    #print(response)
    #print("E")
    #print(response.content)
    #print("F")

    playlists = response.json()
    #print("E")

    return jsonify(playlists)

@app.route('/refresh_token')
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
        session['expires_at'] = datetime.datetime.now().timestamp() + new_token_info['expires_in']    
        
        return redirect('/playlists')
    
if __name__ == '__main__':
     app.run(host='0.0.0.0', debug=True)
     