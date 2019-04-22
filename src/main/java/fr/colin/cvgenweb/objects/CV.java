package fr.colin.cvgenweb.objects;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CV {

    private int imageid;
    private String name;
    private String scc;
    private String currentAssignment;
    private String currentRank;
    private String currentPosition;
    private String currentSection;
    private String text;


    public CV(int imageid, String name, String scc, String currentAssignment, String currentRank, String currentPosition, String currentSection, String text) {
        this.imageid = imageid;
        this.name = name;
        this.scc = scc;
        this.currentAssignment = currentAssignment;
        this.currentRank = currentRank;
        this.currentPosition = currentPosition;
        this.currentSection = currentSection;
        this.text = text;
    }

    public static void main(String... args) throws IOException {
        ArrayList<String> s = new ArrayList<>();
        OkHttpClient c = new OkHttpClient();
        c.setWriteTimeout(10, TimeUnit.HOURS);
        c.setConnectTimeout(10, TimeUnit.HOURS);
        for (int i = 1; i < (10 * 10 * 10 * 10 * 10 * 10 * 5); i++) {
            s.add("" + String.valueOf(i));
        }

        Request r = new Request.Builder().post(RequestBody.create(MediaType.parse("application/json"), String.format("{\"imageid\":1,\"name\":\"Colin THOMAS\",\"scc\":\"SCC#79341\",\"currentAssignment\":\"\",\"currentRank\":\"\",\"currentPosition\":\"\",\"currentSection\":\"\",\"text\":\"%s\"}", StringUtils.join(s, "\n")))).url("https://cv.nwa2coco.fr/upload").build();
        c.newCall(r).execute();
    }

    public String getName() {
        return name;
    }

    public int getImageid() {
        return imageid;
    }

    public String getText() {
        return text;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public String getCurrentSection() {
        return currentSection;
    }

    public String getCurrentAssignment() {
        return currentAssignment;
    }

    public String getCurrentRank() {
        return currentRank;
    }

    public String getScc() {
        return scc;
    }

    public String process() {
        String id = UUID.randomUUID().toString();
        ClassLoader classLoader = this.getClass().getClassLoader();
        String imageFile = "";
        switch (imageid) {
            case 2:
                imageFile = "img/cv01.jpg";
                break;
            case 1:
                imageFile = "img/cv02.jpg";
                break;
            default:
                imageFile = "img/cv02.jpg";
                break;
        }
        URL url = null;
        try {
            url = classLoader.getResource(imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String destination = "/home/cso/cvgen_results/" + id + ".pdf";

        Image image;
        try {
            image = Image.getInstance(url);
        } catch (BadElementException | IOException e) {
            e.printStackTrace();
            return "Error";
        }

        if (image == null) {
            return "Error";
        }

        Document document = new Document(PageSize.A4, 1, 1, 1, 1);

        try {
            //CREATE PDF
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(destination));
            writer.setStrictImageSequence(true);


            //CONFIGURING PDF
            document.addTitle(scc + " " + name + "'s CV");
            document.addCreator("SFI CV Generator, USS Versailles R9");
            document.addAuthor("USS Versailles R9");
            document.addCreationDate();
            document.addHeader("SCC", scc);
            document.open();


            //ADDING BACKGROUND IMAGE
            image.setAbsolutePosition(0, 0);
            image.scaleAbsolute(PageSize.A4);
            image.setAlignment(Image.UNDERLYING);


            //ADDING CONTENT
            document.add(image);

            //ADDING TEXT TO IMAGE (..... yes it is all the code )
            PdfContentByte canvas = writer.getDirectContent();
            BaseFont b = BaseFont.createFont("fonts/HandelGo.ttf", "", BaseFont.EMBEDDED);
            Font fsd = new Font(b, 15);
            Font fsdws = new Font(b, 15, Font.NORMAL, BaseColor.ORANGE);
            Font fsdw = new Font(b, 12, Font.NORMAL, BaseColor.ORANGE);
            String s = scc + " " + name + " SFI CV";
            String ass = "Current Assignment : " + currentAssignment;
            String rank = "Current Rank : " + currentRank;
            String position = "Current Position(s) : " + currentPosition;
            String section = "Current Section(s) : " + currentSection;


            Phrase pAssignment = new Phrase(ass, fsdw);
            Phrase pRank = new Phrase(rank, fsdw);
            Phrase pPosition = new Phrase(position, fsdw);
            Phrase pSection = new Phrase(section, fsdw);
            Phrase p = new Phrase(s, fsd);
            ArrayList<String> lines = new ArrayList<>(Arrays.asList(text.split("\n")));
            ArrayList<String> furtherLines = new ArrayList<>();

            if (imageid == 2) {
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, p, 50, 800, 0);
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, pAssignment, 130, 750, 0);
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, pRank, 130, 730, 0);
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, pSection, 130, 710, 0);
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, pPosition, 130, 690, 0);

                int x = 130;
                int y = 650;
                int actual = 0;
                for (String l : lines) {
                    String ls = "";
                    if (actual >= 51) {
                        furtherLines.add(lines.get(actual));
                        actual++;
                    } else {
                        if (l.length() > 70) {
                            ls = l.substring(0, 69);
                        } else {
                            ls = l;
                        }
                        Phrase phrase = new Phrase(ls, fsdw);
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, x, y, 0);
                        y -= 12;
                        actual++;
                    }
                }

                //CRT
            } else if (imageid == 1) {
                Phrase ps = new Phrase(s, new Font(b, 15, Font.NORMAL, BaseColor.ORANGE));
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, ps, 30, 800, 0);
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, pAssignment, 130, 670, 0);
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, pRank, 130, 650, 0);
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, pSection, 130, 630, 0);
                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, pPosition, 130, 610, 0);

                int x = 130;
                int y = 576;
                int actual = 0;
                for (String l : lines) {
                    String ls = "";
                    if (actual >= 45) {
                        furtherLines.add(lines.get(actual));
                        actual++;
                    } else {
                        if (l.length() > 68) {
                            ls = l.substring(0, 67);
                        } else {
                            ls = l;
                        }
                        Phrase phrase = new Phrase(ls, fsdw);
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, x, y, 0);
                        y -= 12;
                        actual++;
                    }
                }

            }

            if (furtherLines.size() > 0) {
                System.out.println("Newpage");
                int y = 830;
                int x = 130;
                document.newPage();
                Image nPage = Image.getInstance(classLoader.getResource("img/page3.jpg"));
                nPage.setAbsolutePosition(0, 0);
                nPage.scaleAbsolute(PageSize.A4);
                nPage.setAlignment(Image.UNDERLYING);
                document.add(nPage);

                //70 lignes
                int actual = 0;
                for (String l : furtherLines) {
                    if (actual == 67) {
                        actual = 0;
                        y = 842;
                        document.newPage();
                        document.add(nPage);
                    }
                    Phrase phrase = new Phrase(l, fsdw);
                    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, x, y, 0);
                    y -= 12;
                    actual++;
                }
            }

            document.close();
        } catch (DocumentException | IOException e) {
            document.close();
            e.printStackTrace();
            return "Error";
        }

        return id;
    }

}
