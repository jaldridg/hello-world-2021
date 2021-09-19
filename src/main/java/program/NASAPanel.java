package program;
import java.awt.*;
import org.json.JSONObject;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.imageio.*;
import java.util.Scanner;

public class NASAPanel extends JPanel {
    private JPanel panel;
    private HttpURLConnection con;
    private String nasaAuth = "";
    private JSONObject getRequestJSON;
    private BufferedImage buffImg;
    private Dimension size;
    public NASAPanel(Dimension size) {
        this.size = size;
        this.setMinimumSize(size);
        this.setPreferredSize(size);
        this.setBackground(Color.darkGray);
        try {
            File authFile = new File("authkey.txt");
            Scanner sc = new Scanner(authFile);
            nasaAuth = sc.nextLine();
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    // CREDIT: https://zetcode.com/java/getpostrequest/
    public JSONObject getRequest(String requestURL, String authType) {
        if(authType.equalsIgnoreCase("NASA")) {
            requestURL = requestURL.replace("APIKEY", nasaAuth);
        }
        try {
            URL url = new URL(requestURL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            StringBuilder content;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;
                content = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
                con.disconnect();
                return (new JSONObject(content.toString()));
            } catch (Exception e) {
                e.printStackTrace();
                con.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        return null;
    }
    public void doGet(String requestURL, String authType, boolean downloadImage) {
        getRequestJSON = getRequest(requestURL, authType);
        if(downloadImage) {
            try {
                buffImg = ImageIO.read(new URL(getRequestJSON.getString("url")));
                Image result = buffImg.getScaledInstance(size.width, size.height, Image.SCALE_DEFAULT);
                buffImg.getGraphics().drawImage(result, 0, 0, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if(buffImg != null) {
            int w = buffImg.getWidth();
            int h = buffImg.getHeight();
            g2.drawImage(buffImg, 0, 0, w, h, null);

        }


    }









}