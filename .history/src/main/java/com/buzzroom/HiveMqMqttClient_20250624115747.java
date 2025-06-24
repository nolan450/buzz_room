

import org.eclipse.paho.client.mqttv3.*;

import javax.net.ssl.SSLSocketFactory;



public class HiveMqMqttClient {

    private static final String BROKER = "ssl://daf9546f53ff4681ac655cbc511e51f6.s1.eu.hivemq.cloud:8883";
    private static final String CLIENT_ID = "buzzroom-client";
    private static final String USERNAME = "buzzroom-user";
    private static final String PASSWORD = "Buzzroom-password13";

    public static void main(String[] args) {
        try {
            // Configuration des options de connexion sécurisée
            MqttConnectOptions options = new MqttConnectOptions();
            options.setSocketFactory(SSLSocketFactory.getDefault()); // Requis pour TLS/SSL
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());

            // Connexion au broker HiveMQ Cloud
            MqttClient client = new MqttClient(BROKER, CLIENT_ID);
            client.connect(options);
            System.out.println("✅ Connecté à HiveMQ Cloud");

            // Souscription au topic
            client.subscribe("buzzroom/buzzer/+");

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("❌ Connexion perdue !");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    System.out.println("📩 Reçu [" + topic + "] : " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {}
            });

            // Publication d’un message de test
            client.publish("buzzroom/buzzer/1", new MqttMessage("buzz".getBytes()));
            System.out.println("📤 Message publié : buzzroom/buzzer/1 → 'buzz'");

            // Laisser le client actif un moment pour tester les réceptions
            Thread.sleep(10000);

            client.disconnect();
            System.out.println("⛔ Déconnecté du broker");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
