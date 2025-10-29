package com.example.kronosprojeto.utils;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

public class SendSMS {

    public static void enviarSMS(Context context, String phoneNumber, int codigo) {
        try {
            if (!phoneNumber.startsWith("+")) {
                phoneNumber = "+55" + phoneNumber.replaceAll("[^\\d]", "");
            }

            String mensagem = "Código de verificação: " + codigo;
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, mensagem, null, null);

            context.getSharedPreferences("app", Context.MODE_PRIVATE)
                    .edit()
                    .putInt("verification_code", codigo)
                    .apply();

            Log.d("SendSMS", "SMS enviado para: " + phoneNumber);

        } catch (Exception e) {
            Log.e("SendSMS", "Erro ao enviar SMS", e);
            ToastHelper.showFeedbackToast(context,
                    "error",
                    "Falha ao enviar SMS",
                    e.getMessage());
        }
    }
}
