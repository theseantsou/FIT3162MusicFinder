from unittest.mock import patch
from backend import SpotifyUser, refresh_token


def test_retrieve_user(insert_user_client):
    # Test retrieving a user from the database
    user = SpotifyUser.query.filter_by(email='test@example.com').first()
    assert user is not None
    assert user.email == 'test@example.com'


def test_refresh_token(insert_user_client):
    expired_user = SpotifyUser.query.filter_by(email='test@example.com').first()

    # Mock the request to the OAuth token endpoint for token refresh
    with patch('backend.post') as mock_post:
        mock_post.return_value.json.return_value = {
            'access_token': 'new_access_token',
            'expires_in': 3600,  # Expiry time in seconds
            'refresh_token': 'new_refresh_token'
        }

        # Trigger token refresh
        refresh_token(expired_user)

    # Verify that the access token in the database is updated
    updated_user = SpotifyUser.query.filter_by(email='test@example.com').first()

    assert updated_user.auth_token == 'new_access_token'
