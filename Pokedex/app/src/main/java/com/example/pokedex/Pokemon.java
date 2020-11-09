package com.example.pokedex;

// pokemon class to be instantiated per pokemon row in recyclerview
public class Pokemon {
    private String name;
    private String url;
//    private boolean caught;

    Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
//        this.caught = caught;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

//    public boolean getCaught() { return caught; }
}
