package com.neurus.hoytevi;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import android.net.Uri;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private TextView messageText;
	private Calendar c = Calendar.getInstance();
    private Button btnselectpic,btnViewCam,uploadButton;
    private ImageView imageview;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
       
    private String upLoadServerUri = null;
    private String imagepath=null;
    private String imagepathCam=null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//instanciamos los elementos de la vista
		btnselectpic = (Button)findViewById(R.id.btnBuscar);
		btnViewCam = (Button)findViewById(R.id.btnCam);
		uploadButton = (Button)findViewById(R.id.btnSubir);
		messageText  = (TextView)findViewById(R.id.txtNombreArchivo);
		imageview = (ImageView)findViewById(R.id.imageView_pic);

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
				imagepathCam = Environment.getExternalStorageDirectory() + "/imagen"+c.getTime() +".jpg";
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
				dialog = ProgressDialog.show(MainActivity.this, "", "Cargando archivo...", true);//Uploading file
				 messageText.setText("Empezando la carga.....");//uploading started
				 new Thread(new Runnable(){
	                 public void run(){
	                	 uploadFile(imagepath);
	                 }
	             }).start();
				
			}
		});
		upLoadServerUri = "http://192.168.1.2/pruebas/UploadToServer.php";
	}
	//llamado al dar click en boton subir
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
		            conn.setRequestProperty("uploaded_file", fileName);//nuevo valor de nombre 

		            dos = new DataOutputStream(conn.getOutputStream());
		     
		            dos.writeBytes(twoHyphens + boundary + lineEnd); 
		            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
		            		                     + fileName + "\"" + lineEnd);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    
    	if (requestCode == 1 && resultCode == RESULT_OK) {
            //Bitmap photo = (Bitmap) data.getData().getPath(); 
            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);
            Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
            imageview.setImageBitmap(bitmap);
            messageText.setText("Ruta archivo a cargar:" +imagepath);//Uploading file path
	    	
	    }else if(requestCode == 2){//camara
	    	
			imageview.setImageBitmap(BitmapFactory.decodeFile(imagepathCam));//imagen decodificada en mapa de bits

			File file = new File(imagepathCam);
			
			if (file.exists()) {//si se realizó la foto
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
}
