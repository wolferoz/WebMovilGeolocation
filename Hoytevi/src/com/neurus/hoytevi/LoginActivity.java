package com.neurus.hoytevi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends Activity {
	
	private TextView txtViewAlias;
	private TextView txtViewPass;
	private Button btnIniciarSecion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		btnIniciarSecion = (Button) findViewById(R.id.btnLoginInitSesion);
		txtViewAlias = (TextView) findViewById(R.id.txtLoginAlias);
		txtViewPass = (TextView) findViewById(R.id.txtLoginPasword);
		
		btnIniciarSecion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
			}
		});
		
	}
}