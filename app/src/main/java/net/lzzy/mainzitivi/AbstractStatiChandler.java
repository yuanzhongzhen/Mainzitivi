package net.lzzy.mainzitivi;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by lzzy_gxy on 2019/4/12.
 * Description:
 */
public abstract class AbstractStatiChandler<T> extends Handler {
    private final WeakReference<T>context;

    public AbstractStatiChandler(T context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        T t=context.get();
        handleMessage(msg,t);
    }

    /**
     *处理消息业务逻辑
     *
     * **/
    public abstract void handleMessage(Message mags,T t);
}
