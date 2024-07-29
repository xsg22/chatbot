package ai.xsg.chatbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;

public class AsrClientSender {
    private static final Logger logger = LoggerFactory.getLogger(AsrClientSender.class);

    public static void main(String[] args) {
        String audio_path = "static/response.wav";  // 本地音频文件路径；
        InputStream fp = AsrClientSender.class.getClassLoader().getResourceAsStream(audio_path);

        String text = send(fp);
        System.out.println(text);
    }
    public static String send(InputStream fp) {
        String appid = "";  // 项目的 appid
        String token = "";  // 项目的 token
        String cluster = "volcengine_input_common";  // 请求的集群
        String audio_format = "mp3";  // wav 或者 mp3, 根据音频类型设置

        AsrClient asrClient = null;
        try {
            asrClient = new AsrClient();
            asrClient.setAppid(appid);
            asrClient.setToken(token);
            asrClient.setCluster(cluster);
            asrClient.setFormat(audio_format);
            asrClient.setShow_utterances(true);
            asrClient.init();

            asrClient.asr_sync_connect();
            byte[] b = new byte[16000];
            int len = 0;
            int count = 0;
            AsrResponse asr_response = new AsrResponse();
            while ((len = fp.read(b)) > 0) {
                count += 1;
                logger.info("send data pack length: {}, count {}, is_last {}", len, count, fp.available() == 0);
                asr_response = asrClient.asr_send(Arrays.copyOfRange(b, 0, len), fp.available() == 0);
            }

            // get asr text
//            AsrResponse response = asrClient.getAsrResponse();
            for (AsrResponse.Result result : asr_response.getResult()) {
                logger.info(result.getText());
                return result.getText();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (asrClient != null) {
                asrClient.asr_close();
            }
        }
        return null;
    }
}
