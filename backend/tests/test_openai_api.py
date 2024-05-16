import json
from unittest.mock import patch

import pytest

def test_request_filter(client):
    # Data to pass into request filter endpoint
    data = {
        "amount": 5,
        "type": "Genre",
        "previous_filter": ["Study", "Chill"],
        "previous_response": []
    }
    # Send post request to request filter endpoint with data
    with patch('backend.request_openAI') as mock_req:
        # Mock the post request
        mock_req.return_value = '{"filters": ["Pop", "Rock", "Hip-hop", "Electronic", "Dance", "EDM", "Indie"]}'

        # Send post request to request filter endpoint with data
        response = client.post("/request-filter", json=data)

    # Check response is success or not
    assert response.status_code == 200

    # Load data to python dictionary from json format
    response_data = json.loads(response.data)

    # Check if OpenAI API instruction formatting is correct
    assert "filters" in response_data
    assert isinstance(response_data["filters"], list)

    # Check amount of filters is same or greater than the requested amount
    assert len(response_data["filters"]) >= data["amount"]


def test_request_playlist(client):
    # Data to pass into request-playlist endpoint
    data = {
        "amount": 10,
        "filters": ["Road Trip Anthems", "Chill Out", "Hip Hop", "Indie", "2020s", "2010s", "Billie Eilish"]
    }
    # Mocking the OpenAI API call
    with patch('backend.request_openAI') as mock_req:
        mock_req.return_value = (
            '{"title": "Eclectic Vibes - Generated using BeatBlendr", "playlist": ['
            '{"artist": "Imagine Dragons", "track": "Believer"},'
            '{"artist": "Lorde", "track": "Royals"},'
            '{"artist": "Kendrick Lamar", "track": "HUMBLE."},'
            '{"artist": "The Lumineers", "track": "Ho Hey"},'
            '{"artist": "Dua Lipa", "track": "Don''t Start Now"}, '
            '{"artist": "Arctic Monkeys", "track": "Do I Wanna Know?"}, '
            '{"artist": "Drake", "track": "In My Feelings"}, '
            '{"artist": "Vance Joy", "track": "Riptide"}, '
            '{"artist": "SZA", "track": "The Weekend"}, '
            '{"artist": "Foster The People", "track": "Pumped Up Kicks"}, '
            '{"artist": "Childish Gambino", "track": "Redbone"}, '
            '{"artist": "Hozier", "track": "Take Me to Church"}, '
            '{"artist": "Ariana Grande", "track": "thank u, next"}, '
            '{"artist": "Billie Eilish", "track": "bad guy"}, '
            '{"artist": "Twenty One Pilots", "track": "Stressed Out"}]}'
        )
        # response from OpenAI API (response is a mocked response)
        response = client.post("/request-playlist", json=data)

    # Check success status code
    assert response.status_code == 200
    # Convert json to python dictionary
    response_data = json.loads(response.data)

    # Check OpenAI instruction formatting
    assert "title" in response_data
    assert "playlist" in response_data
    assert isinstance(response_data["playlist"], list)

    # Check if number of songs in playlist matches the requested amount
    assert len(response_data["playlist"]) >= data["amount"]
