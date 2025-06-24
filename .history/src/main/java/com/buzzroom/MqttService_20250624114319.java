package com.buzzroom.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MqttService {

    private final String broker = "tcp://localhost:1883"; // HiveMQ CE local
    // Pour HiveMQ Cloud, utilise : "ssl://xxx.hivemq.cloud:8883"

    private final String clientId = "buzzroom-client";
    private MqttClient client;

    @PostConstruct
    public void init() {
        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();

            // Pour HiveMQ Cloud : activer SSL + login
            // options.setUserName("ton_username");
            // options.setPassword("ton_password".toCharArray());

            client.connect(options);
            System.out.println("MQTT connecté au broker HiveMQ");

            // Abonnement aux buzz
            client.subscribe("buzzroom/buzzer/+");

            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {
                    System.out.println("MQTT déconnecté !");
                }

                public void messageArrived(String topic, MqttMessage message) {
                    System.out.println("📩 MQTT Reçu : " + topic + " -> " + new String(message.getPayload()));

                    // Exemple : buzzer 1 → extraire l’ID
                    String[] parts = topic.split("/");
                    if (parts.length == 3 && parts[1].equals("buzzer")) {
                        int buzzerId = Integer.parseInt(parts[2]);
                        System.out.println("Buzzer ID : " + buzzerId);
                        // Ici tu peux enregistrer un score, etc.
                    }
                }

                public void deliveryComplete(IMqttDeliveryToken token) {}
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        try {
            client.publish(topic, new MqttMessage(message.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
