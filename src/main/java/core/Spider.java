package core;

import exception.SpiderSettingFileException;
import mq.MQReceiver;
import mq.MQSender;
import mq.MessageQueue;
import setting.MqObject;
import setting.SettingObject;
import setting.SettingReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by lrkin on 2016/11/16.
 * <p>
 * 爬虫组装工厂(写到这里可以回过去复习一下工厂模式的demo)
 */
public class Spider {
    private Downloader downloader;
    private Processor processor;
    private Saver saver;
    private FreeStaff freeStaff;
    private String settingFilePath;
    private SettingObject settingObject;

    private Map<String, MqObject> mqMap = new HashMap<String, MqObject>();
    private Map<String, MessageQueue> sendToMap = new HashMap<String, MessageQueue>();
    private Map<String, MQReceiver> recvFromMap = new HashMap<String, MQReceiver>();

    public static Spider create() {
        return new Spider();
    }

    public Spider setDownloader(Downloader downloader) {
        this.downloader = downloader;
        return this;
    }

    public Spider setProcessor(Processor processor) {
        this.processor = processor;
        return this;
    }

    public Spider setSaver(Saver saver) {
        this.saver = saver;
        return this;
    }

    public Spider setFreeStaff(FreeStaff freeStaff) {
        this.freeStaff = freeStaff;
        return this;
    }

    public Spider setSettingFilePath(String settingFilePath) {
        this.settingFilePath = settingFilePath;
        return this;
    }

    private void readSettingFile() throws IOException, SpiderSettingFileException, TimeoutException {
        if (this.settingFilePath == null) {
            throw new FileNotFoundException();
        } else {
            this.settingObject = SettingReader.read(settingFilePath);
            for (MqObject mqItem : this.settingObject.getMq()) {
                this.mqMap.put(mqItem.getName(), mqItem);
            }
            for (String item : this.settingObject.getSendto()) {
                MqObject object = this.mqMap.get(item);
                if (object == null) throw new SpiderSettingFileException();
                MQSender sender = new MQSender(object.getHost(), object.getPort(), object.getQueue());
                this.sendToMap.put(object.getName(), sender);
            }
            for (String item : this.settingObject.getRecvfrom()) {
                MqObject obj = this.mqMap.get(item);
                if (obj == null) throw new SpiderSettingFileException();
                MQReceiver receiver = new MQReceiver(obj.getHost(), obj.getPort(), obj.getQueue(), obj.getQos());
                this.recvFromMap.put(obj.getName(), receiver);
            }
        }
    }

    /**
     * 对于
     */
    class RecvThread implements Runnable {

        @Override
        public void run() {

        }
    }
}
