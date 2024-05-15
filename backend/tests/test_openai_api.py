import json
from unittest.mock import patch

import pytest


@pytest.mark.parametrize(
    "amount, filter_type, prev_filter, prev_resp",
    [
        (5, "Genre", ["Study", "Chill"], []),
        (5, "Occasion or Mood", [], []),
        (5, "Artist", ["Pop"], ["Justin Bieber", "Miley Cyrus", "Lady Gaga", "Michael Jackson"]),
        (5, "Time Period (Decade)", ["Shower", "Dance"], [])
    ]
)
def test_request_filter(client, amount, filter_type, prev_filter, prev_resp):
    # Data to pass into request filter endpoint
    data = {
        "amount": amount,
        "type": filter_type,
        "previous_filter": prev_filter,
        "previous_response": prev_resp
    }
    # Send post request to request filter endpoint with data
    with patch('backend.request_openAI') as mock_request_openAI:
        # Mock the post request
        mock_request_openAI.return_value = '{"filters": ["Pop", "Rock", "Hip-hop", "Electronic", "Dance"]}'

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


@pytest.mark.parametrize(
    "amount, filters",
    [
        (10, []),
        (20, ["Sad", "Chill", "2010s", "2020s", "Adele", "Billie Eilish"]),
        (30, ["2000s"]),
        (40, ["Pop", "Romance", "Ed Sheeran"]),
        (50, ["Happy", "Electronic", "Dance", "1990s", "2020s"])
    ]
)
def test_request_playlist(client, amount, filters):
    # Data to pass into request-playlist endpoint
    data = {
        "amount": amount,
        "filters": filters
    }
    # Send post request to request-playlist endpoint
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
