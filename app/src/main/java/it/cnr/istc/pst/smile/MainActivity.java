package it.cnr.istc.pst.smile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private OkHttpClient client;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private ImageView image_view;
    private Button speak_button;
    Translator translator = Translation.getClient(new TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH).setTargetLanguage(TranslateLanguage.ITALIAN).build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int i) {
                Log.d(TAG, "onError: " + i);
                if (i == SpeechRecognizer.ERROR_NO_MATCH) {
                    Toast.makeText(MainActivity.this, "No match", Toast.LENGTH_SHORT).show();
                }
                speak_button.setEnabled(true);
            }

            @Override
            public void onResults(Bundle bundle) {
                speak_button.setEnabled(true);
                String result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                Executors.newSingleThreadExecutor().execute(() -> {
                    JSONObject message_body = new JSONObject();
                    try {
                        message_body.put("sender", "Android");
                        message_body.put("message", result);
                    } catch (Exception e) {
                        Log.d(TAG, "onResults: " + e.getMessage());
                    }

                    try {
                        Response login_response = client.newCall(new okhttp3.Request.Builder().url("http://150.146.65.22:5005/webhooks/rest/webhook").post(RequestBody.create(message_body.toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"))).build()).execute();

                        if (login_response.isSuccessful()) {
                            String responseString = login_response.body().string();
                            Log.d(TAG, "onResponse: " + responseString);
                            JSONObject response_body = new JSONArray(responseString).getJSONObject(0);
                            if (response_body.has("text")) {
                                runOnUiThread(() -> {
                                    try {
                                        Toast.makeText(MainActivity.this, response_body.getString("text"), Toast.LENGTH_SHORT).show();
                                        textToSpeech.speak(response_body.getString("text"), TextToSpeech.QUEUE_FLUSH, null, "42");
                                        image_view.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.favicon));
                                    } catch (JSONException e) {
                                        Log.e(TAG, "onResponse: " + e.getMessage());
                                    }
                                });
                            } else if (response_body.has("custom")) {
                                runOnUiThread(() -> {
                                    try {
                                        JSONObject custom = response_body.getJSONObject("custom");
                                        if (custom.has("image")) {
                                            switch (custom.getString("image")) {
                                                case "ferita_chirurgica_con_infiammazione.jpeg":
                                                    image_view.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ferita_chirurgica_con_infiammazione));
                                                    break;
                                            }
                                        }
                                        if (custom.has("video")) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(custom.getString("video"))));
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, "onResponse: " + e.getMessage());
                                    }
                                });
                            }
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "onResponse: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                String result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                Log.d(TAG, "onError: " + i);
                if (i == SpeechRecognizer.ERROR_NO_MATCH) {
                    Toast.makeText(MainActivity.this, "No match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                switch (textToSpeech.setLanguage(Locale.getDefault())) {
                    case TextToSpeech.LANG_MISSING_DATA:
                        Log.e(TAG, "onCreate: Language missing data");
                        break;
                    case TextToSpeech.LANG_NOT_SUPPORTED:
                        Log.e(TAG, "onCreate: Language not supported");
                        break;
                    case TextToSpeech.LANG_AVAILABLE:
                        Log.d(TAG, "onCreate: Language available");
                        break;
                    case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                        Log.d(TAG, "onCreate: Language country available");
                        break;
                    case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                        Log.d(TAG, "onCreate: Language country var available");
                        break;
                    default:
                        Log.d(TAG, "onCreate: Language unknown");
                        break;
                }
            } else {
                Log.e(TAG, "onCreate: Initialization failed");
            }
        });
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.d(TAG, "onStart: " + utteranceId);
                runOnUiThread(() -> speak_button.setEnabled(false));
            }

            @Override
            public void onDone(String utteranceId) {
                Log.d(TAG, "onDone: " + utteranceId);
                runOnUiThread(() -> speak_button.setEnabled(true));
            }

            @Override
            public void onError(String utteranceId) {
                Log.e(TAG, "onError: " + utteranceId);
                runOnUiThread(() -> speak_button.setEnabled(true));
            }

            @Override
            public void onError(String utteranceId, int errorCode) {
                Log.e(TAG, "onError: " + utteranceId + " " + errorCode);
                runOnUiThread(() -> speak_button.setEnabled(true));
            }
        });

        image_view = findViewById(R.id.image_view);

        speak_button = findViewById(R.id.speak_button);
        speak_button.setOnClickListener(view -> {
            speak_button.setEnabled(false);
            speechRecognizer.startListening(new android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }

        client = getUnsafeOkHttpClient();

        DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(v -> {
            Log.d(TAG, "onCreate: Model downloaded");
            translator.translate("Hello world").addOnSuccessListener(s -> Log.d(TAG, "onCreate: " + s));
        }).addOnFailureListener(e -> Log.e(TAG, "onCreate: " + e.getMessage()));

        startActivity(new android.content.Intent(this, TopicActivity.class));
        startActivity(new android.content.Intent(this, InitActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Ora posso ascoltare", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
        textToSpeech.shutdown();
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]).hostnameVerifier((hostname, session) -> true).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}