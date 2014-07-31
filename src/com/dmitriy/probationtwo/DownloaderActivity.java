package com.dmitriy.probationtwo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class DownloaderActivity extends ActionBarActivity {
	
	private ProgressBar progressBar;
	private TextView status;
	private Button buttonDownload;
	private Button buttonOpen;
	
	private class AsyncImagesLoader extends AsyncTask<String, Double, Boolean> {
		
		private HttpURLConnection urlConnection;
		private double totalSize;
		private double downloadedSize;
		private byte[] buffer;
		
		@Override
		protected Boolean doInBackground(String... url) {
			
			try {
				urlConnection = (HttpURLConnection) new URL(url[0]).openConnection();
				urlConnection.connect();
				totalSize = urlConnection.getContentLength();
				
			    InputStream in = urlConnection.getInputStream();
			    File sDCardRoot = Environment.getExternalStorageDirectory(); 
	            File file = new File(sDCardRoot,"downloaded_file.png");
	            FileOutputStream out = new FileOutputStream(file);
	            
			    downloadedSize = 0;
			    buffer = new byte[1024];
			    double bufferLength;
			    while ((bufferLength = in.read(buffer)) > 0) {
			    	out.write(buffer, 0, (int)bufferLength);
			    	
			    	downloadedSize += bufferLength;
			        super.publishProgress(downloadedSize, totalSize);
			    }
			    out.close();
				
				
			} catch (IOException e) {
				e.printStackTrace();
				return Boolean.FALSE;
			}
			
			return Boolean.TRUE;
		}

		@Override
		protected void onProgressUpdate(Double ... status) {
			progressBar.setProgress((int) ((status[0] / status[1]) * 100));
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				status.setText(R.string.downloaded);
				buttonOpen.setVisibility(Button.VISIBLE);
				Toast.makeText(DownloaderActivity.this, "Loading finished", Toast.LENGTH_SHORT)
				.show();
			}else{
				status.setText(R.string.idle);
				buttonDownload.setVisibility(Button.VISIBLE);
				progressBar.setVisibility(ProgressBar.INVISIBLE);
				Toast.makeText(DownloaderActivity.this,
						"Impossible to load image",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_downloader);
		
		progressBar = (ProgressBar)findViewById(R.id.progress_bar_download);
		status = (TextView)findViewById(R.id.text_view_progress);
		buttonDownload = (Button) findViewById(R.id.button_download);
		buttonOpen = (Button) findViewById(R.id.button_open);


        buttonDownload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	buttonDownload.setVisibility(Button.INVISIBLE);
            	progressBar.setVisibility(ProgressBar.VISIBLE);
            	status.setText(R.string.downloading);
            	
            	AsyncImagesLoader loader = new AsyncImagesLoader();
            	loader.execute(getResources().getString(R.string.picture_url)) ;
            }
        });
        
        
        buttonOpen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + new File(Environment.getExternalStorageDirectory(),"downloaded_file.png").getAbsolutePath()),"image/*");
                startActivity(intent);
                finish();
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.downloader, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */

}
