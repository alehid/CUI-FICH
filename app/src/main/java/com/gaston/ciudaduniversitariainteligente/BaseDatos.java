package com.gaston.ciudaduniversitariainteligente;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lautaro on 30/11/2016.
 */
public class BaseDatos extends SQLiteOpenHelper {


    private LogginCUI log = new LogginCUI();

    public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        cargaDB(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Busquedas");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Punto");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Conexiones");

            //Se crea la nueva versión de las tablas
            cargaDB(sqLiteDatabase);

        }catch (Exception e){
            log.registrar(this,"onUpgrade",e);
        }
    }

    public void saveLastNotiID(SQLiteDatabase sqLiteDatabase, int id){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Notify");
        sqLiteDatabase.execSQL("CREATE TABLE Notify (id INTEGER)");
        sqLiteDatabase.execSQL("INSERT INTO Notify (id) VALUES(" + id + ")");
    }

    public void cargaDB(SQLiteDatabase sqLiteDatabase) {
        try {
            //Creo las tablas
            sqLiteDatabase.execSQL("CREATE TABLE Busquedas (nombre TEXT, edificio TEXT)");
            sqLiteDatabase.execSQL("CREATE TABLE Punto (id INTEGER, latitud TEXT, longitud TEXT, edificio TEXT, piso INTEGER, nombre TEXT, imagen INTEGER)");
            sqLiteDatabase.execSQL("CREATE TABLE Conexiones (idDesde INTEGER, idHasta INTEGER)");

            //Cargo los puntos
            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 0 + " ,'-31.6409242','-60.6718911', 'Ciudad Universitaria'," + 0 + ", 'Porton Principal'," + R.drawable.porton + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 1 + " ,'-31.6399652','-60.6718723', 'FICH'," + 0 + ", 'Entrada FICH'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 2 + " ,'-31.639962','-60.672141', 'FICH'," + 0 + ", 'Escalera'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 3 + " ,'-31.6399632','-60.6722636', 'FICH'," + 0 + ", 'Aula 8'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 4 + " ,'-31.6399677','-60.6723350', 'FICH'," + 0 + ", 'Baños'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 5 + " ,'-31.6399686','-60.6724265', 'FICH'," + 0 + ", 'Escaleras'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 6 + " ,'-31.6397985','-60.6724242', 'FICH'," + 0 + ", 'Cantina'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 7 + " ,'-31.6398022','-60.6723350', 'FICH'," + 0 + ", 'Aula Magna - 4'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 8 + " ,'-31.6398042','-60.6725415', 'FICH'," + 0 + ", 'Aula 1 - 2 - 3'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 9 + " ,'-31.6396181','-60.6725542', 'FICH'," + 0 + ", ' '," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 10 + " ,'-31.6395750','-60.6717439', 'FICH'," + 0 + ", ' '," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 11 + " ,'-31.6399686','-60.6708785', 'FICH'," + 0 + ", 'Aula 7'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 12 + " ,' -31.639559','-60.6709064', 'FCM'," + 0 + ", 'Entrada FCM'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 13 + " ,'-31.6395904','-60.6710308', 'FCM'," + 0 + ", 'Cantina'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 14 + " ,'-31.6398005','-60.6721767', 'FICH'," + 0 + ", 'Aula 5'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 15 + " ,'-31.639964','-60.6731235', 'FCBC'," + 0 + ", 'Entrada FCBC'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 16 + " ,'-31.6402149','-60.673133', ' '," + 0 + ", ' '," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 17 + " ,'-31.6402169','-60.6732875', 'FADU - FHUC'," + 0 + ", 'Entrada FADU - FHUC'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 18 + " ,'-31.6396258','-60.6731108', ' '," + 0 + ", ' '," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 19 + " ,'-31.6403682','-60.6732892', ' '," + 0 + ", ' '," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 20 + " ,'-31.6403796','-60.6738796', 'ISM'," + 0 + ", 'Entrada ISM'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 21 + " ,'-31.6399517','-60.6739577', ' '," + 0 + ", ' '," + -1 + ")");

            //Me los saltie haciendo el grafo, los agrego pero no tienen conexiones
            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 22 + " ,'-31.6399517','-60.6739577', ' '," + 0 + ", ' '," + -1 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 23 + " ,'-31.6399517','-60.6739577', ' '," + 0 + ", ' '," + -1 + ")");
            //

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 24 + " ,'-31.6409847','-60.6731732', ' '," + 0 + ", ' '," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 25 + " ,'-31.6399589','-60.6723135', 'FICH'," + 1 + ", 'Escalera'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 26 + " ,'-31.6399600','-60.6721402', 'FICH'," + 1 + ", ' '," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 27 + " ,'-31.6397773','-60.6721412', 'FICH'," + 1 + ", 'Baños'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 28 + " ,'-31.6397771','-60.6722679', 'FICH'," + 1 + ", 'Aula Lab 1 - 2 - Electronica'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 29 + " ,'-31.6397779','-60.6725519', 'FICH'," + 1 + ", ' '," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 30 + " ,'-31.6398541','-60.6725542', 'FICH'," + 1 + ", 'Aula Lab 3 - 4'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 31 + " ,'-31.6399603','-60.6725506', 'FICH'," + 1 + ", 'Escalera'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 32 + " ,'-31.6399557','-60.6724161', 'FICH'," + 2 + ", 'Escalera'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 33 + " ,'-31.6399632','-60.6721938', 'FICH'," + 2 + ", 'Escalera'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 34 + " ,'-31.6399600','-60.6721150', 'FICH'," + 2 + ", 'Aula Dibujo'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 35 + " ,'-31.6399563','-60.6724081', 'FICH'," + 3 + ", 'Biblioteca'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 36 + " ,'-31.6399632','-60.6721938', 'FICH'," + 3 + ", 'Escaleras'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 37 + " ,'-31.6399614','-60.6720946', 'FICH'," + 3 + ", 'Aula 9'," + -1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
                    "(" + 38 + " ,'-31.6399355','-60.6720775', 'FICH'," + 2 + ", 'Baños'," + -1 + ")");

            //Agrego las conexiones
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 0 + "," + 1 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 1 + "," + 0 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 1 + "," + 2 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 1 + "," + 11 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 2 + "," + 1 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 2 + "," + 3 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 2 + "," + 25 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 3 + "," + 2 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 3 + "," + 4 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 4 + "," + 3 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 4 + "," + 5 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 5 + "," + 4 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 5 + "," + 15 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 5 + "," + 6 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 5 + "," + 31 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 6 + "," + 5 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 6 + "," + 7 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 6 + "," + 8 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 7 + "," + 6 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 7 + "," + 14 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 8 + "," + 6 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 8 + "," + 9 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 9 + "," + 8 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 9 + "," + 10 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 9 + "," + 18 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 10 + "," + 9 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 10 + "," + 13 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 11 + "," + 1 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 11 + "," + 12 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 12 + "," + 11 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 12 + "," + 13 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 13 + "," + 12 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 13 + "," + 10 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 14 + "," + 7 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 15 + "," + 5 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 15 + "," + 16 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 15 + "," + 21 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 15 + "," + 18 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 16 + "," + 15 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 16 + "," + 17 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 17 + "," + 16 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 17 + "," + 19 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 18 + "," + 9 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 18 + "," + 15 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 19 + "," + 17 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 19 + "," + 24 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 19 + "," + 20 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 20 + "," + 19 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 21 + "," + 15 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 24 + "," + 19 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 25 + "," + 2 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 25 + "," + 26 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 25 + "," + 31 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 25 + "," + 33 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 26 + "," + 25 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 26 + "," + 27 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 27 + "," + 26 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 27 + "," + 28 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 28 + "," + 27 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 28 + "," + 29 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 29 + "," + 28 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 29 + "," + 30 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 30 + "," + 29 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 30 + "," + 31 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 31 + "," + 5 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 31 + "," + 30 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 31 + "," + 25 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 31 + "," + 32 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 32 + "," + 31 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 32 + "," + 33 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 32 + "," + 35 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 33 + "," + 32 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 33 + "," + 34 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 33 + "," + 25 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 33 + "," + 36 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 34 + "," + 33 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 34 + "," + 38 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 35 + "," + 32 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 35 + "," + 36 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 36 + "," + 35 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 36 + "," + 37 + ")");
            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 36 + "," + 33 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 37 + "," + 36 + ")");

            sqLiteDatabase.execSQL("INSERT INTO Conexiones (idDesde, idHasta) VALUES (" + 38 + "," + 34 + ")");
        }catch (Exception e){

            log.registrar(this,"cargaDB",e);
            throw e;

        }

    }
}
