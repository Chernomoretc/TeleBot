package ru.chernomoretc.telegrambot.utils;

import lombok.SneakyThrows;
import okhttp3.*;
import okhttp3.MediaType;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;


@Component
public class Utils {
    String botToken = "5550601179:AAHsNFCpjk314YWhNv0XPEsiGoBTzaWkSrA";


    public void sendMessage(String chatId, String text) throws IOException {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage?text=%s&chat_id=%s", botToken, text, chatId);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
    }

    public void deleteMessage (String chatId,String messageId) throws IOException {
        String url = String.
                format("https://api.telegram.org/bot%s/deleteMessage?chat_id=%s&message_id=%s",
                        botToken, chatId, messageId);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
    }

    @SneakyThrows
    public void sendLocation(String longitude, String latitude, String chatId, String caption) {

        String urlPhotoLoc = "https://static-maps.yandex.ru/1.x/?ll=" + longitude + ","
                + latitude + "%26size=650,450%26z=16%26l=map%26pt=" + longitude + "," + latitude + ",pm2rdm";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(String.format("https://api.telegram.org/bot%s/sendPhoto?photo=%s&chat_id=%s&caption=%s", botToken, urlPhotoLoc, chatId, caption))
                .build();
        Response response = client.newCall(request).execute();
    }

//    public void uploadFile(String chatId, ByteArrayResource value) {
//        LinkedMultiValueMap<String,Object> map = new LinkedMultiValueMap<>();
//        map.add("document",value);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
//        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
//            restTemplate.exchange(
//                    MessageFormat.format("https://api.telegram.org/{0}/sendDocument?chat_id={1}", botToken, chatId),
//                    HttpMethod.POST,
//                    requestEntity,
//                    String.class);
        public  Boolean uploadFile(String chatId, File file) {

            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("document", file.getName(),
                                RequestBody.create(MediaType.parse("text/xls"), file))
//                        .addFormDataPart("document", "somevalue-")
                        .build();

                Request request = new Request.Builder()
                        .url(String.format("https://api.telegram.org/bot%s/SendDocument?&chat_id=%s", botToken,  chatId))
                        .post(requestBody)
                        .build();
                System.out.println();
                Response response = client.newCall(request).execute();
                System.out.println(response);
//                client.newCall(request).enqueue(new Callback() {
//
//                    @Override
//                    public void onFailure(final Call call, final IOException e) {
//                        // Handle the error
//                    }
//
//                    @Override
//                    public void onResponse(final Call call, final Response response) throws IOException {
//                        if (!response.isSuccessful()) {
//                            // Handle the error
//                        }
//                        // Upload successful
//                    }
//                });

                return true;
            } catch (Exception ex) {
                // Handle the error
            }
            return false;
        }



}
