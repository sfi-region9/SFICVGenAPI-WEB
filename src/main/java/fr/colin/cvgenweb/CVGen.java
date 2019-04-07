package fr.colin.cvgenweb;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import fr.colin.cvgenweb.objects.Answers;
import fr.colin.cvgenweb.objects.CV;
import fr.colin.cvgenweb.objects.CVAnswer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import spark.Redirect;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static spark.Spark.*;


public class CVGen {


    public static String baseServeur = "http://cv.nwa2coco.fr:4444/";
    
    
    public static void main(String... args) {
        port(4444);

        setupRoutes();
    }


    private static void setupRoutes() {
        staticFileLocation("/publics");
        get("/hello/:name", (request, response) -> "Hello : " + request.params("name"));
        get("/hello", (request, response) -> "Hello, world");

        get("/upload_raw", (request, response) -> "Hello World");

        post("/upload_raw", ((request, response) -> {

            String imageId = request.queryParams("radios"); //only one
            String crtAssigment = request.queryParams("crtassignment");
            String scc = request.queryParams("scc");
            String name = request.queryParams("name");
            String crtPosition = request.queryParams("crtposition");
            String text = request.queryParams("in");
            String crtRank = request.queryParams("crtrank");
            String crtSection = request.queryParams("crtsection");

            CV cv = new CV(Integer.parseInt(imageId), name, scc, crtAssigment, crtRank, crtPosition, crtSection, text);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(cv));
            Request request1 = new Request.Builder().url("http://cv.nwa2coco.fr:4444:4444/upload").post(requestBody).build();
            com.squareup.okhttp.Response response1 = new OkHttpClient().newCall(request1).execute();

            response.type("application/json");
            response.status(300);
            System.out.println(response1.body().string());
            CVAnswer cvAnswer = new Gson().fromJson(response1.body().string(), CVAnswer.class);
            response.body(new Gson().toJson(new Answers("Uploaded succefully uploaded", 300)));
            response.redirect(cvAnswer.getPdfDownload());

            return new Gson().toJson(new Answers("Uploaded succefully uploaded", 300));
        }));

        get("/upload_success/:pdf/:png", (request, response) -> {
            String pdf = request.params("pdf").replace("_", "/").replace("download/pdf", "download_pdf");
            String png = request.params("png").replace("_", "/").replace("download/img", "download_img");
            System.out.println(pdf);
            System.out.println(png);
            return "<html>" +
                    "<head>" +
                    "<script type=\"text/javascript\">var p = 0; " +
                    "function fs(){var ls = document.getElementById('pdf'); ls.onclick = incrementPdf; var lse = document.getElementById('png'); lse.onclick = incrementPng;}" +
                    "function incrementPdf(){p = p+1; console.log(p); if(p==1){document.location.href =" + pdf + "; document.location.href = \"/\";}}" +
                    "function incrementPdf(){p = p+1; console.log(p); if(p==1){document.location.href =" + png + "; document.location.href = \"/\";}}" +
                    "</script>" +
                    "<title>Download Links</title>" +
                    "</head>" +
                    "<body onload=\"fs()\">" +
                    "<button id=\"pdf\">Download PDF Version</button>" +
                    "</br></br>" +
                    "<button id=\"png\">Download PNG Version</button>" +
                    "</body>" +
                    "</html>";
        });

        post("/upload", (request, response) -> {
            CV cv = new Gson().fromJson(request.body(), CV.class);
            String s = cv.process();
            Answers answer = null;

            if (s.equalsIgnoreCase("Error")) {
                answer = new Answers("Error occured in uploading", 500);
                response.type("application/json");
                response.body(new Gson().toJson(answer));
                response.status(301);
                return new Gson().toJson(answer);
            }

            CVAnswer answer1 = new CVAnswer("Your CV is succefully uploaded.", 300, "http://cv.nwa2coco.fr:4444/file/" + s + "/download_pdf", "");
            response.type("application/json");
            response.status(301);
            response.body(new Gson().toJson(answer1));
            return new Gson().toJson(answer1);
        });

        get("/file/:id/download_pdf", (request, response) -> {
            String id = request.params("id");
            if (checkExistance(response, id))
                return new Gson().toJson(new Answers("Error occured in downloading", 500));
            File f = new File("/home/cso/cvgen_results/" + id + ".pdf");
            byte[] data = Files.readAllBytes(Paths.get(f.toURI()));
            response.type("application/pdf");
            response.header("Content-Disposition", "inline; filename=" + id + ".pdf");
            response.raw().getOutputStream().write(data);
            response.raw().getOutputStream().close();
            FileUtils.forceDelete(f);
            FileUtils.deleteDirectory(new File("/home/cso/cvgen_results/" + id + "/"));
            return new Gson().toJson(new Answers("File is succefully downloaded", 301));
        });
        get("/image_convertissor", (req, res) ->
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>FirstFewLines.com - SparkJava file upload example</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h2>FirstFewLines.com - SparkJava file upload example</h2>\n" +
                        "<hr>\n" +
                        "<div style=\"display:block\">\n" +
                        "    <form action=\"/convert_to_img\" method=\"post\" enctype=\"multipart/form-data\">\n" +
                        "        <label for=\"myfile\">Select a file</label>\n" +
                        "        <input type=\"file\" id=\"myfile\" name=\"myfile\" accept=\".pdf\"/>\n" +
                        "        <input type=\"submit\" id=\"buttonUpload\" value=\"Upload\"/>\n" +
                        "        <br>\n" +
                        "        <p>Result:&nbsp;:<span id=\"result\"></span></p>\n" +
                        "    </form>\n" +
                        "</div>\n" +
                        "</body>\n" +
                        "</html>\n"
        );


        File uploadDir = new File("/home/cso/uploading/");
        uploadDir.mkdir();
        post("/convert_to_img", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/home/cso/temps"));
            Part filePart = request.raw().getPart("myfile");
            try (InputStream inputStream = filePart.getInputStream()) {
                OutputStream outputStream = new FileOutputStream("/home/cso/temps/" + filePart.getSubmittedFileName());
                IOUtils.copy(inputStream, outputStream);
                outputStream.close();
            }
            File file = new File("/home/cso/temps/" + filePart.getSubmittedFileName());

            PDDocument document1 = PDDocument.load(file);
            PDFRenderer pdfRenderer = new PDFRenderer(document1);

            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
            String fileName = "/home/cso/temps/" + file.getName().replace(".pdf", ".png");
            ImageIOUtil.writeImage(bufferedImage, fileName, 300);

            File fN = new File(fileName);

            byte[] data = Files.readAllBytes(Paths.get(fN.toURI()));
            response.type("application/png");
            response.header("Content-Disposition", "inline; filename=" + fN.getName().replace(" ", "_"));
            response.raw().getOutputStream().write(data);
            response.raw().getOutputStream().close();
            FileUtils.forceDelete(file);
            FileUtils.forceDelete(fN);
            response.redirect("/");
            return new Gson().toJson(new Answers("File is succefully converted", 300));
        });


    }

    private static boolean checkExistance(Response response, String id) {
        if (!exist(id)) {
            response.type("application/json");
            response.body(new Gson().toJson(new Answers("Error occured in uploading", 500)));
            response.status(500);
            return true;
        }
        return false;
    }


    private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }


    public static boolean exist(String id) {
        return new File("/home/cso/cvgen_results/" + id + ".pdf").exists();
    }


}
