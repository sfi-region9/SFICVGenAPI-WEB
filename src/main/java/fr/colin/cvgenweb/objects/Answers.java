package fr.colin.cvgenweb.objects;

import com.google.gson.Gson;

public class Answers {

    private String message;
    private int id;

    public Answers(String message, int id) {
        this.message = message;
        this.id = id;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
