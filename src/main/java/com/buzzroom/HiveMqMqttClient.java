package com.buzzroom;

import org.eclipse.paho.client.mqttv3.*;

import javax.net.ssl.SSLSocketFactory;

public class HiveMqMqttClient {

    private static final String BROKER = "ssl://0d773a1094b84a4a982ea09b1ded8ae9.s1.eu.hivemq.cloud:8883";
    private static final String CLIENT_ID = "buzzroom-client";
    private static final String USERNAME = "nolan13";
    private static final String PASSWORD = "Test13100";

    private MqttClient client;

    public void connect() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setSocketFactory(SSLSocketFactory.getDefault());
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        client = new MqttClient(BROKER, CLIENT_ID);
        client.connect(options);
        System.out.println("✅ Connecté à HiveMQ Cloud");
    }

    public void publish(String topic, String message) throws MqttException {
        if (client == null || !client.isConnected()) {
            connect();
        }
        client.publish(topic, new MqttMessage(message.getBytes()));
        System.out.println("📤 Message publié : " + topic + " → '" + message + "'");
    }

    public void disconnect() throws MqttException {
        if (client != null && client.isConnected()) {
            client.disconnect();
            System.out.println("⛔ Déconnecté du broker");
        }
    }
}
