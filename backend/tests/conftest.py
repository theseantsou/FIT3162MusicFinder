import pytest
from backend import app, db, SpotifyUser


@pytest.fixture
def client():
    with app.test_client() as client:
        yield client


@pytest.fixture
def db_client():
    # Set up a separate SQLite database for testing
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///:memory:'
    with app.app_context():
        db.create_all()
        yield db
        db.drop_all()


@pytest.fixture
def insert_user_client(db_client):
    # Test inserting a user into the database
    user = SpotifyUser(email='test@example.com', auth_token='token', refresh_token='refresh', expiry_time=0)
    db_client.session.add(user)
    db_client.session.commit()


