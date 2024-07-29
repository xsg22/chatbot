package ai.xsg.chatbot.service;

import com.yang.ai.ByteDanceAudioSpeechModel;
import com.yang.ai.ByteDanceChatModel;
import com.yang.ai.ByteDanceChatOptions;
import com.yang.ai.api.ByteDanceAudioApi;
import com.yang.ai.api.ByteDanceChatApi;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;

@Slf4j
public class ChatService {

    private static String doubaoKey = "";

    private static String SPEECH_API_KEY = "";
    private static String SPEECH_APP_KEY = "";
    private static String SPEECH_VOICE_NAME = "BV700_streaming";

    public byte[] send(byte[] audio) {
        // 语音识别
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audio);
        String text = AsrClientSender.send(byteArrayInputStream);
        if (StringUtils.isBlank(text)) {
            log.error("asr failed, text is empty");
            return null;
        }

        // LLM对话
        ByteDanceChatApi byteDanceChatApi = new ByteDanceChatApi(doubaoKey);
        ByteDanceChatOptions byteDanceChatOptions = ByteDanceChatOptions.builder().withModel("ep-20240527092207-c88kl").withTemperature(0.7f).build();
        ByteDanceChatModel chatModel = new ByteDanceChatModel(byteDanceChatApi, byteDanceChatOptions);
        String llmResponse = chatModel.call(text);

        // 文本转语音
        ByteDanceAudioApi byteDanceAudioApi = new ByteDanceAudioApi(SPEECH_API_KEY);
        ByteDanceAudioSpeechModel audioSpeechModel = new ByteDanceAudioSpeechModel(byteDanceAudioApi, SPEECH_APP_KEY, SPEECH_VOICE_NAME);
        byte[] speech = audioSpeechModel.call(llmResponse);
        // 使用 ByteArrayInputStream 读取二进制数据
        ByteArrayInputStream audioStream = new ByteArrayInputStream(speech);

        try {
            // 创建 Player
            Player player = new Player(audioStream);

            // 播放音频
            player.play();
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
        return speech;
    }

//    public void stream(Flux<byte[]> audio) {
//        // 语音识别
//
//        // LLM对话
//
//        // 文本转语音
//    }

    public void asr(byte[] audio) {

    }

    public void llmChat(String text) {

    }

    public void tts(String text) {

    }
}
