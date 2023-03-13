package com.just1t.dm.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.just1t.dm.entity.DM;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * @author just1t
 * @date 2023/3/3 18:32
 * @introduce 用于网页的工具类
 */
public class WebUtil {
    public static String fileLocation = "";
    public static String showSize = "";
    public static String kwToken = "";
    public static double nowSize = 0.0;
    public static final ExecutorService server = Executors.newFixedThreadPool(5);//通过线程池创建一个线程
    public static List<DM> dmList =new ArrayList<>();

    public static int stopTime;
    public static AdvancedPlayer player=null;

    public static URL nowMusic;

    public static List<DM> getDmList() {
        return dmList;
    }

    static {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = WebUtil.class.getClassLoader().getResourceAsStream("conf.properties");
            properties.load(inputStream);
            fileLocation = properties.getProperty("SAVE_location");
            showSize = properties.getProperty("SHOW_SIZE");
            kwToken = properties.getProperty("KW_TOKEN");
            File file = new File(fileLocation);
            //将保存的文件夹进行设置
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过音乐名下载文件
     *
     * @param keyword 关键字
     * @return 现在文件的位置
     */
    public static String getMUByName(String keyword) {
        String encode = WebUtil.encodeCH(keyword);
        String keyUrl = WebUtil.getUrl(encode);
        String html = WebUtil.getHtml(keyUrl, encode);
        List<DM> list = WebUtil.getList(html);
        dmList=list;
        return fileLocation;
    }

    /**
     * 下载list
     */
    public static void downloadList(List<DM> list,String encode,String keyword,int size){
        for (DM dm : WebUtil.getDownList(list, encode)) {
            System.out.println("下载的地址为："+dm.getUrl());
            downloadByUrl(dm.getUrl(), keyword + " " + dm.getName(),size);
        }
    }

    /**
     * 通过url地址进行下载文件
     *
     * @param url      网页地址
     * @param fileName 文件名，不包含文件路径需要自己配置
     */
    public static void downloadByUrl(String url, String fileName,int size) {
        nowSize++;
        // TODO: 2023/3/8 显示下载信息
        //server.execute(new Runnable() {
        //    @Override
        //    public void run() {
        //        double prs = nowSize / size ;
        //        String s = prs * 100 + "%";
        //        System.out.println(s);
        //        DMController.showPros(prs);
        //    }
        //});
        BufferedInputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL path = new URL(url);
            inputStream = new BufferedInputStream(path.openStream());
            fileOutputStream = new FileOutputStream(fileLocation + "\\" + fileName + ".mp3");
            System.out.println("下载文件名为："+fileName);
            byte[] bytes = new byte[1024];//1m
            int len = 0;//为什么需要记录长度，便于在写入的时候确定长度
            while ((len = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, len);//将读取的文件进行写出
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过url获取页面
     *
     * @return 页面
     */
    public static String getHtml(String url, String key) {
        BufferedReader bufferedReader = null;
        URLConnection urlConnection = null;
        StringBuilder info = new StringBuilder();
        try {
            urlConnection = new URL(url).openConnection();
            urlConnection.setRequestProperty("connection", "KEEP-Alive");
            urlConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            urlConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            urlConnection.setRequestProperty("Host", "kuwo.cn");
            urlConnection.setRequestProperty("Referer", "https://kuwo.cn/search/list?key=" + key);
            urlConnection.setRequestProperty("csrf", kwToken);
            urlConnection.setRequestProperty("Cookie", "kw_token=" + kwToken);
            urlConnection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
            urlConnection.connect();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                info.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return info.toString();
    }

    public static String getDP(String id) {
        return "https://antiserver.kuwo.cn/anti.s?type=convert_url3&rid=" + id + "&format=mp3";
    }

    public static List<DM> getList(String a) {
        JSONObject jsonObject = JSONObject.parseObject(a);
        JSONObject data = JSONObject.parseObject(jsonObject.get("data").toString());
        Object list = JSONObject.parse(data.get("list").toString());
        JSONArray jsonArray = JSONArray.parseArray(list.toString());
        List<DM> list1 = new ArrayList<>();
        for (Object o : jsonArray) {
            JSONObject object = JSONObject.parseObject(o.toString());
            Object artist = object.get("artist");
            Object rid = object.get("rid");
            Object album = object.get("album");
            list1.add(new DM(artist.toString() + " " + album.toString(),
                    getDP(rid.toString())
            ));
        }
        return list1;
    }

    public static List<DM> getDownList(List<DM> list, String key) {
        for (DM dm : list) {
            dm.setUrl(JSONObject.parseObject(getHtml(dm.getUrl(), key)).get("url").toString());
        }
        return list;
    }

    /**
     * 将中文进行编码
     *
     * @param ch 中文
     */
    public static String encodeCH(String ch) {
        String s = ch;
        if (Pattern.matches("^[\u4e00-\u9fa5]{0,}$", ch)) {
            //    是中文，进行编码
            s = chToUrl(ch);
        }
        return s;
    }

    public static String chToUrl(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
                } catch (Exception ex) {
                    System.out.println(ex);
                    b = new byte[0];
                }
                for (int value : b) {
                    int k = value;
                    if (k < 0)
                        k += 256;
                    sb.append("%").append(Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }


    public static String getUrl(String key) {
        return "https://kuwo.cn/api/www/search/searchMusicBykeyWord?key=" + key + "&pn=1&rn=" + showSize + "&httpsStatus=1";
    }

    public static void shutdown() {
        server.shutdown();
    }

    /**
     * 在线播放音乐
     */

    public static void playOnline(int startTime,String path){
        URL url = null;
        BufferedInputStream stream=null;
        try {
            url = new URL(path);
            nowMusic = url;
            URLConnection con = url.openConnection();
            stream = new BufferedInputStream(con.getInputStream());
            player=new AdvancedPlayer(stream);
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackStarted(PlaybackEvent evt) {
                    super.playbackStarted(evt);
                }
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    if (evt==null){
                        stopTime=0;
                        return;
                    }
                    stopTime = evt.getFrame()<0||evt.getFrame()>con.getContentLength()?0:evt.getFrame();
                }
            });
            if (startTime==0)player.play();
            else player.play(startTime,Integer.MAX_VALUE);
        } catch (IOException | JavaLayerException e) {
            e.printStackTrace();
        }finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (player!=null){
                player.close();
            }
        }
    }
}
