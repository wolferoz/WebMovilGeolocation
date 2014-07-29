package com.neurus.hoytevi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private String alias = "";
	private String password = "";
	private String nombre="";
	private String apellido="";
	private String rol="";
	
	private EditText txtEditAlias;
	private EditText txtEditPass;
	private Button btnIniciarSesion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		String[] archivos = fileList();

        if (existe(archivos,"notas.txt")){
            try {
                InputStreamReader archivo = new InputStreamReader(
                        openFileInput("notas.txt"));
                
                BufferedReader br = new BufferedReader(archivo);
                
                String linea = br.readLine();
                //String todo = "";
                //while (linea != null) {
                    alias = linea;
                    linea = br.readLine();
                    password = linea;
                    linea = br.readLine();
                    nombre = linea;
                    linea = br.readLine();
                    apellido = linea;
                    linea = br.readLine();
                    rol = linea;
                //}
                    
                br.close();
                archivo.close();
                
                CargarPantallaPrincipal();
                
            } catch (IOException e) {
            	Toast.makeText(this, "error leendo", Toast.LENGTH_SHORT).show();
            	iniciar();
            }
        }else{
        	Toast.makeText(this, "Ingrese Datos", Toast.LENGTH_SHORT).show();
	        iniciar();
	        
			btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					verificarServer(""+txtEditAlias.getText(),""+txtEditPass.getText());
				}
			});
		}
		
	}
	//metodo que inicializa e instancia los objetos de la vista
	private void iniciar(){
		txtEditAlias = (EditText) findViewById(R.id.txtLoginAlias);
		txtEditPass = (EditText) findViewById(R.id.txtLoginPasword);
		btnIniciarSesion = (Button) findViewById(R.id.btnLoginInitSesion);
		
        txtEditAlias.setText(alias);
        txtEditPass.setText(password);
	}
	//REcibe datos con los que se coenctara al servidor haciendo un llamado a la clase Conectar
	private void verificarServer(String unAlias,String unPassword){
		Conectar c = new Conectar(LoginActivity.this);
		c.execute("http://192.168.1.2/conectar/login.php?alias="+unAlias+"&password="+unPassword);
	}
	private void CargarPantallaPrincipal(){
	    
		Intent intent = new Intent(LoginActivity.this,MainActivity.class);
		
	        intent.putExtra("alias", alias);
	        intent.putExtra("password", password);
	        intent.putExtra("nombre", nombre);
	        intent.putExtra("apellido",apellido);
	        intent.putExtra("rol",rol);
	        
	        startActivityForResult(intent,36);
	}
	//Metodo que comprueba la existencia en memoria de un fichero de texto plano
	//Recibe la lista de archivos y el nombre del archivo que se busca
	private boolean existe(String[] archivos, String archbusca) {
        for (int f = 0; f < archivos.length; f++)
            if (archbusca.equals(archivos[f]))
                return true;
        return false;
    }
	//Metodo que crea un fichero de texto plano y almacena en el datos importantes
	private void grabar() {

        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(
                    "notas.txt", Activity.MODE_PRIVATE));
            
            archivo.write(alias+"\n");
            archivo.write(password+"\n");
            archivo.write(nombre+"\n");
            archivo.write(apellido+"\n");
            archivo.write(rol);
            archivo.flush();
            archivo.close();
            Toast.makeText(this, "¡Guardando Configuración!",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
        	Toast.makeText(this, "Error al guardar configuración",Toast.LENGTH_SHORT).show();
        }
    }
	
	
	//Clase asíncrona para realizar la coneccion con el servidor
	private class Conectar extends AsyncTask<String, Void, String>{
		private Activity activity;
		  
		public Conectar(Activity activity){
		    this.activity = activity;
		}
		
		@Override
		protected String doInBackground(String... urls) {
			String mURL = urls[0];
			String response="";
	        mURL=mURL.replace(" ", "%20");
	        Log.i("LocAndroid Response HTTP Threas","Ejecutando get 0: "+mURL);
	        HttpClient httpclient = new DefaultHttpClient();
	        Log.i("LocAndroid Response HTTP Thread","Ejecutando get 1");
	        HttpGet httppost = new HttpGet(mURL);
	        Log.i("LocAndroid Response HTTP Thread","Ejecutando get 2");
	        try {
				Log.i("LocAndroid Response HTTP","Ejecutando get");
		        // Execute HTTP Post Request
		        ResponseHandler<String> responseHandler=new BasicResponseHandler();
		        response = httpclient.execute(httppost,responseHandler);
		        Log.i("LocAndroid Response HTTP",response);
	   		}catch (ClientProtocolException e) {
	   			Log.i("LocAndroid Response HTTP ERROR 1",e.getMessage());
	           // TODO Auto-generated catch block
	   		} catch (IOException e) {
	           Log.i("LocAndroid Response HTTP ERROR 2",e.getMessage());
	           // TODO Auto-generated catch block
	   		}
	   		return response;
		}
		
		protected void onPostExecute(String feed) {
			  Toast.makeText(activity, feed,Toast.LENGTH_SHORT).show();
			  JSONArray ja=null;String data="hello";
				try {
					data=feed;
					if(data.length()>1)
						ja=new JSONArray(data);
					
					try{
						nombre=ja.getString(0);
						apellido=ja.getString(1);
						rol=ja.getString(2);
						if(nombre!=null  & apellido != null & rol!= null){
							alias = ""+txtEditAlias.getText();
							password = ""+txtEditPass.getText();
							grabar();
						}
						CargarPantallaPrincipal();
						Toast.makeText(activity, "Conectado",Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Toast.makeText(activity, "Error al leer los Datos",Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Error, verifique su conexion a internet", 1000).show();
				}
				
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode==36 && resultCode==RESULT_OK) {
			Bundle extras = data.getExtras();
			if(extras.getInt("resultado")==0){
				try {
					if (!deleteFile("notas.txt")){
						Toast.makeText(this, "El fichero " + "notas.txt"+ " no puede ser borrado!",Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					Toast.makeText(this, "ERROR",Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(this,"Vuelve pronto" ,Toast.LENGTH_SHORT).show();
			}
			finish();
		}
	}
}