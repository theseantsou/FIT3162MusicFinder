from flask import Flask, request, redirect, session, jsonify
import requests

app = Flask(__name__)
app.secret_key = 'randomkey'

CLIENT_ID = '0888c9ec00b14193a22603c31f8fc86c'
CLIENT_SECRET = '174de6d5a4ad4aa49c78048c1c73e03a'
REDIRECT_URI = 'http://127.0.0.1:5000/callback'
SCOPE = 'user-read-private'
AUTH_URL = 'https://accounts.spotify.com/authorize'

songs ={'playlist': [{'artist': 'Adele', 'track': 'Someone Like You'}, {'artist': 'Ed Sheeran', 'track': 'Thinking Out Loud'}, {'artist': 'Sam Smith', 'track': 'Stay With Me'}, {'artist': 'Norah Jones', 'track': "Don't Know Why"}, {'artist': 'Coldplay', 'track': 'The Scientist'}, {'artist': 'Sade', 'track': 'Smooth Operator'}, {'artist': 'Maroon 5', 'track': 'She Will Be Loved'}, {'artist': 'Alicia Keys', 'track': "If I Ain't Got You"}, {'artist': 'Jason Mraz', 'track': "I'm Yours"}, {'artist': 'Rihanna', 'track': 'Stay'}, {'artist': 'Justin Timberlake', 'track': 'Mirrors'}, {'artist': 'The Weeknd', 'track': 'Earned It'}, {'artist': 'Dua Lipa', 'track': 'New Rules'}, {'artist': 'Calvin Harris', 'track': 'Summer'}, {'artist': 'Zedd', 'track': 'Clarity'}, {'artist': 'Kygo', 'track': 'Stole the Show'}, {'artist': 'Avicii', 'track': 'Wake Me Up'}, {'artist': 'OneRepublic', 'track': 'Counting Stars'}, {'artist': 'Taylor Swift', 'track': 'Love Story'}, {'artist': 'Katy Perry', 'track': 'Firework'}, {'artist': 'Lady Gaga', 'track': 'Poker Face'}, {'artist': 'David Guetta', 'track': 'Titanium'}, {'artist': 'P!nk', 'track': 'Just Give Me a Reason'}, {'artist': 'The Chainsmokers', 'track': 'Closer'}, {'artist': 'Imagine Dragons', 'track': 'Radioactive'}, {'artist': 'Beyoncé', 'track': 'Halo'}, {'artist': 'Ariana Grande', 'track': 'One Last Time'}, {'artist': 'Charlie Puth', 'track': 'One Call Away'}, {'artist': 'Shakira', 'track': "Hips Don't Lie"}, {'artist': 'Nelly Furtado', 'track': 'Say It Right'}, {'artist': 'Usher', 'track': "DJ Got Us Fallin' In Love"}, {'artist': 'Sean Paul', 'track': 'Temperature'}, {'artist': 'Rita Ora', 'track': 'Your Song'}, {'artist': 'Robin Thicke', 'track': 'Blurred Lines'}, {'artist': 'Meghan Trainor', 'track': 'All About That Bass'}, {'artist': 'Ne-Yo', 'track': 'So Sick'}, {'artist': 'Enrique Iglesias', 'track': 'Hero'}, {'artist': 'Christina Aguilera', 'track': 'Beautiful'}, {'artist': 'Shawn Mendes', 'track': 'Treat You Better'}, {'artist': 'John Legend', 'track': 'All of Me'}, {'artist': 'Bruno Mars', 'track': 'Just the Way You Are'}, {'artist': 'Adele', 'track': 'Hello'}, {'artist': 'Ed Sheeran', 'track': 'Perfect'}, {'artist': 'Sam Smith', 'track': "I'm Not The Only One"}, {'artist': 'Maroon 5', 'track': 'Sugar'}, {'artist': 'Alicia Keys', 'track': 'No One'}, {'artist': 'Rihanna', 'track': 'Diamonds'}, {'artist': 'Justin Timberlake', 'track': 'Cry Me a River'}, {'artist': 'The Weeknd', 'track': "Can't Feel My Face"}, {'artist': 'Dua Lipa', 'track': "Don't Start Now"}, {'artist': 'Calvin Harris', 'track': 'Feel So Close'}, {'artist': 'Zedd', 'track': 'Stay'}, {'artist': 'Kygo', 'track': 'Firestone'}]}

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
    return redirect('/get-song-previews')

@app.route('/get-song-previews')
def get_song_previews():
    access_token = session.get('access_token')
    if not access_token:
        return redirect('/login')

    headers = {'Authorization': f'Bearer {access_token}'}
    previews = []

    for song in songs['playlist']:
        query = f"{song['track']} artist:{song['artist']}"
        search_url = f"https://api.spotify.com/v1/search?q={query}&type=track&limit=1"
        response = requests.get(search_url, headers=headers)
        if response.status_code == 200:
            track_data = response.json().get('tracks', {}).get('items', [])
            if track_data:
                track_info = track_data[0]
                previews.append({
                    'artist': song['artist'],
                    'track': song['track'],
                    'preview_url': track_info.get('preview_url', 'No preview available')
                })

    return jsonify(previews)

@app.route('/logout')
def logout():
    session.clear()
    return redirect('/login')

if __name__ == '__main__':
    app.run(debug=True)
