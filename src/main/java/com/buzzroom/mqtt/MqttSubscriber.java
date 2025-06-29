package com.buzzroom.mqtt;

import com.buzzroom.service.GameService;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLSocketFactory;

@Component
public class MqttSubscriber {

    private final GameService gameService;

    public MqttSubscriber(GameService gameService) {
        this.gameService = gameService; // 🔗 injection du service
    }

    @PostConstruct
    public void start() {
        try {
            System.out.println("📡 Initialisation MQTT...");
            MqttConnectOptions options = new MqttConnectOptions();
            options.setSocketFactory(SSLSocketFactory.getDefault());
            options.setUserName("nolan13");
            options.setPassword("Test13100".toCharArray());

            MqttClient client = new MqttClient(
                    "ssl://0d773a1094b84a4a982ea09b1ded8ae9.s1.eu.hivemq.cloud:8883",
                    "buzzroom-test"
            );
            client.connect(options);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("❌ Connexion MQTT perdue !");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload());
                    System.out.println("📩 MQTT reçu [" + topic + "] : " + payload);

                    // ✨ Exemple d'appel au service
                    try {
                        int playerId = Integer.parseInt(payload.trim());
                        gameService.handleBuzz(playerId);
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Payload non numérique : " + payload);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {}
            });

            client.subscribe("buzzroom/+");
            System.out.println("✅ Abonné à buzzroom/+");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
