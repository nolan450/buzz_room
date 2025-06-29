package com.buzzroom;

import org.eclipse.paho.client.mqttv3.MqttException;

public class App {
    public static void main(String[] args) throws MqttException {
        CLI cli = new CLI();
        cli.start();
    }
}
