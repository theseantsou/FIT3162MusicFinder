from flask import Flask, request, redirect, session
import requests
import webbrowser
import os

app = Flask(__name__)
app.secret_key = 'randomkey'

# 使用你从Spotify开发者仪表板获取的客户端ID和密钥
CLIENT_ID = '0888c9ec00b14193a22603c31f8fc86c'
CLIENT_SECRET = '174de6d5a4ad4aa49c78048c1c73e03a'
REDIRECT_URI = 'http://127.0.0.1:5000/callback'
SCOPE = 'user-library-read user-read-private playlist-read-private playlist-read-collaborative playlist-modify-public playlist-modify-private user-follow-read user-read-currently-playing user-read-recently-played'  # 修改为所需的权限
AUTH_URL = 'https://accounts.spotify.com/authorize'

@app.route('/login')
def login():
    auth_url = f"{AUTH_URL}?client_id={CLIENT_ID}&response_type=code&redirect_uri={REDIRECT_URI}&scope={SCOPE}"
    return redirect(auth_url)

@app.route('/callback')
def callback():
    code = request.args.get('code')
    # 交换授权码
    auth_response = requests.post('https://accounts.spotify.com/api/token', data={
        'grant_type': 'authorization_code',
        'code': code,
        'redirect_uri': REDIRECT_URI,
        'client_id': CLIENT_ID,
        'client_secret': CLIENT_SECRET,
    })
    if auth_response.status_code != 200:
        print("Failed to fetch access token")
        return f"Error fetching access token, status code: {auth_response.status_code}"


    auth_response_data = auth_response.json()
    access_token = auth_response_data['access_token']
    if access_token is None:
        print("Access token not found in response")
        return "Error, access token not found."
    
    # 在这里使用access_token
    # 获取用户的Spotify资料
    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    user_profile_response = requests.get('https://api.spotify.com/v1/me', headers=headers)
    user_profile_data = user_profile_response.json()
    print(user_profile_data) 

    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    # Spotify获取用户保存的曲目的URL
    saved_tracks_url = 'https://api.spotify.com/v1/me/tracks?limit=50'  # limit可以根据您的需要调整

    # 发送请求获取用户保存的曲目
    response = requests.get(saved_tracks_url, headers=headers)
    saved_tracks_data = response.json()

    # 提取和打印每一首曲目的名字
    tracks = saved_tracks_data.get('items', [])
    for item in tracks:
        track = item.get('track', {})
        print(track.get('name'))  # 打印歌曲名字

    # 这里只是返回所有曲目的简单列表
    print({'tracks': [track.get('track', {}).get('name') for track in tracks]})

    combined_data = {
        'user_info': user_profile_data,
        'saved_tracks': tracks
    }


#save combined_data in save.txt but drop 'available_markets' 
    with open('save.txt', 'w') as f:
        for key in combined_data:
            if key == 'saved_tracks':
                f.write(f"{key}: ")
                f.write('\n')
                for item in combined_data[key]:
                    for k, v in item['track'].items():
                        if k != 'available_markets':
                            f.write(f"{k}: {v}")
                            f.write('\n')
                    f.write('\n')
            else:
                f.write(f"{key}: {combined_data[key]}")
                f.write('\n')
        f.write('\n')


    return combined_data


@app.route('/logout')
def logout():
    # 清除session中的所有信息
    session.clear()
    # 重定向回登录页面
    return redirect('/login')


if __name__ == '__main__':
    webbrowser.open('http://127.0.0.1:5000/login')
    app.run(debug=True)
