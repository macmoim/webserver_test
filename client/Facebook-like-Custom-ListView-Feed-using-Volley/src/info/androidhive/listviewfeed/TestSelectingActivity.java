package info.androidhive.listviewfeed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestSelectingActivity extends Activity {
    private Button mDownloadBtn;
    private Button mUploadBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_main);
        
        mDownloadBtn = (Button) findViewById(R.id.download);
        mDownloadBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(TestSelectingActivity.this, MainActivity.class));
            }
        });
        
        mUploadBtn = (Button) findViewById(R.id.upload);
        mUploadBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(TestSelectingActivity.this, MultiVolleyTest.class));
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (mDownloadBtn != null) {
            mDownloadBtn.setOnClickListener(null);
            mDownloadBtn = null;
        }
        if (mUploadBtn != null) {
            mUploadBtn.setOnClickListener(null);
            mUploadBtn = null;
        }
        super.onDestroy();
    }
    
}
