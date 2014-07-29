package com.neurus.hoytevi;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private String alias = "";
	private String password = "";
	private String nombre="";
	private String apellido="";
	private String rol="";
	
	private TextView messageText;
	private Calendar c = Calendar.getInstance();
    private Button btnselectpic,btnViewCam,uploadButton;
    private EditText txtEditTitulo,txtEditCategoria,txtEditDescripcion;
    private ImageView imageview;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
       
    private String upLoadServerUri = null;
    private String urlBase="http://192.168.137.1/proyecto/public";
    private String imagepath=null;
    private String imagepathCam=null;
    
    private int idAccion=0; 
    
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		Bundle extras = getIntent().getExtras();
		alias = extras.getString("alias");
		password = extras.getString("password");
		nombre = extras.getString("nombre");
		apellido = extras.getString("apellido");
		rol = extras.getString("rol");
		
		iniciar();
	}
	private void iniciar(){
          //instanciamos los elementos de la vista
    		btnselectpic = (Button)findViewById(R.id.btnBuscar);
    		btnViewCam = (Button)findViewById(R.id.btnCam);
    		uploadButton = (Button)findViewById(R.id.btnSubir);
    		messageText  = (TextView)findViewById(R.id.txtNombreArchivo);
    		imageview = (ImageView)findViewById(R.id.imageView_pic);

    		messageText.setText(alias+" "+password+" "+nombre+" "+apellido+" "+rol);
    		
    		txtEditTitulo = (EditText) findViewById(R.id.txtMainEditTitulo);
    		txtEditCategoria= (EditText) findViewById(R.id.txtMainEditCategoria);
    		txtEditDescripcion = (EditText) findViewById(R.id.txtMainEditDescripcion);
    		
    		btnselectpic.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View arg0) {
    				Intent intent = new Intent();
    	            intent.setType("image/*");
    	            intent.setAction(Intent.ACTION_GET_CONTENT);
    	            startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
    			}
    		});
    		btnViewCam.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View arg0) {
    				
    				//intent para acceder a la camara
    				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    				/*
    				 *	SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
    				 *	String formattedDate1 = df1.format(c.getTime());
    				 *	SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
    				 *	String formattedDate2 = df2.format(c.getTime());
    				 *	SimpleDateFormat df3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
    				 *	String formattedDate3 = df3.format(c.getTime());
    				 * 
    				 * 
    				 * Creamos un fichero donde guardaremos la foto
    				 */
    				imagepathCam = Environment.getExternalStorageDirectory() + "/"+alias+c.getTimeInMillis()+".jpeg";
    				Uri output = Uri.fromFile(new File(imagepathCam));
    				intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
    				/*
    				 * Lanzamos el intent y recogemos el resultado en onActivityResult
    				 */
    				startActivityForResult(intent, 2); // 2 para la camara, 1 para la galeria
    			}
    		});
    		
    		uploadButton.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View arg0) {
    				
    				defineUpLoadServerUri(""+txtEditTitulo.getText(),""+txtEditCategoria.getText(),""+txtEditDescripcion.getText());
    				
    				if(idAccion == 1){//galeria
    					dialog = ProgressDialog.show(MainActivity.this, "", "Cargando archivo...", true);//Uploading file
    					 messageText.setText("Empezando la carga.....");//uploading started
    					 new Thread(new Runnable(){
    		                 public void run(){
    		                	 uploadFile(imagepath);
    		                 }
    		             }).start();
    				}else if(idAccion== 2){//camara
    					UploaderFoto nuevaTarea = new UploaderFoto(upLoadServerUri);
    					nuevaTarea.execute(imagepathCam);
    				}
    			}
    		});
    		
    		imageview.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(urlBase));  
    				startActivity(viewIntent);
    			}
    		});
    		
    		//upLoadServerUri = "http://192.168.137.1/proyecto/public/post/guardar/Titulo/galeria/Categoria/2/Descripcion/bb";
    		
	}
	private void defineUpLoadServerUri(String unTitulo,String unaCategoria,String unaDescripcion){
		//upLoadServerUri = "http://192.168.1.2/conectar/UploadToServer.php?titulo="+unTitulo+"&id_categoria="+unaCategoria+"&descripcion="+unaDescripcion;
		upLoadServerUri = urlBase+"/post/guardar/Titulo/"+unTitulo+"/Categoria/"+unaCategoria+"/Descripcion/"+unaDescripcion;
	}
	//Sube una imagen cargada desde la galeria
	public int uploadFile(String sourceFileUri) {
			String fileName = sourceFileUri;

	        HttpURLConnection conn = null;
	        DataOutputStream dos = null;  
	        String lineEnd = "\r\n";
	        String twoHyphens = "--";
	        String boundary = "*****";
	        int bytesRead, bytesAvailable, bufferSize;
	        byte[] buffer;
	        int maxBufferSize = 1 * 1024 * 1024; 
	        File sourceFile = new File(sourceFileUri); 
	        
	        if (!sourceFile.isFile()) {//si no está cargado
	      	  
	        	dialog.dismiss(); 
		           
		        Log.e("uploadFile", "Source File not exist :"+imagepath);
		           
		        runOnUiThread(new Runnable() {
		        	public void run() {
		        		messageText.setText("Source File not exist :"+ imagepath);
		            }
		        }); 
		           
		        return 0;
	        }else{//si esta cargado
	        	try { 
	        		// open a URL connection to the Servlet
	        		FileInputStream fileInputStream = new FileInputStream(sourceFile);
		            URL url = new URL(upLoadServerUri);
		               
		            // Open a HTTP  connection to  the URL
		            conn = (HttpURLConnection) url.openConnection(); 
		            conn.setDoInput(true); // Allow Inputs
		            conn.setDoOutput(true); // Allow Outputs
		            conn.setUseCaches(false); // Don't use a Cached Copy
		            conn.setRequestMethod("POST");
		            conn.setRequestProperty("Connection", "Keep-Alive");
		            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
		            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		            conn.setRequestProperty("File", fileName );//nuevo valor de nombre 

		            dos = new DataOutputStream(conn.getOutputStream());
		     
		            dos.writeBytes(twoHyphens + boundary + lineEnd); 
		            dos.writeBytes("Content-Disposition: form-data; name=\"File\";filename=\""
		            		                     +alias+c.getTimeInMillis()+".jpeg"+ "\"" + lineEnd);
		            dos.writeBytes(lineEnd);
		            
		            // create a buffer of  maximum size
		            bytesAvailable = fileInputStream.available(); 
		     
		            bufferSize = Math.min(bytesAvailable, maxBufferSize);
		            buffer = new byte[bufferSize];
		     
		            // read file and write it into form...
		            bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
		                 
		            while (bytesRead > 0) {
		            	   
		            	dos.write(buffer, 0, bufferSize);
		                bytesAvailable = fileInputStream.available();
		                bufferSize = Math.min(bytesAvailable, maxBufferSize);
		                bytesRead = fileInputStream.read(buffer, 0, bufferSize);   

		            }
		            
		            // send multipart form data necesssary after file data...
		            dos.writeBytes(lineEnd);
		            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		            
		            // Responses from the server (code and message)
		            serverResponseCode = conn.getResponseCode();
		            String serverResponseMessage = conn.getResponseMessage();
		                
		            Log.i("uploadFile", "HTTP Response is : " 
		            		   + serverResponseMessage + ": " + serverResponseCode);
		               
		            if(serverResponseCode == 200){

		            	runOnUiThread(new Runnable() {
		            		public void run() {
		            			String msg = "¡Archivo subido! (:";
		                        messageText.setText(msg);
		                        Toast.makeText(MainActivity.this, "Imagen subida con éxito .", Toast.LENGTH_SHORT).show();
		                    }
		                });                
		            }    
		            //close the streams //
		            fileInputStream.close();
		            dos.flush();
		            dos.close();
	        	} catch (MalformedURLException ex) {
	        		dialog.dismiss();  
		            ex.printStackTrace();
		            runOnUiThread(new Runnable() {
		            	public void run() {
		            		messageText.setText("MalformedURLException Exception : check script url.");
		                      Toast.makeText(MainActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
		                }
		            });
		            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
	        	} catch (Exception e) {
	        		dialog.dismiss();  
	        		e.printStackTrace();
		            
	        		runOnUiThread(new Runnable() {
	        			public void run() {
	        				messageText.setText("Got Exception : see logcat ");
		                    Toast.makeText(MainActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
		                }
		            });
		            Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);  
	        	}
		        	dialog.dismiss();       
		        	return serverResponseCode; 
	        } // End else block 
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_cerrar_sesion) {
			Intent intent = new Intent();
			intent.putExtra("resultado",0);
			setResult(RESULT_OK, intent);
			finish();
		}else if(item.getItemId() == R.id.action_settings){
			Toast.makeText(this, "By Neurus - Aufer Victoriano", Toast.LENGTH_SHORT).show();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    
    	if (requestCode == 1 && resultCode == RESULT_OK) {
            //Bitmap photo = (Bitmap) data.getData().getPath(); 
            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);
            Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
            imageview.setImageBitmap(bitmap);
            messageText.setText("Ruta archivo a cargar:" +imagepath);//Uploading file path
	    	idAccion=1;
	    }else if(requestCode == 2){//camara
	    	
			imageview.setImageBitmap(BitmapFactory.decodeFile(imagepathCam));//imagen decodificada en mapa de bits

			File file = new File(imagepathCam);
			
			if (file.exists()) {//si se realizó la foto
				idAccion=2;
				Toast.makeText(getApplicationContext(), "Se ha realizado la foto", Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(getApplicationContext(), "No se ha realizado la foto", Toast.LENGTH_SHORT).show();
	    }
    }
	public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	
	protected void onSaveInstanceState (Bundle outState){
		outState.putString("imagepathCam", imagepathCam);
		
	}
	@Override
    protected void onRestoreInstanceState(Bundle recEstado) {
         super.onRestoreInstanceState(recEstado);
         if(imagepathCam != null){
        	 imagepathCam  = recEstado.getString("imagepathCam");
         
        	 imageview.setImageBitmap(BitmapFactory.decodeFile(imagepathCam));//imagen decodificada en mapa de bits

 			File file = new File(imagepathCam);
 			
 			if (file.exists()) {//si se realizó la foto
 				Toast.makeText(getApplicationContext(), "Se ha realizado la foto", Toast.LENGTH_SHORT).show();
 			}
 			else
 				Toast.makeText(getApplicationContext(), "No se ha realizado la foto", Toast.LENGTH_SHORT).show();
         }
    }
	class UploaderFoto extends AsyncTask<String, Void, Void>{

		ProgressDialog pDialog;//Dialogo de progreso de subida de la foto
		String miFoto = "";//Guardamos la ruta absoluta de la foto
		String upLoadServerUrl;
		public UploaderFoto(String upLoadServerUri){
			upLoadServerUrl=upLoadServerUri;
		}
		@Override
		protected Void doInBackground(String... params) {
			miFoto = params[0];
			
			try {
				//clase para comunicaciones HTTP
				HttpClient httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				// PARA ENVIAR INFORMACION DE TIPO POST
				//HttpPost httppost = new HttpPost("http://192.168.137.1/proyecto/public/post/guardar/Titulo/camara/Categoria/1/Descripcion/aa");
				HttpPost httppost = new HttpPost(upLoadServerUrl);
				
				File file = new File(miFoto);
				//PARA ENVIAR INFORMACION Y ARCHIVOS
				MultipartEntity mpEntity = new MultipartEntity();
				ContentBody foto = new FileBody(file, "image/jpeg");
				mpEntity.addPart("File", foto);
				httppost.setEntity(mpEntity);
				httpclient.execute(httppost);
				httpclient.getConnectionManager().shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
	        pDialog.setMessage("Subiendo la imagen, espere." );
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setCancelable(true);
	        pDialog.show();
		}
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();
		}
	}
	
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("resultado",1);
		setResult(RESULT_OK, intent);
		finish();
	}

}