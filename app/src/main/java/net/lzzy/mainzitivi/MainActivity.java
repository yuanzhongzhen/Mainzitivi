package net.lzzy.mainzitivi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int WHAT_COUNTING = 0;
    public static final int WHAT_EXCEPTION = 1;
    public static final int WHAT_COUNT_DONE = 2;
    private TextView tvDisplay;
    private boolean isCounting=false;
    private int seconds=20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvDisplay =findViewById(R.id.tv);
        findViewById(R.id.btn_single).setOnClickListener(this);
        findViewById(R.id.btn_async).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hanler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btn_single){
            conutDown();
        }
        if (v.getId()==R.id.btn_async){
            if (isCounting){
                Toast.makeText(MainActivity.this,"jishizhong.."
                ,Toast.LENGTH_SHORT).show();
            }
            asyncCountDown();
        }
    }
/**
 * 线程时
 *
 * **/
    private static final int CPU_COUNT=Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIEE=Math.max(2,Math.min(CPU_COUNT-1,4));
    private static final int MAX_POOL_SIEE=CPU_COUNT*2+1;
    private static final int KEEP_ALICE_SECONDS=30;
    private static final ThreadFactory THREAD_FACTORY=new ThreadFactory() {
        private final AtomicInteger count =new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"thread #"+count.getAndIncrement());
        }
    };
    private  static final BlockingQueue<Runnable>POOL_QUEUE=new LinkedBlockingQueue<>(128);
    private static ThreadPoolExecutor getExecutor(){
        ThreadPoolExecutor executor=new ThreadPoolExecutor(CORE_POOL_SIEE,MAX_POOL_SIEE,
                KEEP_ALICE_SECONDS, TimeUnit.SECONDS,POOL_QUEUE,THREAD_FACTORY);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

/**异步**/
    private void asyncCountDown() {
    new Thread(new Runnable() {
        @Override
        public void run() {
            isCounting=true;
            while (seconds>=0){
                try {
                  Thread.sleep(100);
                  seconds--;
                  Message msg=hanler.obtainMessage();
                  msg.what = WHAT_COUNTING;
                  msg.arg1=seconds;
                  hanler.handleMessage(msg);

                } catch (InterruptedException e) {
                    hanler.sendMessage(hanler.obtainMessage(WHAT_EXCEPTION,e.getMessage()));
                }
                hanler.sendEmptyMessage(WHAT_COUNT_DONE);
            }
        }
    }).start();

    }

    private ConutHanler hanler=new ConutHanler(this);
    private static class ConutHanler extends Handler{
        WeakReference<MainActivity>tageActivity;
        /**构造器**/
        ConutHanler(MainActivity activity){
            tageActivity=new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            MainActivity activity=tageActivity.get();
            switch (msg.what){
                case WHAT_COUNTING:
                    String text="计时剩余"+msg.arg1+"秒";
                    activity.tvDisplay.setText(text);
                    break;
                case WHAT_COUNT_DONE:
                    activity.tvDisplay.setText("及时完成");
                    activity.seconds=20;
                    activity.isCounting=false;
                    break;
                case WHAT_EXCEPTION:
                    Toast.makeText(activity,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    activity.seconds=20;
                    activity.isCounting=false;
                    break;
                    default:
                        break;
            }
        }

    }

             /**同步**/
                private void conutDown() {
               while (seconds>=0){
                   String text="计时剩余"+seconds+"秒";
                   tvDisplay.setText(text);
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   seconds--;
               }
               seconds=20;
               tvDisplay.setText("计时完成");
                }

}
