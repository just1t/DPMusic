package com.just1t.ui;

import com.alibaba.fastjson.JSONObject;
import com.just1t.dm.entity.DM;
import com.just1t.dm.util.WebUtil;
import com.just1t.ui.entity.TableSH;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.util.List;
import java.util.Objects;

import static com.just1t.dm.util.WebUtil.*;

public class DMController {

    @FXML
    public Button button;
    @FXML
    public TableView table;
    @FXML
    public TextField input;
    @FXML
    public TableColumn name;
    @FXML
    public Text prsText;//显示下载进程

    public static List<TableSH> dmList = null;
    public String keyword = "";
    public String encode = "";
    public String downloadLocation = "";
    @FXML
    public Button playButton;
    public Button back;//进行返回


    @FXML
    private void download() {
        server.submit(new Runnable() {
            @Override
            public void run() {
                //进行下载
                List<TableSH> tableSHES = dmList.stream().filter(TableSH::isFlog).toList();
                List<DM> dms = tableSHES.stream().map(item -> {
                    return new DM(item.getName(), item.getUrl().toString());
                }).toList();
                System.out.println(dms);
                WebUtil.downloadList(dms, encode, keyword, dms.size());
                System.out.println("下载位置为：" + downloadLocation);
            }
        });
    }

    @FXML
    private void search() {
        //进行搜索
        keyword = input.getText();
        if (keyword == null || "".equals(keyword)) {
            error("请先输入歌名哦");
            return;
        }
        downloadLocation = WebUtil.getMUByName(keyword);
        encode = WebUtil.encodeCH(keyword);
        setTable(WebUtil.getDmList().stream().map(dm -> {
            return new TableSH(new SimpleBooleanProperty(false), new SimpleStringProperty(keyword + " " + dm.getName()), dm.getUrl());
        }).toList());
    }


    /**
     * 添加选项
     *
     * @param list
     */
    private void setTable(List<TableSH> list) {
        dmList = list;

        //将所有的行进行清除
        TableColumn aa = new TableColumn("SELECT");
        aa.setMinWidth(50);
        Callback<TableColumn<TableSH, SimpleBooleanProperty>, TableCell<TableSH, SimpleBooleanProperty>> callback = new Callback<>() {
            @Override
            public TableCell<TableSH, SimpleBooleanProperty> call(TableColumn<TableSH, SimpleBooleanProperty> tableSHSimpleBooleanPropertyTableColumn) {
                CheckBoxTableCell<TableSH, SimpleBooleanProperty> checkBoxTableCell = new CheckBoxTableCell<>();

                checkBoxTableCell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        //修改这一行的状态（是否被选中）
                        int index = checkBoxTableCell.getIndex();
                        System.out.println(index);
                        TableSH tableSH = list.get(index);
                        tableSH.setFlog(!tableSH.isFlog());
                        checkBoxTableCell.updateIndex(index);
                    }
                });
                return checkBoxTableCell;
            }
        };

        aa.setPrefWidth(113);
        aa.setCellValueFactory(new PropertyValueFactory<TableSH, SimpleBooleanProperty>("flog"));
        aa.setCellFactory(callback);


        TableColumn dd = new TableColumn("NAME");
        dd.setCellValueFactory(new PropertyValueFactory<TableSH, SimpleStringProperty>("name"));
        dd.setPrefWidth(264);

        table.setRowFactory(new Callback<TableView, TableRow>() {
            @Override
            public TableRow call(TableView tableView) {
                TableRow<Object> objectTableRow = new TableRow<>();
                objectTableRow.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getClickCount() == 2) {
                            //进行在线播放
                            if (player != null) player.close();
                            server.execute(new Runnable() {
                                @Override
                                public void run() {
                                    //将播放器进行关闭在进行播放
                                    if (player != null) player.close();
                                    WebUtil.playOnline(0, getMUUrl(list.get(objectTableRow.getIndex()), keyword));
                                }
                            });
                        }
                    }
                });
                return objectTableRow;
            }
        });

        table.getColumns().clear();
        table.getColumns().addAll(aa, dd);

        table.getItems().removeAll();

        table.setItems(FXCollections.observableList(list));

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.refresh();
    }

    /**
     * 显示进程
     */
    public static void showPros(double prs) {
        //一共四十个
        StringBuilder s = new StringBuilder();
        System.out.println(prs);
        for (int i = 0; i < (prs * 40); i++) {
            s.append("=");
        }
        s.append(">");
        String s1 = "[" + s + "]";
        new DMController().showInfo(s1);
    }

    public void showInfo(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("歌曲正在下载咯~~😀");
        alert.setContentText(s);
        alert.setHeaderText("好东西记得和大家一起分享哟~~~😀😀");
        alert.showAndWait();
    }

    /**
     * 显示提示信息
     */
    public void showWring(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("关于使用该应用的须知：");
        alert.setHeaderText("""
                当前应用可能存在一些BUG，解决不了就重启😀😀~~
                如果提示当前音乐无法收听可以多尝试几次😔😔~~
                提示：1、在双击播放的时候，不会显示暂停功能
                         2、尽量少使用暂停功能，因为该库包中获取歌曲帧率的是否不准确😔😔😔
                         3、如果下载的音乐长度为11秒钟，表示该首歌只允许在手机上进行获取😔😔""");
        alert.showAndWait();
    }

    public void error(String info) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("提示小宝贝：");
        alert.setHeaderText("好东西记得和大家一起分享哟~~~😀😀");
        alert.setContentText(info + "~~~");
        alert.showAndWait();
    }

    @FXML
    public void playOnline(MouseEvent mouseEvent) {
        //将按钮的文字进行修改
        if (dmList == null || dmList.isEmpty() && playButton.getText().equals("在线播放")) {
            error("请选中需要播放的音乐在进行在线播放哦");
            return;
        }
        if (playButton.getText().equals("在线播放")) {
            button.setText("⏮");
            back.setVisible(true);
            playButton.setText("⏭");
        } else if (playButton.getText().equals("退出播放")) {
            playButton.setText("在线播放");
            back.setText("⏸");
            back.setVisible(false);
            return;
        }
        server.execute(new Runnable() {
            @Override
            public void run() {
                //将播放器进行关闭在进行播放
                if (player != null) player.close();
                dmList.forEach(item -> {
                    if (item.isFlog()) {
                        WebUtil.playOnline(0, getMUUrl(item, keyword));
                    }
                    ;
                });
            }
        });
    }

    @FXML
    public void stopPlay() {
        if (back.getText().equals("⏸")) {
            player.stop();
            back.setText("▶");
            playButton.setText("退出播放");
            button.setText("点击进行下载");
            //进行暂停操作
            //通过暂停线程的方式进行暂停
            // TODO: 2023/3/11 进行暂停播放
        } else if (back.getText().equals("▶")) {
            back.setText("⏸");
            playButton.setText("⏭");
            button.setText("⏮");
            //进行继续播放操作
            System.out.println("当前的信息为：" + stopTime);
            server.execute(new Runnable() {
                @Override
                public void run() {
                    WebUtil.playOnline(stopTime, nowMusic.toString());
                }
            });
        }
    }

    /**
     * 获取在线播放的地址mp3格式
     *
     * @param sh
     * @param keyword
     * @return
     */
    public static String getMUUrl(TableSH sh, String keyword) {
        return JSONObject.parseObject(getHtml(sh.getUrl(), keyword)).get("url").toString();
    }
}