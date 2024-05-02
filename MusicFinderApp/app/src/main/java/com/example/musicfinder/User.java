package com.example.musicfinder;

import java.util.List;

public class User {
    private int id;
    private String userName;
    private List<Playlist> favorites;
    private List<Playlist> history;

    public User(int id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public List<Playlist> getFavorites() {
        return favorites;
    }

    public List<Playlist> getHistory() {
        return history;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFavorites(List<Playlist> favorites) {
        this.favorites = favorites;
    }

    public void setHistory(List<Playlist> history) {
        this.history = history;
    }

    public String getFavoritesAsString() {
        StringBuilder output = new StringBuilder();
        int numOfFav = this.favorites.size();
        int atMostThree = Math.min(numOfFav, 3);
        for (int i = 0; i < atMostThree; i++)
        {
            output.append("Playlist ").append(i).append("{ ").append(this.favorites.get(numOfFav - 1 - i).toString()).append(" }\n");
        }
        return output.toString();
    }

    public String getHistoryAsString() {
        StringBuilder output = new StringBuilder();
        int numOfHist = this.history.size();
        int atMostThree = Math.min(numOfHist, 3);
        for (int i = 0; i < atMostThree; i++)
        {
            output.append("Playlist ").append(i).append("{ ").append(this.history.get(numOfHist - 1 - i).toString()).append(" }\n");
        }
        return output.toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                '}';
    }
}
