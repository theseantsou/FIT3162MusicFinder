from flask import Flask, request, redirect, session
import requests

app = Flask(__name__)
app.secret_key = 'randomkey'

CLIENT_ID = '0888c9ec00b14193a22603c31f8fc86c'
CLIENT_SECRET = '174de6d5a4ad4aa49c78048c1c73e03a'
REDIRECT_URI = 'http://127.0.0.1:5000/callback'
SCOPE = 'playlist-modify-public playlist-modify-private user-read-private'
AUTH_URL = 'https://accounts.spotify.com/authorize'

@app.route('/login')
def login():
    auth_url = f"{AUTH_URL}?client_id={CLIENT_ID}&response_type=code&redirect_uri={REDIRECT_URI}&scope={SCOPE}"
    return redirect(auth_url)

@app.route('/callback')
def callback():
    code = request.args.get('code')
    auth_response = requests.post('https://accounts.spotify.com/api/token', data={
        'grant_type': 'authorization_code',
        'code': code,
        'redirect_uri': REDIRECT_URI,
        'client_id': CLIENT_ID,
        'client_secret': CLIENT_SECRET,
    })
    
    if auth_response.status_code != 200:
        return f"Error fetching access token, status code: {auth_response.status_code}"
    
    access_token = auth_response.json().get('access_token')
    if access_token is None:
        return "Error, access token not found."

    session['access_token'] = access_token
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
    response = requests.get(playlists_url, headers=headers)
    if response.status_code == 200:
        playlists_data = response.json()
        for playlist in playlists_data['items']:
            if playlist['name'] == 'MusicFinder':
                playlist_id = playlist['id']
                break
    
    # If 'MusicFinder' playlist doesn't exist, create it
    if not playlist_id:
        user_profile_response = requests.get('https://api.spotify.com/v1/me', headers=headers)
        user_id = user_profile_response.json()['id']
        create_playlist_response = requests.post(f'https://api.spotify.com/v1/users/{user_id}/playlists', headers=headers, json={'name': 'MusicFinder', 'public': False})
        if create_playlist_response.status_code == 201:
            playlist_id = create_playlist_response.json()['id']
        else:
            return "Failed to create 'MusicFinder' playlist."

    # Step 2: Find the URI of the song 'Love'
    track_search_url = 'https://api.spotify.com/v1/search'
    search_params = {'q': 'Love', 'type': 'track', 'limit': 1}
    track_search_response = requests.get(track_search_url, headers=headers, params=search_params)
    tracks_data = track_search_response.json()['tracks']['items']
    if not tracks_data:
        return "Song 'Love' not found."
    track_uri = tracks_data[0]['uri']

    # Step 3: Add the song to 'MusicFinder' playlist
    add_track_response = requests.post(f'https://api.spotify.com/v1/playlists/{playlist_id}/tracks', headers=headers, json={'uris': [track_uri]})
    if add_track_response.status_code != 201:
        print(add_track_response.json())  # 打印响应正文以获取更多信息
        if add_track_response.status_code != 201:
            if add_track_response.status_code == 200:
                return "'Love' have already been in 'MusicFinder' playlist."
            else:
                return f"Failed to add 'Love' to 'MusicFinder' playlist, status code: {add_track_response.status_code}"

    return "'Love' added to 'MusicFinder' playlist successfully."

@app.route('/logout')
def logout():
    session.clear()
    return redirect('/login')

if __name__ == '__main__':
    app.run(debug=True)
