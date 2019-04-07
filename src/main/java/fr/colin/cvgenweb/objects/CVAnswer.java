package fr.colin.cvgenweb.objects;

import com.google.gson.Gson;

public class CVAnswer extends Answers {


    private String pdfDownload;
    private String pngDownload;

    public CVAnswer(String messages, int id, String pdfDownload, String pngDownload) {
        super(messages, id);
        this.pdfDownload = pdfDownload;
        this.pngDownload = pngDownload;
    }

    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public String getPdfDownload() {
        return pdfDownload;
    }

    public String getPngDownload() {
        return pngDownload;
    }

}
