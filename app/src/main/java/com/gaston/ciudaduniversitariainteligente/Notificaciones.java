package com.gaston.ciudaduniversitariainteligente;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.gaston.ciudaduniversitariainteligente.MainActivity.currentUser;
import static com.gaston.ciudaduniversitariainteligente.MainActivity.lastID;

class NotificationData {
    public String message;
    public int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

public class Notificaciones extends Service {

    public Notificaciones() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("mensajes").child("C" + currentUser.getCarrera());

            usuariosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        int i = 0;
                        int lastId;
                        BaseDatos DB = new BaseDatos(getApplicationContext(), "DBCUI", null, 1);
                        SQLiteDatabase db1;
                        if (MainActivity.lastID == -1) {

                            db1 = DB.getReadableDatabase();
                            try {
                                Cursor c = db1.rawQuery("SELECT *  FROM Notify", null);
                                c.moveToFirst();

                                lastId = c.getInt(0);
                                db1.close();
                            } catch (Exception e) {
                                Log.d("DEBUG-NOTIFY", e.getMessage());
                                lastId = -1;
                            }
                        } else {
                            lastId = MainActivity.lastID;
                        }
                        Log.d("DEBUG-NOTIFY", "lastId: " + lastId);
                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                            Log.d("DEBUG-NOTIFY", "i: " + i);
                            NotificationData data = messageSnapshot.getValue(NotificationData.class);
                            if (MainActivity.logIn && data.getId() > lastId) {
                                Notificacion(i, data.getMessage());
                            }
                            i++;
                        }


                        db1 = DB.getWritableDatabase();
                        DB.saveLastNotiID(db1, i - 1);
                        db1.close();
                        MainActivity.lastID = i;
                    } catch (Exception e){
                        Log.d("DEBUG-NOTIFY", e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e){
            Log.d("TEST-LOGIN", e.getMessage());
        }
    }

    private void Notificacion(int ncode, String message){
        final NotificationManager mNotific =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        CharSequence name = "Ragav";
        String desc = "this is notific";
        int imp = NotificationManager.IMPORTANCE_HIGH;
        final String ChannelID = "my_channel_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(ChannelID, name,
                    imp);
            mChannel.setDescription(desc);

            mChannel.canShowBadge();
            mChannel.setShowBadge(true);
            mNotific.createNotificationChannel(mChannel);
        }

//        final int ncode = 101;

        android.app.Notification n = new NotificationCompat.Builder(this, ChannelID)
                .setContentTitle("Nuevo mensaje")
                .setContentText("Nuevo mensaje de tu carrera")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setBadgeIconType(R.mipmap.ic_launcher)
                .setSubText("CUI")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(false)
                .build();

        mNotific.notify(ncode, n);
    }
}
