package com.uber.test.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dell on 7/22/2018.
 */
public abstract class UberDataParser implements Runnable {
    private static final String TAG = UberDataParser.class.getSimpleName();
    protected Gson mGson;
    protected Context mContext;
    protected ParserState mParserState;
    private Handler mHandler;
    private UberJsonParserResponse mMvmJsonParserResponse;
    protected String mJsonString;
    protected Reader mReader;

    private static ExecutorService mParsingThreadPool = Executors.newCachedThreadPool();

    private Handler.Callback callback = new Handler.Callback(){

        @Override
        public boolean handleMessage(Message msg) {
            onPostExecute(msg.obj);
            return true;
        }
    };

    public UberDataParser(Context context, UberJsonParserResponse response){
        mGson = new Gson();
        mContext = context;
        mMvmJsonParserResponse = response;
    }

    public UberDataParser(Context context, String jsonString, UberJsonParserResponse response){
        this(context,response);
        mJsonString = jsonString;
        mReader = new StringReader(jsonString);
    }

    // used Object in place of InfoBean
    protected Object parseSimpleJson(JsonObject jsonElement, Class<? extends Object> cls){
        return mGson.fromJson(jsonElement.toString(), cls);
    }

    protected enum ParserState{
        EVENT_START,
        EVENT_SUCCESS,
        EVENT_SERVER_ERROR,
        EVENT_EXCEPTION,
        EVENT_FINISH
    }

    public interface UberJsonParserResponse {
        public void onJsonSuccess(Object bean);

        public  void onJsonError(Object error);

        public void onException(Exception exception);
    }


    protected abstract Object parse(JsonObject jsonObject);

    @Override
    public void run() {
        try{
            mParserState = ParserState.EVENT_START;
            JsonElement jsonElement = new JsonParser().parse(mReader);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Object object = parse(jsonObject);
            if(object instanceof Exception){
                Exception exception = ( Exception) object;
                throw  exception;
            }
            mParserState = ParserState.EVENT_FINISH;
            Message msg = new Message();
            msg.obj = object;
            mHandler.sendMessage(msg);
        }catch( Exception exception){
            mParserState = ParserState.EVENT_SERVER_ERROR;
            Message msg = new Message();
            msg.obj = exception;
            mHandler.sendMessage(msg);
        }finally{
            try{
                if(mReader != null)
                    mReader.close();
            }catch(IOException exception){
                mParserState = ParserState.EVENT_EXCEPTION;
                Message msg = new Message();
                msg.obj = exception;
                mHandler.sendMessage(msg);
            }
        }

    }

    public boolean execute(){
        mHandler = new Handler(callback);
        if(mParsingThreadPool.isShutdown())
            mParsingThreadPool = Executors.newCachedThreadPool();
        mParsingThreadPool.execute(this);
        return true;
    }

    protected void onPostExecute(Object o) {
        switch (mParserState){
            case EVENT_FINISH:
            case EVENT_SUCCESS:
                mMvmJsonParserResponse.onJsonSuccess(o);
                break;
            case EVENT_EXCEPTION:
                mMvmJsonParserResponse.onException((Exception)o);
                break;
            case EVENT_SERVER_ERROR:
                mMvmJsonParserResponse.onJsonError(o);

        }
    }
}
